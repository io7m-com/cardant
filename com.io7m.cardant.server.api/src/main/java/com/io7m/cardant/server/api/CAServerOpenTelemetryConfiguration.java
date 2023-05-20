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

package com.io7m.cardant.server.api;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

/**
 * Configuration information for OpenTelemetry.
 *
 * @param logicalServiceName The logical service name
 * @param metrics            The configuration for OTLP metrics
 * @param traces             The configuration for OTLP traces
 */

public record CAServerOpenTelemetryConfiguration(
  String logicalServiceName,
  Optional<CAMetrics> metrics,
  Optional<CATraces> traces)
{
  /**
   * Configuration information for OpenTelemetry.
   *
   * @param logicalServiceName The logical service name
   * @param metrics            The configuration for OTLP metrics
   * @param traces             The configuration for OTLP traces
   */

  public CAServerOpenTelemetryConfiguration
  {
    Objects.requireNonNull(logicalServiceName, "logicalServiceName");
    Objects.requireNonNull(metrics, "metrics");
    Objects.requireNonNull(traces, "traces");
  }

  /**
   * The protocol used to deliver OpenTelemetry data.
   */

  public enum CAOTLPProtocol
  {
    /**
     * gRPC
     */

    GRPC,

    /**
     * HTTP(s)
     */

    HTTP
  }

  /**
   * Metrics configuration.
   *
   * @param endpoint The endpoint to which OTLP metrics data will be sent.
   * @param protocol The protocol used to deliver OpenTelemetry data.
   */

  public record CAMetrics(
    URI endpoint,
    CAOTLPProtocol protocol)
  {
    /**
     * Metrics configuration.
     */

    public CAMetrics
    {
      Objects.requireNonNull(endpoint, "endpoint");
      Objects.requireNonNull(protocol, "protocol");
    }
  }

  /**
   * Trace configuration.
   *
   * @param endpoint The endpoint to which OTLP trace data will be sent.
   * @param protocol The protocol used to deliver OpenTelemetry data.
   */

  public record CATraces(
    URI endpoint,
    CAOTLPProtocol protocol)
  {
    /**
     * Trace configuration.
     */

    public CATraces
    {
      Objects.requireNonNull(endpoint, "endpoint");
      Objects.requireNonNull(protocol, "protocol");
    }
  }
}
