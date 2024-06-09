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

import com.io7m.cardant.model.CAIncludeDeleted;
import com.io7m.cardant.model.CAItemIDMatch;
import com.io7m.cardant.model.CALocationMatchType;
import com.io7m.cardant.model.CAStockOccurrenceKind;
import com.io7m.cardant.model.CAStockSearchParameters;
import com.io7m.cardant.model.comparisons.CAComparisonExactType;
import com.io7m.cardant.protocol.inventory.CAICommandStockSearchBegin;
import com.io7m.cardant.protocol.inventory.CAIResponseStockSearch;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed0N;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "stock-search-begin"
 */

public final class CAShellCmdStockSearchBegin
  extends CAShellCmdAbstractCR<CAICommandStockSearchBegin, CAIResponseStockSearch>
{
  private static final QParameterNamed1<CALocationMatchType> LOCATION_MATCH =
    new QParameterNamed1<>(
      "--location-match",
      List.of(),
      new QConstant(
        "Only include stock in locations matching the given expression."),
      Optional.of(new CALocationMatchType.CALocationsAll()),
      CALocationMatchType.class
    );

  private static final QParameterNamed1<Integer> LIMIT =
    new QParameterNamed1<>(
      "--limit",
      List.of(),
      new QConstant("The maximum number of results per page."),
      Optional.of(Integer.valueOf(100)),
      Integer.class
    );

  private static final QParameterNamed1<CAItemIDMatch> ITEM_MATCH =
    new QParameterNamed1<>(
      "--item-match",
      List.of(),
      new QConstant(
        "Only include items with IDs matching the given expression."),
      Optional.of(new CAItemIDMatch(new CAComparisonExactType.Anything<>())),
      CAItemIDMatch.class
    );

  private static final QParameterNamed0N<CAStockOccurrenceKind> STOCK_KIND_EXCLUDE =
    new QParameterNamed0N<>(
      "--exclude-stock-kind",
      List.of(),
      new QConstant("Exclude stock occurrences of the given kinds."),
      List.of(),
      CAStockOccurrenceKind.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The context
   */

  public CAShellCmdStockSearchBegin(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "stock-search-begin",
        new QConstant("Start searching for stock."),
        Optional.empty()
      ),
      CAICommandStockSearchBegin.class
    );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(
      LIMIT,
      ITEM_MATCH,
      LOCATION_MATCH,
      STOCK_KIND_EXCLUDE
    );
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    final var client =
      this.client();

    final var includeKinds =
      new HashSet<>(CAStockOccurrenceKind.all());

    for (final var k : context.parameterValues(STOCK_KIND_EXCLUDE)) {
      includeKinds.remove(k);
    }

    final var parameters =
      new CAStockSearchParameters(
        context.parameterValue(LOCATION_MATCH),
        context.parameterValue(ITEM_MATCH).expression(),
        Set.copyOf(includeKinds),
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        context.parameterValue(LIMIT).longValue()
      );

    final var items =
      client.sendAndWaitOrThrow(
        new CAICommandStockSearchBegin(parameters),
        this.commandTimeout()
      ).data();

    this.formatter().formatStockPage(items);
    return SUCCESS;
  }
}
