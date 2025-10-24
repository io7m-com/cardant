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

package com.io7m.cardant.tests.protocol.json;

import com.io7m.cardant.model.CAIncludeDeleted;
import com.io7m.cardant.model.CAItemColumn;
import com.io7m.cardant.model.CAItemColumnOrdering;
import com.io7m.cardant.model.CAItemSearchParameters;
import com.io7m.cardant.model.CAMetadataElementMatchType;
import com.io7m.cardant.model.CAMetadataValueMatchType;
import com.io7m.cardant.model.comparisons.CAComparisonExactType;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType;
import com.io7m.cardant.model.comparisons.CAComparisonSetType;
import com.io7m.cardant.protocol.inventory.CAICommandDebugInvalid;
import com.io7m.cardant.protocol.inventory.CAICommandDebugRandom;
import com.io7m.cardant.protocol.inventory.CAICommandItemSearchBegin;
import com.io7m.cardant.protocol.inventory.CAIMessageType;
import com.io7m.cardant.protocol.inventory.CAITransaction;
import com.io7m.cardant.protocol.inventory.json.CAIJ1Messages;
import net.jqwik.api.Arbitraries;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class CJ1ProtocolTest
{
  @TestFactory
  public Stream<DynamicTest> testRoundTripCommands()
  {
    return Stream.of(
      com.io7m.cardant.protocol.inventory.CAICommandAuditSearchBegin.class,
      com.io7m.cardant.protocol.inventory.CAICommandAuditSearchNext.class,
      com.io7m.cardant.protocol.inventory.CAICommandAuditSearchPrevious.class,
      com.io7m.cardant.protocol.inventory.CAICommandFileDelete.class,
      com.io7m.cardant.protocol.inventory.CAICommandFileGet.class,
      com.io7m.cardant.protocol.inventory.CAICommandFilePut.class,
      com.io7m.cardant.protocol.inventory.CAICommandFileSearchBegin.class,
      com.io7m.cardant.protocol.inventory.CAICommandFileSearchNext.class,
      com.io7m.cardant.protocol.inventory.CAICommandFileSearchPrevious.class,
      com.io7m.cardant.protocol.inventory.CAICommandItemAttachmentAdd.class,
      com.io7m.cardant.protocol.inventory.CAICommandItemAttachmentRemove.class,
      com.io7m.cardant.protocol.inventory.CAICommandItemCreate.class,
      com.io7m.cardant.protocol.inventory.CAICommandItemDelete.class,
      com.io7m.cardant.protocol.inventory.CAICommandItemGet.class,
      com.io7m.cardant.protocol.inventory.CAICommandItemMetadataPut.class,
      com.io7m.cardant.protocol.inventory.CAICommandItemMetadataRemove.class,
      com.io7m.cardant.protocol.inventory.CAICommandItemSearchBegin.class,
      com.io7m.cardant.protocol.inventory.CAICommandItemSearchNext.class,
      com.io7m.cardant.protocol.inventory.CAICommandItemSearchPrevious.class,
      com.io7m.cardant.protocol.inventory.CAICommandItemSetName.class,
      com.io7m.cardant.protocol.inventory.CAICommandItemTypesAssign.class,
      com.io7m.cardant.protocol.inventory.CAICommandItemTypesRevoke.class,
      com.io7m.cardant.protocol.inventory.CAICommandLocationAttachmentAdd.class,
      com.io7m.cardant.protocol.inventory.CAICommandLocationAttachmentRemove.class,
      com.io7m.cardant.protocol.inventory.CAICommandLocationDelete.class,
      com.io7m.cardant.protocol.inventory.CAICommandLocationGet.class,
      com.io7m.cardant.protocol.inventory.CAICommandLocationList.class,
      com.io7m.cardant.protocol.inventory.CAICommandLocationMetadataPut.class,
      com.io7m.cardant.protocol.inventory.CAICommandLocationMetadataRemove.class,
      com.io7m.cardant.protocol.inventory.CAICommandLocationPut.class,
      com.io7m.cardant.protocol.inventory.CAICommandLocationTypesAssign.class,
      com.io7m.cardant.protocol.inventory.CAICommandLocationTypesRevoke.class,
      com.io7m.cardant.protocol.inventory.CAICommandLogin.class,
      com.io7m.cardant.protocol.inventory.CAICommandRolesAssign.class,
      com.io7m.cardant.protocol.inventory.CAICommandRolesGet.class,
      com.io7m.cardant.protocol.inventory.CAICommandRolesRevoke.class,
      com.io7m.cardant.protocol.inventory.CAICommandStockCount.class,
      com.io7m.cardant.protocol.inventory.CAICommandStockReposit.class,
      com.io7m.cardant.protocol.inventory.CAICommandStockSearchBegin.class,
      com.io7m.cardant.protocol.inventory.CAICommandStockSearchNext.class,
      com.io7m.cardant.protocol.inventory.CAICommandStockSearchPrevious.class,
      com.io7m.cardant.protocol.inventory.CAICommandTypePackageGetText.class,
      com.io7m.cardant.protocol.inventory.CAICommandTypePackageInstall.class,
      com.io7m.cardant.protocol.inventory.CAICommandTypePackageSearchBegin.class,
      com.io7m.cardant.protocol.inventory.CAICommandTypePackageSearchNext.class,
      com.io7m.cardant.protocol.inventory.CAICommandTypePackageSearchPrevious.class,
      com.io7m.cardant.protocol.inventory.CAICommandTypePackageUninstall.class,
      com.io7m.cardant.protocol.inventory.CAICommandTypePackageUpgrade.class
    ).map(CJ1ProtocolTest::roundTrip);
  }

  private static DynamicTest roundTrip(
    final Class<? extends CAIMessageType> c)
  {
    return DynamicTest.dynamicTest(
      "testRoundTrip_%s".formatted(c),
      () -> {
        final var inputMessage =
          scrub(Arbitraries.defaultFor(c).sample());

        final var messages =
          new CAIJ1Messages();
        final var output =
          messages.serialize(inputMessage);

        System.out.println(new String(output, StandardCharsets.UTF_8));

        final var parsed = messages.parse(output);
        assertEquals(inputMessage, parsed);
      });
  }

  private static CAIMessageType scrub(
    final CAIMessageType sample)
  {
    if (sample instanceof final CAITransaction transaction) {
      return new CAITransaction(
        transaction.commands()
          .stream()
          .filter(c -> !(c instanceof CAICommandDebugInvalid))
          .filter(c -> !(c instanceof CAICommandDebugRandom))
          .toList()
      );
    }
    return sample;
  }

  @TestFactory
  public Stream<DynamicTest> testRoundTripResponses()
  {
    return Stream.of(
      com.io7m.cardant.protocol.inventory.CAIResponseAuditSearch.class,
      com.io7m.cardant.protocol.inventory.CAIResponseError.class,
      com.io7m.cardant.protocol.inventory.CAIResponseFileDelete.class,
      com.io7m.cardant.protocol.inventory.CAIResponseFileGet.class,
      com.io7m.cardant.protocol.inventory.CAIResponseFilePut.class,
      com.io7m.cardant.protocol.inventory.CAIResponseFileSearch.class,
      com.io7m.cardant.protocol.inventory.CAIResponseItemAttachmentAdd.class,
      com.io7m.cardant.protocol.inventory.CAIResponseItemAttachmentRemove.class,
      com.io7m.cardant.protocol.inventory.CAIResponseItemCreate.class,
      com.io7m.cardant.protocol.inventory.CAIResponseItemDelete.class,
      com.io7m.cardant.protocol.inventory.CAIResponseItemGet.class,
      com.io7m.cardant.protocol.inventory.CAIResponseItemMetadataPut.class,
      com.io7m.cardant.protocol.inventory.CAIResponseItemMetadataRemove.class,
      com.io7m.cardant.protocol.inventory.CAIResponseItemSearch.class,
      com.io7m.cardant.protocol.inventory.CAIResponseItemSetName.class,
      com.io7m.cardant.protocol.inventory.CAIResponseItemTypesAssign.class,
      com.io7m.cardant.protocol.inventory.CAIResponseItemTypesRevoke.class,
      com.io7m.cardant.protocol.inventory.CAIResponseLocationAttachmentAdd.class,
      com.io7m.cardant.protocol.inventory.CAIResponseLocationAttachmentRemove.class,
      com.io7m.cardant.protocol.inventory.CAIResponseLocationDelete.class,
      com.io7m.cardant.protocol.inventory.CAIResponseLocationGet.class,
      com.io7m.cardant.protocol.inventory.CAIResponseLocationList.class,
      com.io7m.cardant.protocol.inventory.CAIResponseLocationMetadataPut.class,
      com.io7m.cardant.protocol.inventory.CAIResponseLocationMetadataRemove.class,
      com.io7m.cardant.protocol.inventory.CAIResponseLocationPut.class,
      com.io7m.cardant.protocol.inventory.CAIResponseLocationTypesAssign.class,
      com.io7m.cardant.protocol.inventory.CAIResponseLocationTypesRevoke.class,
      com.io7m.cardant.protocol.inventory.CAIResponseLogin.class,
      com.io7m.cardant.protocol.inventory.CAIResponseRolesAssign.class,
      com.io7m.cardant.protocol.inventory.CAIResponseRolesGet.class,
      com.io7m.cardant.protocol.inventory.CAIResponseRolesRevoke.class,
      com.io7m.cardant.protocol.inventory.CAIResponseStockCount.class,
      com.io7m.cardant.protocol.inventory.CAIResponseStockReposit.class,
      com.io7m.cardant.protocol.inventory.CAIResponseStockSearch.class,
      com.io7m.cardant.protocol.inventory.CAIResponseTypePackageGetText.class,
      com.io7m.cardant.protocol.inventory.CAIResponseTypePackageInstall.class,
      com.io7m.cardant.protocol.inventory.CAIResponseTypePackageSearch.class,
      com.io7m.cardant.protocol.inventory.CAIResponseTypePackageUninstall.class,
      com.io7m.cardant.protocol.inventory.CAIResponseTypePackageUpgrade.class
    ).map(CJ1ProtocolTest::roundTrip);
  }

  @TestFactory
  public Stream<DynamicTest> testRoundTripTransactionResponses()
  {
    return Stream.of(
      com.io7m.cardant.protocol.inventory.CAITransactionResponse.class
    ).map(CJ1ProtocolTest::roundTrip);
  }

  @TestFactory
  public Stream<DynamicTest> testRoundTripTransaction()
  {
    return Stream.of(
      com.io7m.cardant.protocol.inventory.CAITransaction.class
    ).map(CJ1ProtocolTest::roundTrip);
  }

  @Test
  public void testMetadataAnyValue()
  {
    System.out.println(
      new String(
        new CAIJ1Messages()
          .serialize(
            new CAICommandItemSearchBegin(
              new CAItemSearchParameters(
                new CAComparisonFuzzyType.Anything<>(),
                new CAComparisonFuzzyType.Anything<>(),
                new CAComparisonSetType.Anything<>(),
                new CAMetadataElementMatchType.Specific(
                  new CAComparisonExactType.Anything<>(),
                  new CAComparisonExactType.Anything<>(),
                  new CAComparisonExactType.Anything<>(),
                  CAMetadataValueMatchType.AnyValue.ANY_VALUE
                ),
                CAIncludeDeleted.INCLUDE_ONLY_LIVE,
                new CAItemColumnOrdering(
                  CAItemColumn.BY_ID,
                  true
                ),
                100L
              )
            )
          )
      )
    );
  }
}
