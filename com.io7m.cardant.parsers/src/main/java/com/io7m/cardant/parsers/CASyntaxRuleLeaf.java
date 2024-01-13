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


package com.io7m.cardant.parsers;

import java.util.List;
import java.util.Objects;

/**
 * A syntax rule leaf.
 *
 * @param name     The name
 * @param text     The rule text
 * @param examples The examples
 */

public record CASyntaxRuleLeaf(
  String name,
  String text,
  List<String> examples)
  implements CASyntaxRuleType
{
  /**
   * A syntax rule leaf.
   *
   * @param name     The name
   * @param text     The rule text
   * @param examples The examples
   */

  public CASyntaxRuleLeaf
  {
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(text, "text");
    examples = List.copyOf(examples);
  }

  @Override
  public int compareTo(
    final CASyntaxRuleType other)
  {
    return this.name.compareTo(other.name());
  }

  @Override
  public List<String> subRules()
  {
    return List.of();
  }
}
