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

package com.io7m.cardant.model.xml;

import com.io7m.blackthorne.api.BTQualifiedName;
import com.io7m.jxe.core.JXESchemaDefinition;
import com.io7m.jxe.core.JXESchemaResolutionMappings;

import java.net.URI;

/**
 * The inventory schemas.
 */

public final class CAInventorySchemas
{
  private static final URI INVENTORY_1 =
    URI.create("urn:com.io7m.cardant.inventory:1");

  private static final JXESchemaDefinition INVENTORY_1_SCHEMA =
    JXESchemaDefinition.builder()
      .setFileIdentifier("inventory-1.xsd")
      .setLocation(CAInventorySchemas.class.getResource(
        "/com/io7m/cardant/model/xml/inventory-1.xsd"))
      .setNamespace(INVENTORY_1)
      .build();

  private static final JXESchemaResolutionMappings SCHEMAS =
    JXESchemaResolutionMappings.builder()
      .putMappings(INVENTORY_1, INVENTORY_1_SCHEMA)
      .build();

  private CAInventorySchemas()
  {

  }

  /**
   * @return The namespace for the 1.0 inventory schema
   */

  public static URI inventory1Namespace()
  {
    return INVENTORY_1;
  }

  /**
   * @return The 1.0 inventory schema
   */

  public static JXESchemaDefinition inventory1Schema()
  {
    return INVENTORY_1_SCHEMA;
  }

  /**
   * @return The schema mappings
   */

  public static JXESchemaResolutionMappings schemas()
  {
    return SCHEMAS;
  }

  /**
   * @param localName The element local name
   *
   * @return A qualified name in the 1.0 schema
   */

  public static BTQualifiedName element1(
    final String localName)
  {
    return BTQualifiedName.of(INVENTORY_1.toString(), localName);
  }
}
