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

import com.io7m.blackthorne.api.BTElementHandlerConstructorType;
import com.io7m.blackthorne.api.BTElementHandlerType;
import com.io7m.blackthorne.api.BTElementParsingContextType;
import com.io7m.blackthorne.api.BTQualifiedName;

import java.util.Map;

import static com.io7m.cardant.database.derby.internal.xml.CADatabaseXML.element;

/**
 * A parser for database schemas.
 */

public final class CADatabaseV1SchemaDeclSetParser
  implements BTElementHandlerType<CADatabaseSchemaDecl, CADatabaseSchemaSetDecl>
{
  private final CADatabaseSchemaSetDecl.Builder schemas;

  /**
   * A parser for database schemas.
   *
   * @param context The context
   */

  public CADatabaseV1SchemaDeclSetParser(
    final BTElementParsingContextType context)
  {
    this.schemas = CADatabaseSchemaSetDecl.builder();
  }

  @Override
  public Map<BTQualifiedName, BTElementHandlerConstructorType<?, ? extends CADatabaseSchemaDecl>>
  onChildHandlersRequested(
    final BTElementParsingContextType context)
  {
    return Map.ofEntries(
      Map.entry(
        element("Schema"),
        CADatabaseV1SchemaDeclParser::new
      )
    );
  }

  @Override
  public void onChildValueProduced(
    final BTElementParsingContextType context,
    final CADatabaseSchemaDecl result)
  {
    this.schemas.addSchemas(result);
  }

  @Override
  public CADatabaseSchemaSetDecl onElementFinished(
    final BTElementParsingContextType context)
  {
    return this.schemas.build();
  }
}
