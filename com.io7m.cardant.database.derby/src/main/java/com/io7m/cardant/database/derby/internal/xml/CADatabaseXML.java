/*
 * Copyright © 2021 Mark Raynsford <code@io7m.com> http://io7m.com
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

package com.io7m.cardant.database.derby.internal.xml;

import com.io7m.blackthorne.api.BTException;
import com.io7m.blackthorne.api.BTQualifiedName;
import com.io7m.blackthorne.jxe.BlackthorneJXE;
import com.io7m.jxe.core.JXESchemaDefinition;
import com.io7m.jxe.core.JXESchemaResolutionMappings;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

/**
 * Database XML schemas.
 */

public final class CADatabaseXML
{
  /**
   * The 1.0 schema.
   */

  public static final JXESchemaDefinition SCHEMA_1_0 =
    JXESchemaDefinition.builder()
      .setFileIdentifier("statements-1.xsd")
      .setLocation(CADatabaseXML.class.getResource(
        "/com/io7m/cardant/database/derby/internal/statements-1.xsd"))
      .setNamespace(URI.create("urn:com.io7m.cardant.database.statements:1:0"))
      .build();

  /**
   * The set of supported schemas.
   */

  public static final JXESchemaResolutionMappings SCHEMA_MAPPINGS =
    JXESchemaResolutionMappings.builder()
      .putMappings(SCHEMA_1_0.namespace(), SCHEMA_1_0)
      .build();

  private CADatabaseXML()
  {

  }

  /**
   * The element with the given name.
   *
   * @param localName The local name
   *
   * @return The qualified name
   */

  public static BTQualifiedName element(
    final String localName)
  {
    return BTQualifiedName.of(
      SCHEMA_1_0.namespace().toString(),
      localName
    );
  }

  /**
   * Parse a set of schemas.
   *
   * @param uri    The uri
   * @param stream The stream
   *
   * @return A parsed set of schemas
   */

  public static CADatabaseSchemaSetDecl parse(
    final URI uri,
    final InputStream stream)
    throws IOException
  {
    try {
      return BlackthorneJXE.parse(
        uri,
        stream,
        Map.ofEntries(
          Map.entry(
            element("Schemas"),
            CADatabaseV1SchemaDeclSetParser::new
          )
        ),
        SCHEMA_MAPPINGS
      );
    } catch (final BTException e) {
      throw new IOException(e);
    }
  }
}
