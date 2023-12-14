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


package com.io7m.cardant.server.service.tls;


import com.io7m.cardant.error_codes.CAException;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.server.service.telemetry.api.CAServerTelemetryServiceType;
import com.io7m.cardant.strings.CAStringConstants;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.cardant.tls.CATLSContext;
import com.io7m.cardant.tls.CATLSStoreConfiguration;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.io7m.cardant.server.service.telemetry.api.CAServerTelemetryServiceType.recordSpanException;

/**
 * The TLS context service.
 */

public final class CATLSContextService
  implements CATLSContextServiceType
{
  private final ConcurrentHashMap.KeySetView<CATLSContext, Boolean> contexts;
  private final CAServerTelemetryServiceType telemetry;
  private final CAStrings strings;

  private CATLSContextService(
    final CAServerTelemetryServiceType inTelemetry,
    final CAStrings inStrings)
  {
    this.telemetry =
      Objects.requireNonNull(inTelemetry, "telemetry");
    this.strings =
      Objects.requireNonNull(inStrings, "strings");
    this.contexts =
      ConcurrentHashMap.newKeySet();
  }

  @Override
  public String toString()
  {
    return "[CATLSContextService 0x%x]"
      .formatted(Integer.valueOf(this.hashCode()));
  }

  /**
   * @param services The service directory
   *
   * @return A new TLS context service
   */

  public static CATLSContextServiceType createService(
    final RPServiceDirectoryType services)
  {
    return new CATLSContextService(
      services.requireService(CAServerTelemetryServiceType.class),
      services.requireService(CAStrings.class)
    );
  }

  @Override
  public CATLSContext create(
    final String user,
    final CATLSStoreConfiguration keyStoreConfiguration,
    final CATLSStoreConfiguration trustStoreConfiguration)
    throws CAException
  {
    try {
      final var newContext =
        CATLSContext.create(
          user,
          keyStoreConfiguration,
          trustStoreConfiguration
        );
      this.contexts.add(newContext);
      return newContext;
    } catch (final IOException e) {
      throw errorIO(this.strings, e);
    } catch (final GeneralSecurityException e) {
      throw errorSecurity(e);
    }
  }

  @Override
  public void reload()
  {
    final var span =
      this.telemetry.tracer()
        .spanBuilder("ReloadTLSContexts")
        .startSpan();

    try (var ignored = span.makeCurrent()) {
      for (final var context : this.contexts) {
        this.reloadContext(context);
      }
    } finally {
      span.end();
    }
  }

  private void reloadContext(
    final CATLSContext context)
  {
    final var span =
      this.telemetry.tracer()
        .spanBuilder("ReloadTLSContext")
        .startSpan();

    try (var ignored = span.makeCurrent()) {
      context.reload();
    } catch (final Throwable e) {
      recordSpanException(e);
    } finally {
      span.end();
    }
  }

  @Override
  public String description()
  {
    return "The TLS context service.";
  }

  private static CAException errorIO(
    final CAStrings strings,
    final IOException e)
  {
    return new CAException(
      strings.format(CAStringConstants.ERROR_IO),
      e,
      CAStandardErrorCodes.errorIo(),
      Map.of(),
      Optional.empty()
    );
  }

  private static CAException errorSecurity(
    final GeneralSecurityException e)
  {
    return new CAException(
      e.getMessage(),
      e,
      CAStandardErrorCodes.errorIo(),
      Map.of(),
      Optional.empty()
    );
  }
}
