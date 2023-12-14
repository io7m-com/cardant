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


package com.io7m.cardant.server.http;

import com.io7m.cardant.server.service.telemetry.api.CAMetricsServiceType;
import com.io7m.cardant.server.service.telemetry.api.CAServerTelemetryServiceType;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import io.helidon.webserver.http.ServerRequest;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;

import java.time.Instant;
import java.util.Objects;

import static com.io7m.cardant.server.service.telemetry.api.CAServerTelemetryServiceType.recordSpanException;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.HTTP_CLIENT_IP;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.HTTP_METHOD;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.HTTP_RESPONSE_CONTENT_LENGTH;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.HTTP_STATUS_CODE;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.HTTP_URL;

/**
 * A servlet core that executes the given core with instrumentation.
 */

public final class CAHTTPHandlerCoreInstrumented
  implements CAHTTPHandlerFunctionalCoreType
{
  private final CAHTTPHandlerFunctionalCoreType core;
  private final CAServerTelemetryServiceType telemetry;
  private final CAMetricsServiceType metrics;

  private CAHTTPHandlerCoreInstrumented(
    final RPServiceDirectoryType inServices,
    final CAHTTPHandlerFunctionalCoreType inCore)
  {
    this.telemetry =
      inServices.requireService(CAServerTelemetryServiceType.class);
    this.metrics =
      inServices.requireService(CAMetricsServiceType.class);

    this.core =
      Objects.requireNonNull(inCore, "core");
  }

  /**
   * @param inServices The services
   * @param inCore     The core
   *
   * @return A servlet core that executes the given core with instrumentation
   */

  public static CAHTTPHandlerFunctionalCoreType withInstrumentation(
    final RPServiceDirectoryType inServices,
    final CAHTTPHandlerFunctionalCoreType inCore)
  {
    return new CAHTTPHandlerCoreInstrumented(inServices, inCore);
  }

  @Override
  public CAHTTPResponseType execute(
    final ServerRequest request,
    final CAHTTPRequestInformation information)
  {
    final var context =
      this.telemetry.textMapPropagator()
        .extract(
          Context.current(),
          request,
          CAHTTPServerRequestContextExtractor.instance()
        );

    final var tracer =
      this.telemetry.tracer();

    final var span =
      tracer.spanBuilder(request.path().path())
        .setParent(context)
        .setStartTimestamp(Instant.now())
        .setSpanKind(SpanKind.SERVER)
        .setAttribute(HTTP_CLIENT_IP, information.remoteAddress())
        .setAttribute(HTTP_METHOD, request.prologue().method().text())
        .setAttribute(HTTP_URL, request.path().path())
        .setAttribute("http.request_id", information.requestID().toString())
        .startSpan();

    this.metrics.onHttpRequested();
    this.metrics.onHttpRequestSize(
      CAHTTPServerRequests.contentLength(request)
    );

    try (var ignored = span.makeCurrent()) {
      final var response =
        this.core.execute(request, information);

      final var code = response.statusCode();
      if (code >= 400) {
        if (code >= 500) {
          this.metrics.onHttp5xx();
        } else {
          this.metrics.onHttp4xx();
        }
      } else {
        this.metrics.onHttp2xx();
      }

      span.setAttribute(HTTP_STATUS_CODE, response.statusCode());
      response.contentLengthOptional().ifPresent(size -> {
        span.setAttribute(HTTP_RESPONSE_CONTENT_LENGTH, Long.valueOf(size));
        this.metrics.onHttpResponseSize(size);
      });
      return response;
    } catch (final Throwable e) {
      recordSpanException(e);
      throw e;
    } finally {
      span.end();
    }
  }
}
