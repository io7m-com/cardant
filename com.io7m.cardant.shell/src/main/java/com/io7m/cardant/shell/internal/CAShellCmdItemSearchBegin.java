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
import com.io7m.cardant.model.CAItemLocationMatchType.CAItemLocationExact;
import com.io7m.cardant.model.CAItemLocationMatchType.CAItemLocationWithDescendants;
import com.io7m.cardant.model.CAItemLocationMatchType.CAItemLocationsAll;
import com.io7m.cardant.model.CAItemSearchParameters;
import com.io7m.cardant.model.CAItemSearchParameters.CAMetadataMatchType;
import com.io7m.cardant.model.CAItemSearchParameters.CAMetadataMatchType.CAMetadataMatchAny;
import com.io7m.cardant.model.CAItemSearchParameters.CAMetadataMatchType.CAMetadataRequire;
import com.io7m.cardant.model.CAItemSearchParameters.CAMetadataValueMatchType;
import com.io7m.cardant.model.CAItemSearchParameters.CAMetadataValueMatchType.CAMetadataValueMatchAny;
import com.io7m.cardant.model.CAItemSearchParameters.CAMetadataValueMatchType.CAMetadataValueMatchExact;
import com.io7m.cardant.model.CAItemSearchParameters.CANameMatchType;
import com.io7m.cardant.model.CAItemSearchParameters.CANameMatchType.CANameMatchAny;
import com.io7m.cardant.model.CAItemSearchParameters.CANameMatchType.CANameMatchExact;
import com.io7m.cardant.model.CAItemSearchParameters.CANameMatchType.CANameMatchSearch;
import com.io7m.cardant.model.CAItemSearchParameters.CATypeMatchType;
import com.io7m.cardant.model.CAItemSearchParameters.CATypeMatchType.CATypeMatchAny;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CAMetadata;
import com.io7m.cardant.protocol.inventory.CAICommandItemSearchBegin;
import com.io7m.cardant.protocol.inventory.CAIResponseItemSearch;
import com.io7m.lanark.core.RDottedName;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed01;
import com.io7m.quarrel.core.QParameterNamed0N;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.io7m.cardant.model.CAItemColumn.BY_NAME;
import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "item-search-begin"
 */

public final class CAShellCmdItemSearchBegin
  extends CAShellCmdAbstractCR<CAICommandItemSearchBegin, CAIResponseItemSearch>
{
  private static final QParameterNamed01<CALocationID> LOCATIONS_EXACT =
    new QParameterNamed01<>(
      "--location-exact",
      List.of(),
      new QConstant(
        "Specify the exact location within which to limit search results."),
      Optional.empty(),
      CALocationID.class
    );

  private static final QParameterNamed01<CALocationID> LOCATIONS_DESCENDANTS =
    new QParameterNamed01<>(
      "--location-with-descendants",
      List.of(),
      new QConstant(
        "Specify the location (and descendants) within which to limit search results."),
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

  private static final QParameterNamed0N<RDottedName> TYPES_ALL_OF =
    new QParameterNamed0N<>(
      "--types-all-of",
      List.of(),
      new QConstant(
        "Only include items that have all of the given types."),
      List.of(),
      RDottedName.class
    );

  private static final QParameterNamed0N<RDottedName> TYPES_ANY_OF =
    new QParameterNamed0N<>(
      "--types-any-of",
      List.of(),
      new QConstant(
        "Only include items that have any of the given types."),
      List.of(),
      RDottedName.class
    );

  private static final QParameterNamed01<String> NAME_EXACT =
    new QParameterNamed01<>(
      "--name-exact",
      List.of(),
      new QConstant(
        "Only include items that have the given name."),
      Optional.empty(),
      String.class
    );

  private static final QParameterNamed01<String> NAME_SEARCH =
    new QParameterNamed01<>(
      "--name-search",
      List.of(),
      new QConstant(
        "Only include items that have names matching the search query."),
      Optional.empty(),
      String.class
    );

  private static final QParameterNamed0N<RDottedName> METADATA_REQUIRE_PRESENT =
    new QParameterNamed0N<>(
      "--metadata-require-present",
      List.of(),
      new QConstant(
        "Only include items that have metadata with the given name."),
      List.of(),
      RDottedName.class
    );

  private static final QParameterNamed0N<CAMetadata> METADATA_REQUIRE_EXACT =
    new QParameterNamed0N<>(
      "--metadata-require-value",
      List.of(),
      new QConstant(
        "Only include items that have metadata with the exact given value."),
      List.of(),
      CAMetadata.class
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
      LOCATIONS_DESCENDANTS,
      LOCATIONS_EXACT,
      NAME_EXACT,
      NAME_SEARCH,
      TYPES_ALL_OF,
      TYPES_ANY_OF,
      METADATA_REQUIRE_PRESENT,
      METADATA_REQUIRE_EXACT
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
      parseLocationMatch(context);
    final var nameMatch =
      parseNameMatch(context);
    final var typeMatch =
      parseTypeMatch(context);
    final var metaMatch =
      parseMetaMatch(context);

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

  private static CAMetadataMatchType parseMetaMatch(
    final QCommandContextType context)
  {
    final var present =
      context.parameterValues(METADATA_REQUIRE_PRESENT);
    final var exact =
      context.parameterValues(METADATA_REQUIRE_EXACT);

    if (present.isEmpty() && exact.isEmpty()) {
      return CAMetadataMatchAny.ANY;
    }

    final var map =
      new HashMap<RDottedName, CAMetadataValueMatchType>(
        present.size() + exact.size()
      );

    for (final var name : present) {
      map.put(name, CAMetadataValueMatchAny.ANY);
    }
    for (final var meta : exact) {
      map.put(meta.name(), new CAMetadataValueMatchExact(meta.value()));
    }
    return new CAMetadataRequire(map);
  }

  private static CATypeMatchType parseTypeMatch(
    final QCommandContextType context)
  {
    final var typesAllOf =
      context.parameterValues(TYPES_ALL_OF);
    final var typesAnyOf =
      context.parameterValues(TYPES_ANY_OF);

    CATypeMatchType typeMatch = CATypeMatchAny.ANY;
    if (!typesAllOf.isEmpty()) {
      typeMatch = new CATypeMatchType.CATypeMatchAllOf(Set.copyOf(typesAllOf));
    }
    if (!typesAnyOf.isEmpty()) {
      typeMatch = new CATypeMatchType.CATypeMatchAnyOf(Set.copyOf(typesAnyOf));
    }
    return typeMatch;
  }

  private static CANameMatchType parseNameMatch(
    final QCommandContextType context)
  {
    final var nameExactOpt =
      context.parameterValue(NAME_EXACT);
    final var nameSearchOpt =
      context.parameterValue(NAME_SEARCH);

    CANameMatchType nameMatch = CANameMatchAny.ANY;
    if (nameExactOpt.isPresent()) {
      nameMatch = new CANameMatchExact(nameExactOpt.get());
    }
    if (nameSearchOpt.isPresent()) {
      nameMatch = new CANameMatchSearch(nameSearchOpt.get());
    }
    return nameMatch;
  }

  private static CAItemLocationMatchType parseLocationMatch(
    final QCommandContextType context)
  {
    final var exactOpt =
      context.parameterValue(LOCATIONS_EXACT);
    final var descendantsOpt =
      context.parameterValue(LOCATIONS_DESCENDANTS);

    CAItemLocationMatchType locationMatch = new CAItemLocationsAll();
    if (exactOpt.isPresent()) {
      locationMatch = new CAItemLocationExact(exactOpt.get());
    }
    if (descendantsOpt.isPresent()) {
      locationMatch = new CAItemLocationWithDescendants(descendantsOpt.get());
    }
    return locationMatch;
  }
}
