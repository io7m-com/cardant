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

/**
 * An expression that matches an object.
 */

public sealed interface CSMatchObjectType
{
  /**
   * An expression that matches an object by the type of the object.
   */

  enum CSMatchObjectByType implements CSMatchObjectType
  {
    ITEM,
    LOCATION,
    USER,
    ANY_OBJECT;

    @Override
    public String serialized()
    {
      return switch (this) {
        case ITEM -> "type item";
        case USER -> "type user";
        case LOCATION -> "type location";
        case ANY_OBJECT -> "type any";
      };
    }
  }

  /**
   * @return The serialized form of the expression
   */

  String serialized();
}
