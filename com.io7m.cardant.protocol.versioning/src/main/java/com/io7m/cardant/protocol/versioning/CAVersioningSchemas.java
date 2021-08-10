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

package com.io7m.cardant.protocol.versioning;

import com.io7m.blackthorne.api.BTQualifiedName;
import com.io7m.jxe.core.JXESchemaDefinition;
import com.io7m.jxe.core.JXESchemaResolutionMappings;

import java.net.URI;

/**
 * The inventory protocol 1.0 schemas.
 */

public final class CAVersioningSchemas
{
  private static final URI NAMESPACE =
    URI.create("urn:com.io7m.cardant.server.versioning:1");

  private static final JXESchemaDefinition NAMESPACE_SCHEMA =
    JXESchemaDefinition.builder()
      .setFileIdentifier("versioning.xsd")
      .setLocation(CAVersioningSchemas.class.getResource(
        "/com/io7m/cardant/protocol/versioning/versioning.xsd"))
      .setNamespace(NAMESPACE)
      .build();

  private static final JXESchemaResolutionMappings SCHEMAS =
    JXESchemaResolutionMappings.builder()
      .putMappings(NAMESPACE, NAMESPACE_SCHEMA)
      .build();

  private CAVersioningSchemas()
  {

  }

  /**
   * @return The namespace
   */

  public static URI namespace()
  {
    return NAMESPACE;
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
   * @return A qualified name in the namespace
   */

  public static BTQualifiedName element(
    final String localName)
  {
    return BTQualifiedName.of(NAMESPACE.toString(), localName);
  }
}
