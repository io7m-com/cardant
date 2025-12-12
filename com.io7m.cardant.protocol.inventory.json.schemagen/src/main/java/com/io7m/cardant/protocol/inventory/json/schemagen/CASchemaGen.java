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

package com.io7m.cardant.protocol.inventory.json.schemagen;

import com.io7m.cardant.protocol.inventory.json.internal.CJ1MessageType;
import com.io7m.sumjack.core.SjException;
import com.io7m.sumjack.core.SjGeneratorConfiguration;
import com.io7m.sumjack.core.SjGenerators;
import com.io7m.sumjack.core.SjSchemaVersion;
import com.io7m.sumjack.core.standard.SjBase64ByteArray;
import com.io7m.sumjack.core.standard.SjBigDecimal;
import com.io7m.sumjack.core.standard.SjOffsetDateTime;
import com.io7m.sumjack.core.standard.SjUUID;
import com.io7m.sumjack.lanark.SjDottedName;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.nio.file.Paths;

import static com.io7m.cardant.protocol.inventory.json.schemagen.CADefinitionCAErrorCode.CA_ERROR_CODE;
import static com.io7m.cardant.protocol.inventory.json.schemagen.CADefinitionCurrencyUnit.CURRENCY_UNIT;

public final class CASchemaGen
{
  private CASchemaGen()
  {

  }

  public static void main(
    final String[] args)
    throws Exception
  {
    final var outputFile =
      Paths.get(args[0]).toAbsolutePath();

    final var configuration =
      SjGeneratorConfiguration.builder()
        .addDefinitions(CADefinitionTypePackageIdentifier.CA_TYPE_PACKAGE_IDENTIFIER)
        .addDefinitions(CADefinitionTypeRecordFieldIdentifier.CA_TYPE_RECORD_FIELD_IDENTIFIER)
        .addDefinitions(CADefinitionTypeRecordIdentifier.CA_TYPE_RECORD_IDENTIFIER)
        .addDefinitions(CAUnsignedInt.CA_UNSIGNED_INT)
        .addDefinitions(CAUnsignedLong.CA_UNSIGNED_LONG)
        .addDefinitions(CA_ERROR_CODE)
        .addDefinitions(CURRENCY_UNIT)
        .addDefinitions(SjBase64ByteArray.BASE64_BYTE_ARRAY)
        .addDefinitions(SjBigDecimal.BIG_DECIMAL)
        .addDefinitions(SjDottedName.DOTTED_NAME)
        .addDefinitions(SjOffsetDateTime.OFFSET_DATE_TIME)
        .addDefinitions(SjUUID.UUID)
        .setId(URI.create("urn:com.io7m.cardant.inventory:1.0"))
        .setMapper(JsonMapper.shared())
        .setRootType(CJ1MessageType.class)
        .setSchemaVersion(SjSchemaVersion.DRAFT_2020_12)
        .setTitle("Cardant Inventory 1.0")
        .build();

    try {
      SjGenerators.create(configuration)
        .executeAndWrite(outputFile);
    } catch (final SjException e) {
      e.printStackTrace(System.err);
      for (final var ea : e.attributes().entrySet()) {
        System.err.println(ea.getKey() + ": " + ea.getValue());
      }
      throw e;
    }
  }
}
