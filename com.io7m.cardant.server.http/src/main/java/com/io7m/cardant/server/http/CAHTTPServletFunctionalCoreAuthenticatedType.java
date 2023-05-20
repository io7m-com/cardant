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

import jakarta.servlet.http.HttpServletRequest;

/**
 * An authenticated functional servlet core. Consumes a request (and request
 * information) and returns a response.
 *
 * @param <S> The type of sessions
 * @param <U> The type of users
 */

public interface CAHTTPServletFunctionalCoreAuthenticatedType<S, U>
{
  /**
   * Execute the core after authenticating.
   *
   * @param request     The request
   * @param information The request information
   * @param session     The session
   * @param user        The user
   *
   * @return The response
   */

  CAHTTPServletResponseType executeAuthenticated(
    HttpServletRequest request,
    CAHTTPServletRequestInformation information,
    S session,
    U user
  );
}
