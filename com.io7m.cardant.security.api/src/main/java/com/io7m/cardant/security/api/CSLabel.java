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
import java.util.SortedMap;

/**
 * A security label.
 *
 * @param attributes The set of attributes in the label
 */

public record CSLabel(
  SortedMap<CSAttributeName, CSAttributeValue> attributes)
{
  /**
   * A security label.
   *
   * @param attributes The set of attributes in the label
   */

  public CSLabel
  {
    Objects.requireNonNull(attributes, "attributes");
  }

  /**
   * @return The label in canonical serialized form
   */

  public String serialized()
  {
    final var serialized = new StringBuilder(this.attributes.size() * 8);
    for (final var entry : this.attributes.entrySet()) {
      serialized.append(entry.getKey().value());
      serialized.append('=');
      serialized.append(entry.getValue().value());
      serialized.append(';');
    }
    return serialized.toString();
  }
}
