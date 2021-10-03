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

package com.io7m.cardant.client.transfer.vanilla;

import com.io7m.cardant.client.transfer.api.CATransferDownloadWaiting;
import com.io7m.cardant.client.transfer.api.CATransferServiceType;
import com.io7m.cardant.client.transfer.api.CATransferStatusType;
import com.io7m.cardant.client.transfer.vanilla.internal.CASlowInputStream;
import com.io7m.cardant.client.transfer.vanilla.internal.CATransferException;
import com.io7m.cardant.client.transfer.vanilla.internal.CATransferOperation;
import com.io7m.cardant.client.transfer.vanilla.internal.CATransferStrings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Flow;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class CATransferService implements CATransferServiceType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CATransferService.class);

  private final ScheduledExecutorService executor;
  private final CATransferStrings strings;
  private final Path outputDirectory;
  private final SubmissionPublisher<CATransferStatusType> status;
  private final AtomicBoolean slowTransfers;
  private final ConcurrentHashMap<UUID, CompletableFuture<?>> transfers;
  private final Clock clock;

  private CATransferService(
    final Clock inClock,
    final ScheduledExecutorService inExecutor,
    final CATransferStrings inStrings,
    final Path inOutputDirectory)
  {
    this.clock =
      Objects.requireNonNull(inClock, "inClock");
    this.transfers =
      new ConcurrentHashMap<>();
    this.slowTransfers =
      new AtomicBoolean(false);
    this.executor =
      Objects.requireNonNull(inExecutor, "executor");
    this.strings =
      Objects.requireNonNull(inStrings, "inStrings");
    this.outputDirectory =
      Objects.requireNonNull(inOutputDirectory, "outputDirectory");
    this.status =
      new SubmissionPublisher<>();
  }

  public static CATransferServiceType create(
    final Clock clock,
    final ScheduledExecutorService executor,
    final Duration cleanupFrequency,
    final Locale locale,
    final Path outputDirectory)
  {
    try {
      LOG.debug("transfer download directory: {}", outputDirectory);

      if (cleanupFrequency.compareTo(Duration.ofSeconds(1L)) < 0) {
        throw new IllegalStateException(
          "Cannot clean up storage less frequently than one second");
      }

      final var service =
        new CATransferService(
          clock,
          executor,
          new CATransferStrings(locale),
          outputDirectory
        );

      executor.scheduleAtFixedRate(
        service::cleanUpOldFiles,
        cleanupFrequency.toSeconds(),
        cleanupFrequency.toSeconds(),
        TimeUnit.SECONDS);
      return service;
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private void cleanUpOldFiles()
  {
    LOG.debug("cleaning up old files");

    try {
      try (var stream = Files.list(this.outputDirectory)) {
        stream
          .filter(p -> !p.toString().endsWith(".tmp"))
          .filter(this::fileIsOld)
          .forEach(path -> {
            try {
              LOG.debug("delete old {}", path);
              Files.deleteIfExists(path);
            } catch (final IOException e) {
              // Not a problem
            }
          });
      }
    } catch (final IOException e) {
      // Not a problem
    }
  }

  private boolean fileIsOld(
    final Path p)
  {
    try {
      final var timeNow =
        Instant.now(this.clock);
      final var timeFile =
        Files.getLastModifiedTime(p).toInstant();
      final var timeThen =
        timeNow.minusSeconds(30L);

      return timeFile.isBefore(timeThen);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public Flow.Publisher<CATransferStatusType> status()
  {
    return this.status;
  }

  @Override
  public CompletableFuture<Path> transfer(
    final InputStream stream,
    final String title,
    final long expectedSize,
    final String hashAlgorithm,
    final String hashValue)
  {
    final var id =
      UUID.randomUUID();
    final var outputFile =
      this.outputDirectory.resolve(id.toString());

    return this.transferToWithId(
      id,
      stream,
      title,
      expectedSize,
      hashAlgorithm,
      hashValue,
      outputFile
    );
  }

  @Override
  public CompletableFuture<Path> transferTo(
    final InputStream stream,
    final String title,
    final long expectedSize,
    final String hashAlgorithm,
    final String hashValue,
    final Path file)
  {
    return this.transferToWithId(
      UUID.randomUUID(),
      stream,
      title,
      expectedSize,
      hashAlgorithm,
      hashValue,
      file);
  }

  private CompletableFuture<Path> transferToWithId(
    final UUID id,
    final InputStream stream,
    final String title,
    final long expectedSize,
    final String hashAlgorithm,
    final String hashValue,
    final Path file)
  {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(stream, "stream");
    Objects.requireNonNull(title, "title");
    Objects.requireNonNull(hashAlgorithm, "hashAlgorithm");
    Objects.requireNonNull(hashValue, "hashValue");
    Objects.requireNonNull(file, "file");

    final var future = new CompletableFuture<Path>();
    this.transfers.put(id, future);

    this.status.submit(
      new CATransferDownloadWaiting(id, title, expectedSize, 0L)
    );

    final InputStream inputStream;
    if (this.isSlowTransfers()) {
      inputStream = new CASlowInputStream(stream);
    } else {
      inputStream = stream;
    }

    this.executor.execute(() -> {
      final var transfer =
        new CATransferOperation(
          this.clock,
          future,
          this.status,
          this.strings,
          file,
          inputStream,
          id,
          title,
          expectedSize,
          hashAlgorithm,
          hashValue
        );

      try {
        future.complete(transfer.execute());
        this.transfers.remove(id);
      } catch (final CATransferException e) {
        future.completeExceptionally(e);
        this.transfers.remove(id);
      } catch (final CancellationException e) {
        future.cancel(true);
        this.transfers.remove(id);
      }
    });
    return future;
  }

  @Override
  public boolean isSlowTransfers()
  {
    return this.slowTransfers.get();
  }

  @Override
  public void setSlowTransfers(
    final boolean slow)
  {
    this.slowTransfers.set(slow);
  }

  @Override
  public void cancel(
    final UUID id)
  {
    Objects.requireNonNull(id, "id");

    final var existing = this.transfers.get(id);
    if (existing != null) {
      existing.cancel(true);
    }
  }
}
