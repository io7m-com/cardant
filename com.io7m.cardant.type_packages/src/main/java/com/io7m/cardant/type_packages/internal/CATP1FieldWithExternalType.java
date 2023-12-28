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


package com.io7m.cardant.type_packages.internal;

import com.io7m.blackthorne.core.BTElementHandlerType;
import com.io7m.blackthorne.core.BTElementParsingContextType;
import com.io7m.cardant.model.type_package.CANameQualified;
import com.io7m.cardant.model.type_package.CANameUnqualified;
import com.io7m.cardant.model.type_package.CATypeFieldDeclaration;
import com.io7m.lanark.core.RDottedName;
import org.xml.sax.Attributes;

import java.util.Objects;

/**
 * A parser.
 */

public final class CATP1FieldWithExternalType
  implements BTElementHandlerType<Object, CATypeFieldDeclaration>
{
  private CANameUnqualified name;
  private String description;
  private CANameQualified type;
  private boolean required;

  /**
   * A parser.
   *
   * @param context The parse context
   */

  public CATP1FieldWithExternalType(
    final BTElementParsingContextType context)
  {

  }

  @Override
  public void onElementStart(
    final BTElementParsingContextType context,
    final Attributes attributes)
  {
    this.name =
      new CANameUnqualified(attributes.getValue("Name"));
    this.description =
      attributes.getValue("Description");
    this.type =
      new CANameQualified(new RDottedName(attributes.getValue("Type")));
    this.required =
      Boolean.parseBoolean(
        Objects.requireNonNullElse(
          attributes.getValue("Required"),
          "true"
        )
      );
  }

  @Override
  public CATypeFieldDeclaration onElementFinished(
    final BTElementParsingContextType context)
  {
    return new CATypeFieldDeclaration(
      this.name,
      this.description,
      this.type,
      this.required
    );
  }
}
