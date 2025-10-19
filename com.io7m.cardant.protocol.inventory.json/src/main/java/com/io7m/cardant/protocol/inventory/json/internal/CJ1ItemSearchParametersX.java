/*
 * Copyright © 2025 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.protocol.inventory.json.internal;

import com.io7m.cardant.model.CAItemSearchParameters;
import com.io7m.cardant.model.CATypeRecordIdentifier;

import java.util.Set;
import java.util.stream.Collectors;

import static com.io7m.cardant.protocol.inventory.json.internal.CJ1IncludeDeletedX.INCLUDE_DELETED;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1ItemColumnOrderingX.ITEM_COLUMN_ORDERING;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1MetadataElementMatchX.METADATA_MATCH;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1TypeRecordIdentifierX.TYPE_RECORD_IDENTIFIER;

public enum CJ1ItemSearchParametersX
  implements CJ1SerialBijectionType<CJ1ItemSearchParameters, CAItemSearchParameters>
{
  ITEM_SEARCH_PARAMETERS;

  @Override
  public CAItemSearchParameters toCore(
    final CJ1ItemSearchParameters m)
  {
    return new CAItemSearchParameters(
      new CJ1ComparisonFuzzyX<String>().toCore(m.nameMatch()),
      new CJ1ComparisonFuzzyX<String>().toCore(m.descriptionMatch()),
      new CJ1ComparisonSetX<>(
        CJ1ItemSearchParametersX::serializeTypeRecordIdentifiers,
        CJ1ItemSearchParametersX::validateTypeRecordIdentifiers
      ).toCore(m.typeMatch()),
      METADATA_MATCH.toCore(m.metadataMatch()),
      INCLUDE_DELETED.toCore(m.includeDeleted()),
      ITEM_COLUMN_ORDERING.toCore(m.ordering()),
      m.pageSize()
    );
  }

  @Override
  public CJ1ItemSearchParameters toCJ1(
    final CAItemSearchParameters m)
  {
    return new CJ1ItemSearchParameters(
      new CJ1ComparisonFuzzyX<String>().toCJ1(m.nameMatch()),
      new CJ1ComparisonFuzzyX<String>().toCJ1(m.descriptionMatch()),
      new CJ1ComparisonSetX<>(
        CJ1ItemSearchParametersX::serializeTypeRecordIdentifiers,
        CJ1ItemSearchParametersX::validateTypeRecordIdentifiers
      ).toCJ1(m.typeMatch()),
      METADATA_MATCH.toCJ1(m.metadataMatch()),
      INCLUDE_DELETED.toCJ1(m.includeDeleted()),
      ITEM_COLUMN_ORDERING.toCJ1(m.ordering()),
      m.pageSize()
    );
  }

  private static Set<CJ1TypeRecordIdentifier> serializeTypeRecordIdentifiers(
    final Set<CATypeRecordIdentifier> i)
  {
    return i.stream()
      .map(TYPE_RECORD_IDENTIFIER::toCJ1)
      .collect(Collectors.toSet());
  }

  private static Set<CATypeRecordIdentifier> validateTypeRecordIdentifiers(
    final Set<CJ1TypeRecordIdentifier> s)
  {
    return s.stream()
      .map(TYPE_RECORD_IDENTIFIER::toCore)
      .collect(Collectors.toSet());
  }
}
