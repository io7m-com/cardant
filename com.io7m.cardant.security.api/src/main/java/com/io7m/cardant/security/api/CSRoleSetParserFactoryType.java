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

package com.io7m.cardant.security.api;

import com.io7m.anethum.api.ParserFactoryType;
import com.io7m.anethum.common.ParseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * The type of role set parser factories.
 */

public interface CSRoleSetParserFactoryType
  extends ParserFactoryType<Void, CSRoleSet, CSRoleSetParserType>
{
  /**
   * Parse a role set from the given string.
   *
   * @param text The serialized role set
   *
   * @return A parsed role set
   *
   * @throws ParseException On parse errors
   * @see CSLabel#serialized()
   */

  default CSRoleSet parseFromString(
    final String text)
    throws ParseException
  {
    Objects.requireNonNull(text, "text");

    try (var parser = this.createParserWithContext(
      null,
      URI.create("urn:string"),
      new ByteArrayInputStream(text.getBytes(UTF_8)),
      parseStatus -> {

      }
    )) {
      return parser.execute();
    } catch (final IOException e) {
      throw new IllegalStateException(e);
    }
  }
}
