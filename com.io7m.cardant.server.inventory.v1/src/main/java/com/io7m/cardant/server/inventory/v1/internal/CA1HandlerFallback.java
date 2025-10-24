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

import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.protocol.inventory.CAIResponseBlame;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.json.CAIJ1Messages;
import com.io7m.cardant.server.http.CAHTTPHandlerFunctional;
import com.io7m.cardant.server.http.CAHTTPHandlerFunctionalCoreType;
import com.io7m.cardant.server.http.CAHTTPRequestInformation;
import com.io7m.cardant.server.http.CAHTTPResponseFixedSize;
import com.io7m.cardant.server.http.CAHTTPResponseType;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.io7m.cardant.server.http.CAHTTPHandlerCoreInstrumented.withInstrumentation;

/**
 * A fallback servlet.
 */

public final class CA1HandlerFallback
  extends CAHTTPHandlerFunctional
{
  /**
   * A fallback servlet.
   *
   * @param services The services
   */

  public CA1HandlerFallback(
    final RPServiceDirectoryType services)
  {
    super(createCore(services));
  }

  private static CAHTTPHandlerFunctionalCoreType createCore(
    final RPServiceDirectoryType services)
  {
    final var messages =
      services.requireService(CAIJ1Messages.class);

    return (request, information) -> {
      return withInstrumentation(
        services,
        (req0, info0) -> {
          return execute(messages, information);
        }
      ).execute(request, information);
    };
  }

  private static CAHTTPResponseType execute(
    final CAIJ1Messages messages,
    final CAHTTPRequestInformation information)
  {
    final var error =
      new CAIResponseError(
        information.requestID(),
        "No such endpoint.",
        CAStandardErrorCodes.errorApiMisuse(),
        Map.of(),
        Optional.empty(),
        Optional.empty(),
        CAIResponseBlame.BLAME_CLIENT,
        List.of()
      );

    return new CAHTTPResponseFixedSize(
      400,
      Set.of(),
      "text/json",
      messages.serialize(error)
    );
  }
}
