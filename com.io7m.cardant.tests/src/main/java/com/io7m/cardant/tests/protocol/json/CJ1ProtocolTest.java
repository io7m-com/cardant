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

import com.io7m.cardant.protocol.inventory.CAICommandAuditSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandAuditSearchNext;
import com.io7m.cardant.protocol.inventory.CAICommandAuditSearchPrevious;
import com.io7m.cardant.protocol.inventory.CAICommandFileDelete;
import com.io7m.cardant.protocol.inventory.CAICommandFileGet;
import com.io7m.cardant.protocol.inventory.CAICommandFilePut;
import com.io7m.cardant.protocol.inventory.CAICommandFileSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandFileSearchNext;
import com.io7m.cardant.protocol.inventory.CAICommandFileSearchPrevious;
import com.io7m.cardant.protocol.inventory.CAICommandItemCreate;
import com.io7m.cardant.protocol.inventory.CAICommandItemDelete;
import com.io7m.cardant.protocol.inventory.CAICommandItemGet;
import com.io7m.cardant.protocol.inventory.CAICommandItemSetName;
import com.io7m.cardant.protocol.inventory.CAICommandLocationDelete;
import com.io7m.cardant.protocol.inventory.CAICommandLocationGet;
import com.io7m.cardant.protocol.inventory.CAICommandLogin;
import com.io7m.cardant.protocol.inventory.CAIMessageType;
import com.io7m.cardant.protocol.inventory.json.CAIJ1Messages;
import net.jqwik.api.Arbitraries;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class CJ1ProtocolTest
{
  @TestFactory
  public Stream<DynamicTest> testRoundTrip()
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
          Arbitraries.defaultFor(c).sample();
        final var messages =
          new CAIJ1Messages();
        final var output =
          messages.serialize(inputMessage);

        System.out.println(new String(output, StandardCharsets.UTF_8));

        final var parsed = messages.parse(output);
        assertEquals(inputMessage, parsed);
      });
  }
}
