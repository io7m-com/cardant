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

package com.io7m.cardant.server.service.telemetry.otp;

import com.io7m.cardant.model.CAVersion;
import com.io7m.cardant.server.api.CAServerOpenTelemetryConfiguration;
import com.io7m.cardant.server.service.telemetry.api.CAServerTelemetryNoOp;
import com.io7m.cardant.server.service.telemetry.api.CAServerTelemetryServiceFactoryType;
import com.io7m.cardant.server.service.telemetry.api.CAServerTelemetryServiceType;
import com.io7m.cardant.server.service.telemetry.otp.internal.CAServerTelemetryService;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.http.metrics.OtlpHttpMetricExporter;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static io.opentelemetry.semconv.resource.attributes.ResourceAttributes.SERVICE_NAME;
import static io.opentelemetry.semconv.resource.attributes.ResourceAttributes.SERVICE_VERSION;

/**
 * An OpenTelemetry service factory.
 */

public final class CAServerTelemetryServices
  implements CAServerTelemetryServiceFactoryType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAServerTelemetryServices.class);

  /**
   * An OpenTelemetry service factory.
   */

  public CAServerTelemetryServices()
  {

  }

  /**
   * Create a telemetry service.
   *
   * @param telemetryConfiguration The server configuration
   *
   * @return The service
   */

  @Override
  public CAServerTelemetryServiceType create(
    final CAServerOpenTelemetryConfiguration telemetryConfiguration)
  {
    Objects.requireNonNull(telemetryConfiguration, "configuration");

    final var metricsOpt =
      telemetryConfiguration.metrics();
    final var tracesOpt =
      telemetryConfiguration.traces();

    if (metricsOpt.isEmpty() && tracesOpt.isEmpty()) {
      LOG.warn(
        "Neither metrics nor trace configurations were provided; no telemetry will be sent!");
      return CAServerTelemetryNoOp.noop();
    }

    final var resource =
      Resource.getDefault()
        .merge(Resource.create(
          Attributes.builder()
            .put(SERVICE_NAME, telemetryConfiguration.logicalServiceName())
            .put(SERVICE_VERSION, CAVersion.MAIN_VERSION)
            .build()
        ));

    final var builder =
      OpenTelemetrySdk.builder();

    metricsOpt.ifPresent(metrics -> {
      builder.setMeterProvider(createMeterProvider(resource, metrics));
    });

    tracesOpt.ifPresent(traces -> {
      builder.setTracerProvider(createTracerProvider(resource, traces));
    });

    final var contextPropagators =
      ContextPropagators.create(W3CTraceContextPropagator.getInstance());

    final OpenTelemetry openTelemetry =
      builder.setPropagators(contextPropagators)
        .buildAndRegisterGlobal();

    final var tracer =
      openTelemetry.getTracer(
        "com.io7m.cardant",
        CAVersion.MAIN_VERSION
      );

    return new CAServerTelemetryService(
      openTelemetry,
      tracer
    );
  }

  private static SdkMeterProvider createMeterProvider(
    final Resource resource,
    final CAServerOpenTelemetryConfiguration.CAMetrics metrics)
  {
    final var endpoint = metrics.endpoint().toString();
    LOG.info(
      "metrics data will be sent to {} using {}",
      endpoint,
      metrics.protocol()
    );

    final var metricExporter =
      switch (metrics.protocol()) {
        case HTTP -> {
          yield OtlpHttpMetricExporter.builder()
            .setEndpoint(endpoint)
            .build();
        }
        case GRPC -> {
          yield OtlpGrpcMetricExporter.builder()
            .setEndpoint(endpoint)
            .build();
        }
      };

    final var periodicMetricReader =
      PeriodicMetricReader.builder(metricExporter)
        .build();

    return SdkMeterProvider.builder()
      .registerMetricReader(periodicMetricReader)
      .setResource(resource)
      .build();
  }

  private static SdkTracerProvider createTracerProvider(
    final Resource resource,
    final CAServerOpenTelemetryConfiguration.CATraces traces)
  {
    final var endpoint = traces.endpoint().toString();
    LOG.info(
      "trace data will be sent to {} using {}",
      endpoint,
      traces.protocol()
    );

    final var spanExporter =
      switch (traces.protocol()) {
        case HTTP -> {
          yield OtlpHttpSpanExporter.builder()
            .setEndpoint(endpoint)
            .build();
        }
        case GRPC -> {
          yield OtlpGrpcSpanExporter.builder()
            .setEndpoint(endpoint)
            .build();
        }
      };

    final var batchSpanProcessor =
      BatchSpanProcessor.builder(spanExporter)
        .build();

    return SdkTracerProvider.builder()
      .addSpanProcessor(batchSpanProcessor)
      .setResource(resource)
      .build();
  }
}