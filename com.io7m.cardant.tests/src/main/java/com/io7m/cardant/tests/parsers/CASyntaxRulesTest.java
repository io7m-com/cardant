/*
 * Copyright © 2024 Mark Raynsford <code@io7m.com> https://www.io7m.com
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


package com.io7m.cardant.tests.parsers;

import com.io7m.cardant.parsers.CASyntaxRuleType;
import com.io7m.cardant.parsers.CASyntaxRules;
import com.io7m.cardant.strings.CAStrings;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class CASyntaxRulesTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CASyntaxRulesTest.class);

  @Test
  public void testAllRules()
  {
    final var rules =
      CASyntaxRules.open(CAStrings.create(Locale.ROOT));

    final var stack = new ArrayDeque<String>();
    for (final var rule : rules.rules().values()) {
      LOG.debug("Examining rule: {}", rule);

      try {
        stack.push(rule.name());
        checkSubRules(rules, stack, rule);
      } finally {
        stack.pop();
      }
    }
  }

  private static void checkSubRules(
    final CASyntaxRules rules,
    final ArrayDeque<String> stack,
    final CASyntaxRuleType rule)
  {
    for (final var subRuleName : rule.subRules()) {
      LOG.debug("Examining subrule: {}", subRuleName);

      try {
        stack.push(subRuleName);

        final var subRule = rules.rules().get(subRuleName);
        assertNotNull(
          subRule,
          "Rule path \"%s\" must be valid".formatted(path(stack))
        );

        checkSubRules(rules, stack, subRule);
      } finally {
        stack.pop();
      }
    }
  }

  private static String path(
    final ArrayDeque<String> stack)
  {
    final var names = new ArrayList<>(stack);
    return String.join(" -> ", names.reversed());
  }
}
