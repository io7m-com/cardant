/*
 * Copyright © 2021 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.protocol.inventory.v1;

import com.io7m.blackthorne.api.BTQualifiedName;
import com.io7m.cardant.model.xml.CAInventorySchemas;
import com.io7m.jxe.core.JXESchemaDefinition;
import com.io7m.jxe.core.JXESchemaResolutionMappings;

import java.net.URI;

/**
 * The inventory protocol 1.0 schemas.
 */

public final class CA1InventorySchemas
{
  private static final URI PROTOCOL_1 =
    URI.create("urn:com.io7m.cardant.inventory.protocol:1");

  private static final JXESchemaDefinition PROTOCOL_1_SCHEMA =
    JXESchemaDefinition.builder()
      .setFileIdentifier("inventory-protocol-1.xsd")
      .setLocation(CA1InventorySchemas.class.getResource(
        "/com/io7m/cardant/protocol/inventory/v1/inventory-protocol-1.xsd"))
      .setNamespace(PROTOCOL_1)
      .build();

  private static final JXESchemaResolutionMappings SCHEMAS =
    JXESchemaResolutionMappings.builder()
      .putMappings(PROTOCOL_1, PROTOCOL_1_SCHEMA)
      .putMappings(
        CAInventorySchemas.inventory1Namespace(),
        CAInventorySchemas.inventory1Schema())
      .build();

  private CA1InventorySchemas()
  {

  }

  /**
   * @return The 1.0 namespace
   */

  public static URI protocol1Namespace()
  {
    return PROTOCOL_1;
  }

  /**
   * @return The schema mappings
   */

  public static JXESchemaResolutionMappings schemas()
  {
    return SCHEMAS;
  }

  /**
   * @param localName The local element name
   *
   * @return A qualified name in the 1.0 namespace
   */

  public static BTQualifiedName element1(
    final String localName)
  {
    return BTQualifiedName.of(PROTOCOL_1.toString(), localName);
  }
}
