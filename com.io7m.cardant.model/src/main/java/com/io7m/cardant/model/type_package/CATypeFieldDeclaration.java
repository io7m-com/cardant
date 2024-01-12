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


package com.io7m.cardant.model.type_package;

import com.io7m.cardant.model.CATypeScalarIdentifier;

import java.util.Objects;

/**
 * A field declared within a record type.
 *
 * @param name        The field name
 * @param description The field description
 * @param type        The field type
 * @param isRequired  {@code true} if the field is required
 */

public record CATypeFieldDeclaration(
  CANameUnqualified name,
  String description,
  CATypeScalarIdentifier type,
  boolean isRequired)
{
  /**
   * A field declared within a record type.
   *
   * @param name        The field name
   * @param description The field description
   * @param type        The field type
   * @param isRequired  {@code true} if the field is required
   */

  public CATypeFieldDeclaration
  {
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(description, "description");
    Objects.requireNonNull(type, "type");
  }
}
