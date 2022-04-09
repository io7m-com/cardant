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

import java.util.Objects;

/**
 * An expression that matches roles.
 */

public sealed interface CSMatchRoleType
{
  /**
   * An expression that matches any set of roles.
   */

  enum CSMatchRolesAny implements CSMatchRoleType
  {
    ANY_ROLES;

    @Override
    public String serialized()
    {
      return "roles *";
    }
  }

  /**
   * An expression that matches if all of the provided roles are present.
   */

  record CSMatchRolesAllOf(CSRoleSet roles)
    implements CSMatchRoleType
  {
    public CSMatchRolesAllOf
    {
      Objects.requireNonNull(roles, "roles");

      if (roles.roles().isEmpty()) {
        throw new IllegalArgumentException(
          "Cannot match against an empty set of roles");
      }
    }

    @Override
    public String serialized()
    {
      return "roles all " + this.roles.serialized();
    }
  }

  /**
   * @return The serialized form of this expression
   */

  String serialized();
}
