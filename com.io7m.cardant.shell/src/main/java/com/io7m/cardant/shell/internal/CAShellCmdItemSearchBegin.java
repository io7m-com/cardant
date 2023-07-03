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
import com.io7m.cardant.model.CAItemSearchParameters;
import com.io7m.cardant.model.CAListLocationBehaviourType;
import com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationExact;
import com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationWithDescendants;
import com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationsAll;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.protocol.inventory.CAICommandItemSearchBegin;
import com.io7m.cardant.protocol.inventory.CAIResponseItemSearch;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed01;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;

import java.util.List;
import java.util.Optional;

import static com.io7m.cardant.model.CAItemColumn.BY_NAME;
import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "item-search-begin"
 */

public final class CAShellCmdItemSearchBegin
  extends CAShellCmdAbstractCR<CAICommandItemSearchBegin, CAIResponseItemSearch>
{
  private static final QParameterNamed01<String> SEARCH =
    new QParameterNamed01<>(
      "--query",
      List.of(),
      new QConstant("The item search query."),
      Optional.empty(),
      String.class
    );

  private static final QParameterNamed01<CALocationID> LOCATIONS_EXACT =
    new QParameterNamed01<>(
      "--location-exact",
      List.of(),
      new QConstant("Specify the exact location within which to limit search results."),
      Optional.empty(),
      CALocationID.class
    );

  private static final QParameterNamed01<CALocationID> LOCATIONS_DESCENDANTS =
    new QParameterNamed01<>(
      "--location-with-descendants",
      List.of(),
      new QConstant("Specify the location (and descendants) within which to limit search results."),
      Optional.empty(),
      CALocationID.class
    );

  private static final QParameterNamed1<Integer> LIMIT =
    new QParameterNamed1<>(
      "--limit",
      List.of(),
      new QConstant("The maximum number of results per page."),
      Optional.of(Integer.valueOf(100)),
      Integer.class
    );

  /**
   * Construct a command.
   *
   * @param inContext The context
   */

  public CAShellCmdItemSearchBegin(
    final CAShellContextType inContext)
  {
    super(
      inContext,
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
    return List.of(SEARCH, LIMIT, LOCATIONS_EXACT, LOCATIONS_DESCENDANTS);
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    final var client =
      this.client();

    final var exactOpt =
      context.parameterValue(LOCATIONS_EXACT);
    final var descendantsOpt =
      context.parameterValue(LOCATIONS_DESCENDANTS);

    CAListLocationBehaviourType locationBehaviour =
      new CAListLocationsAll();

    if (exactOpt.isPresent()) {
      locationBehaviour =
        new CAListLocationExact(exactOpt.get());
    }
    if (descendantsOpt.isPresent()) {
      locationBehaviour =
        new CAListLocationWithDescendants(descendantsOpt.get());
    }

    final var parameters =
      new CAItemSearchParameters(
        locationBehaviour,
        context.parameterValue(SEARCH),
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
