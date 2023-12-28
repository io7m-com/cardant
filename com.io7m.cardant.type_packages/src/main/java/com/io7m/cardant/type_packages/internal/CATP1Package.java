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

import com.io7m.blackthorne.core.BTElementHandlerConstructorType;
import com.io7m.blackthorne.core.BTElementHandlerType;
import com.io7m.blackthorne.core.BTElementParsingContextType;
import com.io7m.blackthorne.core.BTQualifiedName;
import com.io7m.cardant.model.CATypeScalarType;
import com.io7m.cardant.model.type_package.CANameUnqualified;
import com.io7m.cardant.model.type_package.CATypePackageDeclaration;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.model.type_package.CATypePackageImport;
import com.io7m.cardant.model.type_package.CATypeRecordDeclaration;
import com.io7m.lanark.core.RDottedName;
import com.io7m.verona.core.Version;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.io7m.cardant.type_packages.internal.CATP1Names.qName;

/**
 * A parser.
 */

public final class CATP1Package
  implements BTElementHandlerType<Object, CATypePackageDeclaration>
{
  private static final CATP1PackageInfoData PACKAGE_INFO =
    new CATP1PackageInfoData(
      new CATypePackageIdentifier(
        new RDottedName("x"),
        Version.of(1, 0, 0)
      ),
      "Nothing."
    );

  private CATP1PackageInfoData packageInfo;
  private Set<CATypePackageImport> imports;
  private Map<CANameUnqualified, CATypeScalarType> scalarTypes;
  private Map<CANameUnqualified, CATypeRecordDeclaration> recordTypes;

  /**
   * A parser.
   *
   * @param context The parse context
   */

  public CATP1Package(
    final BTElementParsingContextType context)
  {
    this.packageInfo = PACKAGE_INFO;
    this.imports = new HashSet<>();
    this.scalarTypes = new HashMap<>();
    this.recordTypes = new HashMap<>();
  }

  @Override
  public Map<BTQualifiedName, BTElementHandlerConstructorType<?, ?>>
  onChildHandlersRequested(
    final BTElementParsingContextType context)
  {
    return Map.ofEntries(
      Map.entry(
        qName("PackageInfo"), CATP1PackageInfo::new),
      Map.entry(
        qName("Import"), CATP1Import::new),
      Map.entry(
        qName("TypeRecord"), CATP1TypeRecord::new),
      Map.entry(
        qName("TypeScalarMonetary"), CATP1TypeScalarMonetary::new),
      Map.entry(
        qName("TypeScalarIntegral"), CATP1TypeScalarIntegral::new),
      Map.entry(
        qName("TypeScalarReal"), CATP1TypeScalarReal::new),
      Map.entry(
        qName("TypeScalarText"), CATP1TypeScalarText::new),
      Map.entry(
        qName("TypeScalarTime"), CATP1TypeScalarTime::new)
    );
  }

  @Override
  public void onChildValueProduced(
    final BTElementParsingContextType context,
    final Object result)
  {
    switch (result) {
      case final CATP1PackageInfoData data -> {
        this.packageInfo = data;
        return;
      }

      case final CATypePackageImport imp -> {
        this.imports.add(imp);
        return;
      }

      case final CATypeScalarType sc -> {
        this.scalarTypes.put(new CANameUnqualified(sc.name().value()), sc);
        return;
      }

      case final CATypeRecordDeclaration tr -> {
        this.recordTypes.put(tr.name(), tr);
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
  public CATypePackageDeclaration onElementFinished(
    final BTElementParsingContextType context)
  {
    return new CATypePackageDeclaration(
      this.packageInfo.identifier(),
      this.packageInfo.description(),
      this.imports,
      this.scalarTypes,
      this.recordTypes
    );
  }
}
