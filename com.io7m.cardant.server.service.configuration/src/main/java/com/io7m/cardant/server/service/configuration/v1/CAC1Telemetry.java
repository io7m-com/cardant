/*
 * Copyright © 2023 Mark Raynsford <code@io7m.com> https://www.io7m.com
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


package com.io7m.cardant.server.service.configuration.v1;

import com.io7m.blackthorne.core.BTElementHandlerConstructorType;
import com.io7m.blackthorne.core.BTElementHandlerType;
import com.io7m.blackthorne.core.BTElementParsingContextType;
import com.io7m.blackthorne.core.BTQualifiedName;
import com.io7m.cardant.server.api.CAServerOpenTelemetryConfiguration;
import org.xml.sax.Attributes;

import java.util.Map;
import java.util.Optional;

import static com.io7m.cardant.server.service.configuration.v1.CAC1Names.qName;
import static java.util.Map.entry;

final class CAC1Telemetry
  implements BTElementHandlerType<Object, CAServerOpenTelemetryConfiguration>
{
  private String serviceName;
  private Optional<CAServerOpenTelemetryConfiguration.CALogs> logs;
  private Optional<CAServerOpenTelemetryConfiguration.CAMetrics> metrics;
  private Optional<CAServerOpenTelemetryConfiguration.CATraces> traces;

  CAC1Telemetry(
    final BTElementParsingContextType context)
  {
    this.logs = Optional.empty();
    this.metrics = Optional.empty();
    this.traces = Optional.empty();
  }

  @Override
  public void onElementStart(
    final BTElementParsingContextType context,
    final Attributes attributes)
  {
    this.serviceName =
      attributes.getValue("LogicalServiceName");
  }

  @Override
  public Map<BTQualifiedName, BTElementHandlerConstructorType<?, ?>>
  onChildHandlersRequested(
    final BTElementParsingContextType context)
  {
    return Map.ofEntries(
      entry(qName("Logs"), CAC1TelemetryLogs::new),
      entry(qName("Metrics"), CAC1TelemetryMetrics::new),
      entry(qName("Traces"), CAC1TelemetryTraces::new)
    );
  }

  @Override
  public void onChildValueProduced(
    final BTElementParsingContextType context,
    final Object result)
  {
    switch (result) {
      case final CAServerOpenTelemetryConfiguration.CALogs e -> {
        this.logs = Optional.of(e);
      }
      case final CAServerOpenTelemetryConfiguration.CAMetrics e -> {
        this.metrics = Optional.of(e);
      }
      case final CAServerOpenTelemetryConfiguration.CATraces e -> {
        this.traces = Optional.of(e);
      }
      default -> {
        throw new IllegalStateException(
          "Unrecognized element received: %s".formatted(result)
        );
      }
    }
  }

  @Override
  public CAServerOpenTelemetryConfiguration onElementFinished(
    final BTElementParsingContextType context)
  {
    return new CAServerOpenTelemetryConfiguration(
      this.serviceName,
      this.logs,
      this.metrics,
      this.traces
    );
  }
}
