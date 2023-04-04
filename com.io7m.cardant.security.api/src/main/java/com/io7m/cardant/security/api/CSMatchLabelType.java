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

import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * An expression matching a label.
 */

public sealed interface CSMatchLabelType
{
  /**
   * An expression that matches any label.
   */

  enum CSMatchLabelAny implements CSMatchLabelType
  {
    /**
     * An expression that matches any label.
     */

    ANY_LABEL;

    @Override
    public String serialized()
    {
      return "label *";
    }
  }

  /**
   * An expression that matches a label that contains any of the matching
   * attributes.
   *
   * @param attributes The attribute match expressions
   */

  record CSMatchLabelAnyOf(
    SortedMap<CSAttributeName, CSMatchAttributeValueType> attributes)
    implements CSMatchLabelType
  {
    /**
     * An expression that matches a label that contains any of the matching
     * attributes.
     *
     * @param attributes The attribute match expressions
     */

    public CSMatchLabelAnyOf
    {
      Objects.requireNonNull(attributes, "attributes");
    }

    @Override
    public String serialized()
    {
      final var text = new StringBuilder(this.attributes.size() * 8);

      text.append("label ");
      for (final var entry : this.attributes.entrySet()) {
        text.append(entry.getKey().value());
        text.append('=');
        text.append(entry.getValue().serialized());
        text.append(';');
      }
      return text.toString();
    }
  }

  /**
   * A convenience function for constructing {@link CSMatchLabelAnyOf} values.
   *
   * @param entries The attributes
   *
   * @return A label expression
   */

  @SafeVarargs
  static CSMatchLabelAnyOf anyOf(
    final Map.Entry<CSAttributeName, CSMatchAttributeValueType>... entries)
  {
    return new CSMatchLabelAnyOf(
      new TreeMap<>(Map.ofEntries(entries))
    );
  }

  /**
   * @return The serialized form of this element
   */

  String serialized();
}
