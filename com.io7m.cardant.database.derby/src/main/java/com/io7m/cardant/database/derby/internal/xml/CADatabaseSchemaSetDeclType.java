/*
 * Copyright © 2021 Mark Raynsford <code@io7m.com> http://io7m.com
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

package com.io7m.cardant.database.derby.internal.xml;

import com.io7m.immutables.styles.ImmutablesStyleType;
import org.immutables.value.Value;

import java.math.BigInteger;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A set of database schemas.
 */

@ImmutablesStyleType
@Value.Immutable
public interface CADatabaseSchemaSetDeclType
{
  /**
   * @return The list of schema declarations
   */

  List<CADatabaseSchemaDecl> schemas();

  /**
   * @return A set of database schemas in order
   */

  @Value.Derived
  default NavigableMap<BigInteger, CADatabaseSchemaDecl> schemasInOrder()
  {
    return new TreeMap<>(
      this.schemas()
        .stream()
        .collect(Collectors.toMap(
          CADatabaseSchemaDeclType::schemaCurrent,
          Function.identity())
        ));
  }
}
