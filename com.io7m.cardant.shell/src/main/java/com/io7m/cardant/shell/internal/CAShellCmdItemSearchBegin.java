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

import com.io7m.cardant.client.api.CAClientException;
import com.io7m.cardant.model.CAItemColumnOrdering;
import com.io7m.cardant.model.CAItemLocationMatchType;
import com.io7m.cardant.model.CAItemLocationMatchType.CAItemLocationsAll;
import com.io7m.cardant.model.CAItemSearchParameters;
import com.io7m.cardant.model.CAMetadataElementMatchType;
import com.io7m.cardant.model.CANameMatchType;
import com.io7m.cardant.model.CATypeMatchType;
import com.io7m.cardant.protocol.inventory.CAICommandItemSearchBegin;
import com.io7m.cardant.protocol.inventory.CAIResponseItemSearch;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.util.List;
import java.util.Optional;

import static com.io7m.cardant.model.CAItemColumn.BY_NAME;
import static com.io7m.cardant.model.CAMetadataElementMatchType.ANYTHING;
import static com.io7m.cardant.model.CANameMatchType.Any.ANY_NAME;
import static com.io7m.cardant.model.CATypeMatchType.CATypeMatchAny.ANY;
import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "item-search-begin"
 */

public final class CAShellCmdItemSearchBegin
  extends CAShellCmdAbstractCR<CAICommandItemSearchBegin, CAIResponseItemSearch>
{
  private static final QParameterNamed1<CAItemLocationMatchType> LOCATION_MATCH =
    new QParameterNamed1<>(
      "--location-match",
      List.of(),
      new QConstant(
        "Only include items in locations matching the given expression."),
      Optional.of(new CAItemLocationsAll()),
      CAItemLocationMatchType.class
    );

  private static final QParameterNamed1<Integer> LIMIT =
    new QParameterNamed1<>(
      "--limit",
      List.of(),
      new QConstant("The maximum number of results per page."),
      Optional.of(Integer.valueOf(100)),
      Integer.class
    );

  private static final QParameterNamed1<CATypeMatchType> TYPE_MATCH =
    new QParameterNamed1<>(
      "--type-match",
      List.of(),
      new QConstant(
        "Only include items that have types matching the given expression."),
      Optional.of(ANY),
      CATypeMatchType.class
    );

  private static final QParameterNamed1<CANameMatchType> NAME_MATCH =
    new QParameterNamed1<>(
      "--name-match",
      List.of(),
      new QConstant(
        "Only include items that have names matching the given expression."),
      Optional.of(ANY_NAME),
      CANameMatchType.class
    );

  private static final QParameterNamed1<CAMetadataElementMatchType> METADATA_MATCH =
    new QParameterNamed1<>(
      "--metadata-match",
      List.of(),
      new QConstant(
        "Only include items with metadata matching the given expression."),
      Optional.of(ANYTHING),
      CAMetadataElementMatchType.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The context
   */

  public CAShellCmdItemSearchBegin(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "item-search-begin",
        new QConstant("Start searching for items."),
        Optional.empty()
      ),
      CAICommandItemSearchBegin.class
    );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(
      LIMIT,
      LOCATION_MATCH,
      METADATA_MATCH,
      NAME_MATCH,
      TYPE_MATCH
    );
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    final var client =
      this.client();

    final var locationMatch =
      context.parameterValue(LOCATION_MATCH);
    final var nameMatch =
      context.parameterValue(NAME_MATCH);
    final var typeMatch =
      context.parameterValue(TYPE_MATCH);
    final var metaMatch =
      context.parameterValue(METADATA_MATCH);

    final var parameters =
      new CAItemSearchParameters(
        locationMatch,
        nameMatch,
        typeMatch,
        metaMatch,
        new CAItemColumnOrdering(BY_NAME, true),
        context.parameterValue(LIMIT).intValue()
      );

    final var items =
      ((CAIResponseItemSearch) client.executeOrElseThrow(
        new CAICommandItemSearchBegin(parameters),
        CAClientException::ofError
      )).data();

    this.formatter().formatItemsPage(items);
    return SUCCESS;
  }
}
