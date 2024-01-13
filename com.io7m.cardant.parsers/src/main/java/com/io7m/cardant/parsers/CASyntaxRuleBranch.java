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
 * A syntax rule consisting of a set of sub rules.
 *
 * @param name     The name
 * @param subRules The rules
 * @param examples The examples
 */

public record CASyntaxRuleBranch(
  String name,
  List<String> subRules,
  List<String> examples)
  implements CASyntaxRuleType
{
  /**
   * A syntax rule consisting of a set of sub rules.
   *
   * @param name     The name
   * @param subRules The rules
   * @param examples The examples
   */

  public CASyntaxRuleBranch
  {
    Objects.requireNonNull(name, "name");
    subRules = List.copyOf(subRules);
    examples = List.copyOf(examples);
  }

  @Override
  public String text()
  {
    return String.join("\n  | ", this.subRules);
  }

  @Override
  public int compareTo(
    final CASyntaxRuleType o)
  {
    return this.name.compareTo(o.name());
  }
}
