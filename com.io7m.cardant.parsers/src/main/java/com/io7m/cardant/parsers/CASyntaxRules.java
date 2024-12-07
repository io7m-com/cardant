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

import com.io7m.cardant.strings.CAStrings;
import com.io7m.jaffirm.core.Preconditions;

import java.util.Collections;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A directory of syntax rules.
 */

public final class CASyntaxRules
{
  private final SortedMap<String, CASyntaxRuleType> rules;

  private CASyntaxRules(
    final SortedMap<String, CASyntaxRuleType> inRules)
  {
    this.rules = Objects.requireNonNull(inRules, "rules");
  }

  /**
   * Open a syntax rule directory from ServiceLoader.
   *
   * @param strings The strings
   *
   * @return The syntax rules
   */

  public static CASyntaxRules open(
    final CAStrings strings)
  {
    final var iterator =
      ServiceLoader.load(CASyntaxFactoryType.class)
        .iterator();

    final var ruleMap =
      new TreeMap<String, CASyntaxRuleType>();

    while (iterator.hasNext()) {
      final var factory =
        iterator.next();
      final var syntax =
        factory.createSyntax(strings);

      for (final var rule : syntax.syntaxRules()) {
        Preconditions.checkPreconditionV(
          !ruleMap.containsKey(rule.name()),
          "Rule map cannot contain multiple rules named %s",
          rule.name()
        );
        ruleMap.put(rule.name(), rule);
      }
    }

    return new CASyntaxRules(ruleMap);
  }

  /**
   * @return A read-only map of the known syntax rules
   */

  public SortedMap<String, CASyntaxRuleType> rules()
  {
    return Collections.unmodifiableSortedMap(this.rules);
  }

  @Override
  public String toString()
  {
    return "[CASyntaxRules 0x%s]"
      .formatted(Integer.toUnsignedString(this.hashCode(), 16));
  }
}
