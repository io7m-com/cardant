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


package com.io7m.cardant.server.inventory.v1.internal;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.io7m.cardant.protocol.inventory.json.CAIJ1Messages;
import com.io7m.cardant.server.http.CAHTTPHandlerFunctional;
import com.io7m.cardant.server.http.CAHTTPHandlerFunctionalCoreType;
import com.io7m.cardant.server.http.CAHTTPResponseFixedSize;
import com.io7m.cardant.server.http.CAHTTPResponseType;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import com.io7m.ventrad.core.VProtocol;
import com.io7m.ventrad.core.VProtocols;

import java.math.BigInteger;
import java.net.URI;
import java.util.List;
import java.util.Set;

import static com.io7m.cardant.server.http.CAHTTPHandlerCoreInstrumented.withInstrumentation;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * The v1 version servlet.
 */

public final class CA1HandlerVersions
  extends CAHTTPHandlerFunctional
{
  private static final JsonMapper MAPPER =
    JsonMapper.builder()
      .build();

  private static final VProtocols PROTOCOLS =
    createProtocols();

  /**
   * The v1 version servlet.
   *
   * @param services The services
   */

  public CA1HandlerVersions(
    final RPServiceDirectoryType services)
  {
    super(createCore(services));
  }

  private static CAHTTPHandlerFunctionalCoreType createCore(
    final RPServiceDirectoryType services)
  {
    return (request, information) -> {
      return withInstrumentation(
        services,
        (req0, info0) -> {
          return execute();
        }
      ).execute(request, information);
    };
  }

  private static CAHTTPResponseType execute()
  {
    try {
      return new CAHTTPResponseFixedSize(
        200,
        Set.of(),
        "text/json",
        MAPPER.writeValueAsBytes(PROTOCOLS)
      );
    } catch (final Exception e) {
      return new CAHTTPResponseFixedSize(
        500,
        Set.of(),
        "text/plain",
        e.getMessage().getBytes(UTF_8)
      );
    }
  }

  private static VProtocols createProtocols()
  {
    return new VProtocols(List.of(
      new VProtocol(
        CAIJ1Messages.protocolId().toString(),
        BigInteger.ONE,
        BigInteger.ZERO,
        URI.create("/inventory/1/0/"),
        "Cardant Inventory v1.0"
      )
    ));
  }
}
