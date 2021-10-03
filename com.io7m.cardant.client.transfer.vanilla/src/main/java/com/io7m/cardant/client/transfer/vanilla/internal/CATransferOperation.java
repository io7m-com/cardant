/*
 * Copyright © 2021 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.cardant.client.transfer.vanilla.internal;

import com.io7m.cardant.client.transfer.api.CATransferDownloadCancelled;
import com.io7m.cardant.client.transfer.api.CATransferDownloadCompleted;
import com.io7m.cardant.client.transfer.api.CATransferDownloadFailed;
import com.io7m.cardant.client.transfer.api.CATransferDownloading;
import com.io7m.cardant.client.transfer.api.CATransferStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.SubmissionPublisher;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.Locale.ROOT;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;

public final class CATransferOperation
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CATransferOperation.class);

  private final CompletableFuture<?> future;
  private final SubmissionPublisher<CATransferStatusType> status;
  private final CATransferStrings strings;
  private final String title;
  private final long expectedSize;
  private final String hashAlgorithm;
  private final String hashValue;
  private final InputStream inputStream;
  private final UUID id;
  private final Path outputFile;
  private final Path outputFileTemporary;
  private final CAOctetsPerSecond octetsPerSecond;
  private long receivedSize;
  private MessageDigest messageDigest;

  public CATransferOperation(
    final Clock clock,
    final CompletableFuture<?> inFuture,
    final SubmissionPublisher<CATransferStatusType> inStatus,
    final CATransferStrings inStrings,
    final Path inOutputFile,
    final InputStream inInputStream,
    final UUID uuid,
    final String inTitle,
    final long inExpectedSize,
    final String inHashAlgorithm,
    final String inHashValue)
  {
    this.future = 
      Objects.requireNonNull(inFuture, "future");
    this.status =
      Objects.requireNonNull(inStatus, "status");
    this.strings =
      Objects.requireNonNull(inStrings, "inStrings");
    this.outputFile =
      Objects.requireNonNull(inOutputFile, "outputFile");
    this.inputStream =
      Objects.requireNonNull(inInputStream, "inputStream");
    this.id =
      Objects.requireNonNull(uuid, "uuid");
    this.title =
      Objects.requireNonNull(inTitle, "title");
    this.expectedSize =
      inExpectedSize;
    this.receivedSize =
      0L;
    this.hashAlgorithm =
      Objects.requireNonNull(inHashAlgorithm, "hashAlgorithm");
    this.hashValue =
      Objects.requireNonNull(inHashValue, "hashValue");
    this.outputFileTemporary =
      this.outputFile.resolveSibling(this.outputFile.getFileName() + ".tmp");
    this.octetsPerSecond =
      new CAOctetsPerSecond(clock);
  }

  private static Optional<Duration> expectedRemainingTime(
    final long receivedSize,
    final long expectedSize,
    final long averageRate)
  {
    if (averageRate == 0L) {
      return Optional.empty();
    }
    final var remaining = expectedSize - receivedSize;
    return Optional.of(Duration.ofSeconds(remaining / averageRate));
  }

  public Path execute()
    throws CATransferException, CancellationException
  {
    try {
      final var timeNow = Instant.now();

      if (this.inputStream instanceof CASlowInputStream) {
        try {
          Thread.sleep(2_000L);
        } catch (final InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }

      this.messageDigest = MessageDigest.getInstance(this.hashAlgorithm);
      this.doCopy();
      this.doCheck();
      this.doRename();

      this.status.submit(
        new CATransferDownloadCompleted(
          this.id,
          this.title,
          this.expectedSize,
          this.receivedSize,
          Duration.between(timeNow, Instant.now())
        )
      );

      LOG.debug("transfer: saved to {}", this.outputFile);
      return this.outputFile;
    } catch (final NoSuchAlgorithmException e) {
      this.cleanUp();
      throw this.noSuchAlgorithm(e);
    } catch (final CopyException e) {
      this.cleanUp();
      throw this.copyException(e);
    } catch (final CheckException e) {
      this.cleanUp();
      throw this.checkException(e);
    } catch (final IOException e) {
      this.cleanUp();
      throw this.renameException(e);
    } catch (final CancellationException e) {

      this.status.submit(
        new CATransferDownloadCancelled(
          this.id,
          this.title,
          this.expectedSize,
          this.receivedSize
        )
      );

      this.cleanUp();
      throw e;
    }
  }

  private void doRename()
    throws IOException
  {
    Files.move(
      this.outputFileTemporary,
      this.outputFile,
      ATOMIC_MOVE,
      REPLACE_EXISTING
    );
  }

  private void cleanUp()
  {
    try {
      Files.deleteIfExists(this.outputFileTemporary);
    } catch (final IOException ex) {
      // Don't care.
    }
  }

  private CATransferException renameException(
    final IOException e)
  {
    final var errorAttributes =
      ofEntries(
        entry(this.strings.format("hashAlgorithm"), this.hashAlgorithm)
      );

    final var errorMessage =
      this.strings.format("renameFailed");

    this.status.submit(
      new CATransferDownloadFailed(
        this.id,
        this.title,
        this.expectedSize,
        this.receivedSize,
        errorAttributes,
        errorMessage
      )
    );

    return new CATransferException(errorMessage, errorAttributes);
  }


  private CATransferException checkException(
    final CheckException e)
  {
    final var errorAttributes =
      ofEntries(
        entry(this.strings.format("expectedHash"), e.expected),
        entry(this.strings.format("receivedHash"), e.received),
        entry(this.strings.format("hashAlgorithm"), this.hashAlgorithm)
      );

    final var errorMessage =
      this.strings.format("hashCheckFailed");

    this.status.submit(
      new CATransferDownloadFailed(
        this.id,
        this.title,
        this.expectedSize,
        this.receivedSize,
        errorAttributes,
        errorMessage
      )
    );

    return new CATransferException(errorMessage, errorAttributes);
  }

  private CATransferException copyException(
    final CopyException e)
  {
    this.status.submit(
      new CATransferDownloadFailed(
        this.id,
        this.title,
        this.expectedSize,
        this.receivedSize,
        Map.of(),
        e.getMessage()
      )
    );

    return new CATransferException(e.getMessage(), Map.of());
  }

  private CATransferException noSuchAlgorithm(
    final NoSuchAlgorithmException e)
  {
    final var errorAttributes =
      ofEntries(
        entry(this.strings.format("hashAlgorithm"), this.hashAlgorithm)
      );

    this.status.submit(
      new CATransferDownloadFailed(
        this.id,
        this.title,
        this.expectedSize,
        this.receivedSize,
        errorAttributes,
        e.getMessage()
      )
    );

    return new CATransferException(e.getMessage(), errorAttributes);
  }

  private void doCopy()
    throws CopyException
  {
    try {
      this.checkCancelled();

      final var parent = this.outputFileTemporary.getParent();
      if (parent != null) {
        Files.createDirectories(parent);
      }

      var eventTimeNext = Instant.now();

      try (var outputStream =
             Files.newOutputStream(
               this.outputFileTemporary, CREATE, TRUNCATE_EXISTING, WRITE)) {
        try (var digestOutput =
               new DigestOutputStream(outputStream, this.messageDigest)) {

          final var buffer = new byte[8192];
          var receivedSecond = 0L;

          while (true) {
            this.checkCancelled();

            final var r = this.inputStream.read(buffer);
            if (r < 0) {
              break;
            }

            this.receivedSize += Integer.toUnsignedLong(r);
            receivedSecond += Integer.toUnsignedLong(r);
            digestOutput.write(buffer, 0, r);

            if (this.octetsPerSecond.isReadyForMore()){
              this.octetsPerSecond.add(receivedSecond);
              receivedSecond = 0L;
            }

            final var eventTimeNow = Instant.now();
            if (eventTimeNow.isAfter(eventTimeNext)) {
              final var secondAverage =
                (long) this.octetsPerSecond.average();

              this.status.submit(
                new CATransferDownloading(
                  this.id,
                  this.title,
                  this.expectedSize,
                  this.receivedSize,
                  secondAverage,
                  expectedRemainingTime(
                    this.receivedSize, this.expectedSize, secondAverage)
                )
              );
              eventTimeNext = eventTimeNow.plusMillis(100L);
            }
          }

          digestOutput.flush();
        }
      }
    } catch (final IOException e) {
      throw new CopyException(e);
    }
  }

  private void checkCancelled()
  {
    if (this.future.isCancelled()) {
      throw new CancellationException();
    }
  }

  private void doCheck()
    throws CheckException
  {
    final var received =
      HexFormat.of()
        .formatHex(this.messageDigest.digest())
        .toUpperCase(ROOT);

    final var expected =
      this.hashValue.toUpperCase(ROOT);

    if (!received.equals(expected)) {
      throw new CheckException(expected, received);
    }
  }

  private static final class CopyException extends Exception
  {
    private CopyException(
      final IOException e)
    {
      super(e);
    }
  }

  private static final class CheckException extends Exception
  {
    private final String expected;
    private final String received;

    private CheckException(
      final String inExpected,
      final String inReceived)
    {
      this.expected =
        Objects.requireNonNull(inExpected, "expected");
      this.received =
        Objects.requireNonNull(inReceived, "received");
    }
  }
}
