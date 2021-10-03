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

package com.io7m.cardant.tests;

import com.io7m.cardant.client.transfer.api.CATransferDownloadCancelled;
import com.io7m.cardant.client.transfer.api.CATransferDownloadCompleted;
import com.io7m.cardant.client.transfer.api.CATransferDownloadFailed;
import com.io7m.cardant.client.transfer.api.CATransferDownloadWaiting;
import com.io7m.cardant.client.transfer.api.CATransferDownloading;
import com.io7m.cardant.client.transfer.api.CATransferServiceType;
import com.io7m.cardant.client.transfer.api.CATransferStatusType;
import com.io7m.cardant.client.transfer.vanilla.CATransferService;
import com.io7m.cardant.client.transfer.vanilla.internal.CATransferException;
import com.io7m.cardant.gui.internal.CAPerpetualSubscriber;
import org.apache.commons.io.input.BrokenInputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class CATransferServiceTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CATransferServiceTest.class);

  private static final String HASH_ALGORITHM =
    "SHA-256";
  private static final String HASH_VALUE =
    "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

  private Path directory;
  private ScheduledExecutorService executor;
  private CATransferServiceType transfers;
  private ConcurrentLinkedDeque<CATransferStatusType> events;
  private CAPerpetualSubscriber<CATransferStatusType> subscriber;

  @BeforeEach
  public void setup()
    throws IOException
  {
    this.events =
      new ConcurrentLinkedDeque<>();
    this.directory =
      CATestDirectories.createTempDirectory();
    this.executor =
      Executors.newScheduledThreadPool(10);
    this.transfers =
      CATransferService.create(
        Clock.systemUTC(),
        this.executor,
        Duration.ofMinutes(30L),
        Locale.ENGLISH,
        this.directory
      );

    this.subscriber =
      new CAPerpetualSubscriber<>(this::logEvent);
    this.transfers.status()
      .subscribe(this.subscriber);
  }

  private void logEvent(
    final CATransferStatusType e)
  {
    LOG.debug("event: {}", e);
    this.events.add(e);
  }

  @AfterEach
  public void tearDown()
    throws IOException, InterruptedException
  {
    CATestDirectories.deleteDirectory(this.directory);
    this.executor.shutdown();
    this.executor.awaitTermination(10L, SECONDS);
  }

  /**
   * A broken stream fails the transfer.
   *
   * @throws Exception On errors
   */

  @Test
  public void testBrokenStream()
    throws Exception
  {
    final var ex =
      Assertions.assertThrows(ExecutionException.class, () -> {
        this.transfers.transfer(
          new BrokenInputStream(),
          "Title",
          1000L,
          HASH_ALGORITHM,
          HASH_VALUE
        ).get(10L, SECONDS);
      });

    assertTrue(ex.getCause() instanceof CATransferException);
    assertTrue(ex.getCause().getMessage().contains("Broken"));

    assertTimeout(Duration.ofSeconds(10L), () -> {
      this.waitUntilEventsReceived(2);

      {
        final var e = this.events.removeFirst();
        assertTrue(e instanceof CATransferDownloadWaiting);
      }

      {
        final var e = this.events.removeFirst();
        assertTrue(e instanceof CATransferDownloadFailed);
      }

      assertEquals(0, this.events.size());
    });
  }

  /**
   * A trivial stream works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTrivialStream()
    throws Exception
  {
    final var path =
      this.transfers.transfer(
        new ByteArrayInputStream("HELLO".getBytes(UTF_8)),
        "Title",
        5L,
        HASH_ALGORITHM,
        "3733cd977ff8eb18b987357e22ced99f46097f31ecb239e878ae63760e83e4d5"
      ).get(10L, SECONDS);

    Assertions.assertArrayEquals(
      "HELLO".getBytes(UTF_8),
      Files.readAllBytes(path)
    );

    assertTimeout(Duration.ofSeconds(10L), () -> {
      this.waitUntilEventsReceived(3);

      {
        final var e = this.events.removeFirst();
        assertTrue(e instanceof CATransferDownloadWaiting);
      }

      {
        final var e = this.events.removeFirst();
        assertTrue(e instanceof CATransferDownloading);
      }

      {
        final var e = this.events.removeFirst();
        assertTrue(e instanceof CATransferDownloadCompleted);
      }

      assertEquals(0, this.events.size());
    });
  }

  /**
   * A trivial stream to a specific file works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTrivialStreamSpecificFile()
    throws Exception
  {
    final var targetPath =
      this.directory.resolve("output.txt");

    this.transfers.transferTo(
      new ByteArrayInputStream("HELLO".getBytes(UTF_8)),
      "Title",
      5L,
      HASH_ALGORITHM,
      "3733cd977ff8eb18b987357e22ced99f46097f31ecb239e878ae63760e83e4d5",
      targetPath
    ).get(10L, SECONDS);

    Assertions.assertArrayEquals(
      "HELLO".getBytes(UTF_8),
      Files.readAllBytes(targetPath)
    );

    assertTimeout(Duration.ofSeconds(10L), () -> {
      this.waitUntilEventsReceived(3);

      {
        final var e = this.events.removeFirst();
        assertTrue(e instanceof CATransferDownloadWaiting);
      }

      {
        final var e = this.events.removeFirst();
        assertTrue(e instanceof CATransferDownloading);
      }

      {
        final var e = this.events.removeFirst();
        assertTrue(e instanceof CATransferDownloadCompleted);
      }

      assertEquals(0, this.events.size());
    });
  }

  /**
   * A trivial stream with slow transfers enabled works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTrivialStreamSlow()
    throws Exception
  {
    this.transfers.setSlowTransfers(true);
    assertTrue(this.transfers.isSlowTransfers());

    final var path =
      this.transfers.transfer(
        new ByteArrayInputStream("HELLO".getBytes(UTF_8)),
        "Title",
        5L,
        HASH_ALGORITHM,
        "3733cd977ff8eb18b987357e22ced99f46097f31ecb239e878ae63760e83e4d5"
      ).get(10L, SECONDS);

    Assertions.assertArrayEquals(
      "HELLO".getBytes(UTF_8),
      Files.readAllBytes(path)
    );

    assertTimeout(Duration.ofSeconds(10L), () -> {
      this.waitUntilEventsReceived(1);
      final var e = this.events.removeFirst();
      assertTrue(e instanceof CATransferDownloadWaiting);
    });

    assertTimeout(Duration.ofSeconds(10L), () -> {
      while (true) {
        this.waitUntilEventsReceived(1);
        final var e = this.events.removeFirst();
        if (e instanceof CATransferDownloadCompleted) {
          break;
        }
        assertTrue(e instanceof CATransferDownloading);
      }
    });
  }

  /**
   * Cancelling a transfer works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testCancel()
    throws Exception
  {
    this.transfers.setSlowTransfers(true);
    assertTrue(this.transfers.isSlowTransfers());

    final var future =
      this.transfers.transfer(
        new ByteArrayInputStream("HELLO".getBytes(UTF_8)),
        "Title",
        5L,
        HASH_ALGORITHM,
        "3733cd977ff8eb18b987357e22ced99f46097f31ecb239e878ae63760e83e4d5"
      );

    assertTimeout(Duration.ofSeconds(10L), () -> {
      this.waitUntilEventsReceived(1);
      final var e = this.events.removeFirst();
      this.transfers.cancel(e.id());
    });

    assertThrows(CancellationException.class, () -> {
      future.get(10L, SECONDS);
    });

    assertTimeout(Duration.ofSeconds(10L), () -> {
      this.waitUntilEventsReceived(1);

      {
        final var e = this.events.removeFirst();
        assertTrue(e instanceof CATransferDownloadCancelled);
      }

      assertEquals(0, this.events.size());
    });
  }

  /**
   * A trivial stream with the wrong hash fails the transfer.
   *
   * @throws Exception On errors
   */

  @Test
  public void testIncorrectHashStream()
    throws Exception
  {
    final var ex =
      Assertions.assertThrows(ExecutionException.class, () -> {
        this.transfers.transfer(
          new ByteArrayInputStream("HELLO".getBytes(UTF_8)),
          "Title",
          1000L,
          HASH_ALGORITHM,
          "wrong"
        ).get(10L, SECONDS);
      });

    assertTrue(ex.getCause() instanceof CATransferException);
    assertTrue(ex.getCause().getMessage().contains("expected hash value"));

    assertTimeout(Duration.ofSeconds(10L), () -> {
      this.waitUntilEventsReceived(3);

      {
        final var e = this.events.removeFirst();
        assertTrue(e instanceof CATransferDownloadWaiting);
      }

      {
        final var e = this.events.removeFirst();
        assertTrue(e instanceof CATransferDownloading);
      }

      {
        final var e = this.events.removeFirst();
        assertTrue(e instanceof CATransferDownloadFailed);
      }

      assertEquals(0, this.events.size());
    });
  }

  /**
   * A trivial stream with an unrecognized hash algorithm fails the transfer.
   *
   * @throws Exception On errors
   */

  @Test
  public void testUnrecognizedHashStream()
    throws Exception
  {
    final var ex =
      Assertions.assertThrows(ExecutionException.class, () -> {
        this.transfers.transfer(
          new ByteArrayInputStream("HELLO".getBytes(UTF_8)),
          "Title",
          1000L,
          "What?",
          "wrong"
        ).get(10L, SECONDS);
      });

    assertTrue(ex.getCause() instanceof CATransferException);
    assertTrue(ex.getCause().getMessage().contains("MessageDigest not available"));

    assertTimeout(Duration.ofSeconds(10L), () -> {
      this.waitUntilEventsReceived(2);

      {
        final var e = this.events.removeFirst();
        assertTrue(e instanceof CATransferDownloadWaiting);
      }

      {
        final var e = this.events.removeFirst();
        assertTrue(e instanceof CATransferDownloadFailed);
      }

      assertEquals(0, this.events.size());
    });
  }

  private void waitUntilEventsReceived(
    final int count)
    throws InterruptedException, TimeoutException
  {
    final var timeStart = Instant.now();
    final var timeEnd = timeStart.plus(Duration.ofSeconds(10L));
    while (this.events.size() < count) {
      Thread.sleep(10L);
      if (Instant.now().isAfter(timeEnd)) {
        throw new TimeoutException("Waited for more than ten seconds!");
      }
    }
  }

  /**
   * Cancelling a transfer works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testCancelTransfer()
    throws Exception
  {
    final var path =
      this.transfers.transfer(
        new ByteArrayInputStream("HELLO".getBytes(UTF_8)),
        "Title",
        5L,
        HASH_ALGORITHM,
        "3733cd977ff8eb18b987357e22ced99f46097f31ecb239e878ae63760e83e4d5"
      );

    path.cancel(true);

    assertTimeout(Duration.ofSeconds(10L), () -> {
      this.waitUntilEventsReceived(2);

      {
        final var e = this.events.removeFirst();
        assertTrue(e instanceof CATransferDownloadWaiting);
      }

      {
        final var e = this.events.removeFirst();
        assertTrue(e instanceof CATransferDownloadCancelled);
      }

      assertEquals(0, this.events.size());
    });
  }

  /**
   * Cancelling a transfer works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testCleanOldFiles()
    throws Exception
  {
    final var testDirectory =
      CATestDirectories.createTempDirectory();

    final var p0 = testDirectory.resolve(UUID.randomUUID().toString());
    Files.writeString(p0, "HELLO");
    final var p1 = testDirectory.resolve(UUID.randomUUID().toString());
    Files.writeString(p1, "HELLO");
    final var p2 = testDirectory.resolve(UUID.randomUUID().toString());
    Files.writeString(p2, "HELLO");

    final var timeThen = FileTime.from(Instant.now().minusSeconds(86400L));
    Files.setLastModifiedTime(p0, timeThen);
    Files.setLastModifiedTime(p1, timeThen);
    Files.setLastModifiedTime(p2, timeThen);

    final var testExecutor =
      Executors.newScheduledThreadPool(10);

    final var testTransfers =
      CATransferService.create(
        Clock.systemUTC(),
        testExecutor,
        Duration.ofSeconds(1L),
        Locale.ENGLISH,
        testDirectory
      );

    assertTimeout(Duration.ofSeconds(10L), () -> {
      while (true) {
        if (!Files.exists(p0)) {
          break;
        }
      }
    });

    assertFalse(Files.exists(p0));
    assertFalse(Files.exists(p1));
    assertFalse(Files.exists(p2));

    testExecutor.shutdown();
  }
}
