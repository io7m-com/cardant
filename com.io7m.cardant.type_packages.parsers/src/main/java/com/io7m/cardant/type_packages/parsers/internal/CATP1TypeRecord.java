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


package com.io7m.cardant.type_packages.parsers.internal;

import com.io7m.blackthorne.core.BTElementHandlerConstructorType;
import com.io7m.blackthorne.core.BTElementHandlerType;
import com.io7m.blackthorne.core.BTElementParsingContextType;
import com.io7m.blackthorne.core.BTQualifiedName;
import com.io7m.cardant.model.CATypeRecordIdentifier;
import com.io7m.cardant.model.type_package.CANameUnqualified;
import com.io7m.cardant.model.type_package.CATypeFieldDeclaration;
import com.io7m.cardant.model.type_package.CATypeRecordDeclaration;
import com.io7m.lanark.core.RDottedName;
import org.xml.sax.Attributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.io7m.cardant.type_packages.parsers.internal.CATP1Names.qName;

/**
 * A parser.
 */

public final class CATP1TypeRecord
  implements BTElementHandlerType<Object, CATypeRecordDeclaration>
{
  private final HashMap<CANameUnqualified, CATypeFieldDeclaration> fields;
  private final RDottedName packageName;
  private CANameUnqualified name;
  private String description;
  private CATypeRecordIdentifier fullName;

  /**
   * A parser.
   *
   * @param context The parse context
   * @param inPackageName The package name
   */

  public CATP1TypeRecord(
    final BTElementParsingContextType context,
    final RDottedName inPackageName)
  {
    this.fields =
      new HashMap<>();
    this.packageName =
      Objects.requireNonNull(inPackageName, "packageName");
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

    this.fullName =
      new CATypeRecordIdentifier(
        this.packageName,
        new RDottedName(this.name.value())
      );
  }

  @Override
  public Map<BTQualifiedName, BTElementHandlerConstructorType<?, ?>>
  onChildHandlersRequested(
    final BTElementParsingContextType context)
  {
    return Map.ofEntries(
      Map.entry(qName("Field"), c -> {
        return new CATP1Field(c, this.fullName);
      }),
      Map.entry(qName("FieldWithExternalType"), CATP1FieldWithExternalType::new)
    );
  }

  @Override
  public void onChildValueProduced(
    final BTElementParsingContextType context,
    final Object result)
  {
    switch (result) {
      case final CATypeFieldDeclaration f -> {
        this.fields.put(f.name(), f);
        return;
      }
      default -> {
        throw new IllegalStateException(
          "Unexpected value: %s".formatted(result)
        );
      }
    }
  }

  @Override
  public CATypeRecordDeclaration onElementFinished(
    final BTElementParsingContextType context)
  {
    return new CATypeRecordDeclaration(
      this.name,
      this.description,
      Map.copyOf(this.fields)
    );
  }
}
