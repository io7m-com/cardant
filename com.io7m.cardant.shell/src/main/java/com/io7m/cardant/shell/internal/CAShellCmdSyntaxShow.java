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

package com.io7m.cardant.shell.internal;

import com.io7m.cardant.error_codes.CAException;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.parsers.CASyntaxRuleType;
import com.io7m.cardant.parsers.CASyntaxRules;
import com.io7m.cardant.strings.CAStringConstants;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QParameterType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import org.jline.reader.Completer;
import org.jline.reader.impl.completer.StringsCompleter;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;

import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "syntax-show"
 */

public final class CAShellCmdSyntaxShow extends CAShellCmdAbstract
{
  private static final QParameterNamed1<String> RULE =
    new QParameterNamed1<>(
      "--rule",
      List.of(),
      new QConstant("The syntax rule name."),
      Optional.empty(),
      String.class
    );

  private static final QParameterNamed1<Boolean> EXAMPLE =
    new QParameterNamed1<>(
      "--example",
      List.of(),
      new QConstant("Show a syntax example."),
      Optional.of(Boolean.FALSE),
      Boolean.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The context
   */

  public CAShellCmdSyntaxShow(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "syntax-show",
        new QConstant("Show the given syntax definition."),
        Optional.empty()
      ));
  }

  @Override
  public Completer completer()
  {
    return new StringsCompleter(
      this.onListNamedParameters()
        .stream()
        .map(QParameterType::name)
        .toList()
    );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(RULE, EXAMPLE);
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    final var name =
      context.parameterValue(RULE);

    final var strings =
      this.strings();

    final var rules =
      CASyntaxRules.open(strings);

    final var rule =
      rules.rules().get(name);

    if (rule == null) {
      throw new CAException(
        strings.format(CAStringConstants.ERROR_NONEXISTENT),
        CAStandardErrorCodes.errorNonexistent(),
        Map.of(),
        Optional.empty()
      );
    }

    if (context.parameterValue(EXAMPLE).booleanValue()) {
      for (final var example : rule.examples()) {
        final var formatter = this.formatter();
        formatter.printLine(example.trim());
        formatter.printLine("");
      }
    } else {
      this.formatRule(rules.rules(), rule);
    }
    return SUCCESS;
  }

  private void formatRule(
    final SortedMap<String, CASyntaxRuleType> ruleMap,
    final CASyntaxRuleType rule)
    throws Exception
  {
    Objects.requireNonNull(ruleMap, "ruleMap");
    Objects.requireNonNull(rule, "rule");

    final var formatter = this.formatter();
    formatter.print(rule.name());
    formatter.printLine(" := ");
    formatter.print("  ");
    formatter.printLine(rule.text().trim());

    for (final var subRule : rule.subRules()) {
      this.formatRule(ruleMap, ruleMap.get(subRule));
    }
  }
}
