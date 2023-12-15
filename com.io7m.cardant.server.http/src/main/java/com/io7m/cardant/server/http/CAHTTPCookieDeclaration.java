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

import java.time.Duration;
import java.util.Objects;

/**
 * A cookie declaration.
 *
 * @param name     The cookie name
 * @param value    The cookie value
 * @param validity The duration for which the cookie is valid
 */

public record CAHTTPCookieDeclaration(
  String name,
  String value,
  Duration validity)
{
  /**
   * A cookie declaration.
   *
   * @param name     The cookie name
   * @param value    The cookie value
   * @param validity The duration for which the cookie is valid
   */

  public CAHTTPCookieDeclaration
  {
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(value, "value");
    Objects.requireNonNull(validity, "validity");
  }
}