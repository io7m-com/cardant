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

package com.io7m.cardant.gui.internal.model;

import com.io7m.cardant.client.transfer.api.CATransferDownloadCancelled;
import com.io7m.cardant.client.transfer.api.CATransferDownloadCompleted;
import com.io7m.cardant.client.transfer.api.CATransferDownloadFailed;
import com.io7m.cardant.client.transfer.api.CATransferDownloadWaiting;
import com.io7m.cardant.client.transfer.api.CATransferDownloading;
import com.io7m.cardant.client.transfer.api.CATransferStatusType;
import com.io7m.cardant.gui.internal.CAMainStrings;
import com.io7m.cardant.gui.internal.CAObservables;
import com.io7m.cardant.gui.internal.CASizeFormatter;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class CATransferMutable
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CATransferMutable.class);

  private final Instant started;
  private final UUID id;
  private final String title;
  private final long expectedSize;
  private final SimpleLongProperty receivedSize;
  private final SimpleLongProperty receivingRate;
  private final SimpleObjectProperty<Optional<Duration>> timeRemaining;
  private final CAMainStrings strings;
  private final ObservableValue<String> timeRemainingTextProperty;
  private final ObservableValue<String> receivingText;
  private final ObservableValue<String> receivedText;
  private final SimpleDoubleProperty progressProperty;
  private final SimpleObjectProperty<CATransferStatus> statusProperty;
  private final SimpleStringProperty statusTextProperty;
  private final SimpleObjectProperty<CATransferStatusType> transferStatusProperty;

  public CATransferMutable(
    final CAMainStrings inStrings,
    final Instant inStarted,
    final UUID inId,
    final String inTitle,
    final long inExpectedSize)
  {
    this.strings =
      Objects.requireNonNull(inStrings, "strings");
    this.started =
      Objects.requireNonNull(inStarted, "started");
    this.id =
      Objects.requireNonNull(inId, "id");
    this.title =
      Objects.requireNonNull(inTitle, "title");
    this.expectedSize =
      inExpectedSize;

    this.statusTextProperty =
      new SimpleStringProperty();
    this.receivedSize =
      new SimpleLongProperty();
    this.receivingRate =
      new SimpleLongProperty();

    this.receivingText =
      CAObservables.transform(this.receivingRate, receiving -> {
        return this.strings.format(
          "transfer.receiving",
          CASizeFormatter.formatSize(receiving.longValue())
        );
      });
    this.receivedText =
      CAObservables.transform(this.receivedSize, received -> {
        return this.strings.format(
          "transfer.received",
          CASizeFormatter.formatSize(received.longValue()),
          CASizeFormatter.formatSize(this.expectedSize)
        );
      });

    this.timeRemaining =
      new SimpleObjectProperty<>(Optional.empty());

    this.timeRemainingTextProperty =
      CAObservables.transform(
        this.timeRemaining, remainingOpt -> {
          if (remainingOpt.isEmpty()) {
            return "";
          }

          final var remainingTime = remainingOpt.get();
          return this.strings.format(
            "transfer.remaining.time",
            DurationFormatUtils.formatDuration(
              remainingTime.toMillis(), "HH:mm:ss")
          );
        }
      );

    this.progressProperty =
      new SimpleDoubleProperty(-1.0);
    this.statusProperty =
      new SimpleObjectProperty<>(CATransferStatus.STATUS_WAITING);
    this.transferStatusProperty =
      new SimpleObjectProperty<>(
        new CATransferDownloadWaiting(
          this.id,
          this.title,
          this.expectedSize,
          0L));
  }

  public static CATransferMutable of(
    final CAMainStrings strings,
    final Instant started,
    final CATransferStatusType status)
  {
    final var transfer =
      new CATransferMutable(
        strings,
        started,
        status.id(),
        status.title(),
        status.expectedOctets()
      );

    return transfer;
  }

  public Instant started()
  {
    return this.started;
  }

  public ObservableValue<String> timeRemainingTextProperty()
  {
    return this.timeRemainingTextProperty;
  }

  public ObservableValue<String> receivingTextProperty()
  {
    return this.receivingText;
  }

  public ObservableValue<String> receivedTextProperty()
  {
    return this.receivedText;
  }

  public UUID id()
  {
    return this.id;
  }

  public String title()
  {
    return this.title;
  }

  public long expectedSize()
  {
    return this.expectedSize;
  }

  public SimpleLongProperty receivedSize()
  {
    return this.receivedSize;
  }

  public SimpleLongProperty receivingRate()
  {
    return this.receivingRate;
  }

  public SimpleObjectProperty<Optional<Duration>> timeRemaining()
  {
    return this.timeRemaining;
  }

  public ReadOnlyDoubleProperty progressProperty()
  {
    return this.progressProperty;
  }

  public void updateFrom(
    final CATransferStatusType status)
  {
    this.transferStatusProperty.set(status);
    this.receivedSize.set(status.receivedOctets());
    this.progressProperty.set(status.progress());

    if (status instanceof CATransferDownloadWaiting waiting) {
      this.receivingRate.set(0L);
      this.timeRemaining.set(Optional.empty());
      this.statusProperty.set(CATransferStatus.STATUS_WAITING);
      this.statusTextProperty.set("");
      return;
    }
    if (status instanceof CATransferDownloading downloading) {
      this.receivingRate.set((long) downloading.octetsPerSecondAverage());
      this.timeRemaining.set(downloading.expectedRemainingTime());
      this.statusProperty.set(CATransferStatus.STATUS_DOWNLOADING);
      this.statusTextProperty.set("");
      return;
    }
    if (status instanceof CATransferDownloadFailed failed) {
      this.receivingRate.set(0L);
      this.timeRemaining.set(Optional.empty());
      this.receivedSize.set(failed.receivedOctets());
      this.statusProperty.set(CATransferStatus.STATUS_FAILED);
      this.statusTextProperty.set(failed.errorMessage());
      return;
    }
    if (status instanceof CATransferDownloadCompleted completed) {
      this.receivingRate.set(0L);
      this.timeRemaining.set(Optional.empty());
      this.receivedSize.set(this.expectedSize);
      this.statusProperty.set(CATransferStatus.STATUS_COMPLETED);
      this.statusTextProperty.set(
        this.strings.format(
          "transfer.completed",
          CASizeFormatter.formatSize(this.expectedSize),
          DurationFormatUtils.formatDurationHMS(completed.completedTime().toMillis()))
      );
      return;
    }
    if (status instanceof CATransferDownloadCancelled cancelled) {
      this.receivingRate.set(0L);
      this.timeRemaining.set(Optional.empty());
      this.receivedSize.set(this.expectedSize);
      this.statusProperty.set(CATransferStatus.STATUS_CANCELLED);
      this.statusTextProperty.set(this.strings.format("transfer.cancelled"));
      return;
    }
  }

  public ObservableValue<CATransferStatus> statusProperty()
  {
    return this.statusProperty;
  }

  public ObservableValue<String> statusTextProperty()
  {
    return this.statusTextProperty;
  }

  public ReadOnlyObjectProperty<CATransferStatusType> transferStatusProperty()
  {
    return this.transferStatusProperty;
  }
}
