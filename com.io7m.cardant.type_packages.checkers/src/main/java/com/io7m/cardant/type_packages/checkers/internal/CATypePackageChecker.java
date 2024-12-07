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


package com.io7m.cardant.type_packages.checkers.internal;

import com.io7m.cardant.model.CATypeField;
import com.io7m.cardant.model.CATypeRecord;
import com.io7m.cardant.model.CATypeRecordFieldIdentifier;
import com.io7m.cardant.model.CATypeRecordIdentifier;
import com.io7m.cardant.model.CATypeScalarIdentifier;
import com.io7m.cardant.model.CATypeScalarType;
import com.io7m.cardant.model.type_package.CANameUnqualified;
import com.io7m.cardant.model.type_package.CATypePackage;
import com.io7m.cardant.model.type_package.CATypePackageDeclaration;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.cardant.type_packages.checker.api.CATypePackageCheckerFailure;
import com.io7m.cardant.type_packages.checker.api.CATypePackageCheckerResultType;
import com.io7m.cardant.type_packages.checker.api.CATypePackageCheckerSuccess;
import com.io7m.cardant.type_packages.checker.api.CATypePackageCheckerType;
import com.io7m.cardant.type_packages.resolver.api.CATypePackageResolverType;
import com.io7m.jaffirm.core.Invariants;
import com.io7m.lanark.core.RDottedName;
import com.io7m.seltzer.api.SStructuredError;
import com.io7m.verona.core.VersionRange;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.io7m.cardant.strings.CAStringConstants.FIELD_NAME;
import static com.io7m.cardant.strings.CAStringConstants.FIELD_TYPE;
import static com.io7m.cardant.strings.CAStringConstants.NAME;
import static com.io7m.cardant.strings.CAStringConstants.NAME_BASE;
import static com.io7m.cardant.strings.CAStringConstants.PACKAGE;
import static com.io7m.cardant.strings.CAStringConstants.TYPE;
import static com.io7m.cardant.strings.CAStringConstants.TYPECHECKER_NAME_QUALIFIED_INVALID;
import static com.io7m.cardant.strings.CAStringConstants.TYPECHECKER_NO_SUCH_PACKAGE;
import static com.io7m.cardant.strings.CAStringConstants.TYPECHECKER_RECORD_FIELD_TYPE_NONEXISTENT;
import static com.io7m.cardant.strings.CAStringConstants.VERSION_RANGE;

/**
 * A type package checker.
 */

public final class CATypePackageChecker
  implements CATypePackageCheckerType
{
  private final CAStrings strings;
  private final CATypePackageResolverType resolver;
  private final CATypePackageDeclaration declaration;
  private final LinkedList<SStructuredError<String>> errors;
  private final HashMap<CATypeScalarIdentifier, CATypeScalarType> typeScalars;
  private final HashMap<CATypeRecordIdentifier, CATypeRecord> typeRecords;
  private final HashMap<CANameUnqualified, CATypeScalarType> typeScalarsUnqualified;
  private final HashMap<CATypeRecordFieldIdentifier, CATypeField> typeRecordFields;
  private final HashMap<CANameUnqualified, CATypeField> typeRecordFieldsUnqualified;

  /**
   * A type package checker.
   *
   * @param inStrings     The string resources
   * @param inDeclaration The package declaration
   * @param inResolver    The type resolver
   */

  public CATypePackageChecker(
    final CAStrings inStrings,
    final CATypePackageResolverType inResolver,
    final CATypePackageDeclaration inDeclaration)
  {
    this.strings =
      Objects.requireNonNull(inStrings, "strings");
    this.resolver =
      Objects.requireNonNull(inResolver, "resolver");
    this.declaration =
      Objects.requireNonNull(inDeclaration, "declaration");
    this.errors =
      new LinkedList<>();
    this.typeScalars =
      new HashMap<>();
    this.typeScalarsUnqualified =
      new HashMap<>();
    this.typeRecords =
      new HashMap<>();
    this.typeRecordFields =
      new HashMap<>();
    this.typeRecordFieldsUnqualified =
      new HashMap<>();
  }

  @Override
  public CATypePackageCheckerResultType execute()
  {
    this.checkImports();
    this.checkTypeScalars();
    this.checkTypeRecords();

    if (this.errors.isEmpty()) {
      return this.buildPackage();
    } else {
      return new CATypePackageCheckerFailure(List.copyOf(this.errors));
    }
  }

  private void checkTypeRecords()
  {
    this.typeRecords.clear();

    for (final var e : this.declaration.recordTypes().entrySet()) {
      final var typeNameUnqualified = e.getKey();
      final var type = e.getValue();

      final CATypeRecordIdentifier typeNameQual;
      try {
        typeNameQual = this.createQualifiedTypeName(typeNameUnqualified);
      } catch (final IllegalArgumentException ex) {
        this.errors.add(this.errorNameQualifiedInvalid(typeNameUnqualified, ex));
        continue;
      }

      this.typeRecordFieldsUnqualified.clear();
      this.typeRecordFields.clear();

      for (final var fe : type.fields().entrySet()) {
        final var fName = fe.getKey();
        final var fDecl = fe.getValue();

        final var fTypePackage =
          fDecl.type().packageName();
        final var packageName =
          this.declaration.identifier().name();

        final Optional<CATypeScalarType> fType;
        if (Objects.equals(fTypePackage, packageName)) {
          final var u = new CANameUnqualified(fDecl.type().typeName().value());
          fType = Optional.ofNullable(this.typeScalarsUnqualified.get(u));
        } else {
          fType = this.resolver.findTypeScalar(fDecl.type());
        }

        if (fType.isEmpty()) {
          this.errors.add(
            this.errorTypeFieldTypeNonexistent(typeNameUnqualified, fName, fDecl.type())
          );
          continue;
        }

        final CATypeRecordFieldIdentifier fieldNameQual;
        try {
          fieldNameQual = createFieldName(typeNameQual, fName);
        } catch (final IllegalArgumentException ex) {
          this.errors.add(this.errorNameQualifiedInvalid(typeNameUnqualified, ex));
          continue;
        }

        final var field =
          new CATypeField(
            fieldNameQual,
            fDecl.description(),
            fType.get(),
            fDecl.isRequired()
          );

        Invariants.checkInvariantV(
          !this.typeRecordFieldsUnqualified.containsKey(fName),
          "Record field cannot already exist."
        );

        this.typeRecordFields.put(fieldNameQual, field);
        this.typeRecordFieldsUnqualified.put(fName, field);
      }

      Invariants.checkInvariantV(
        !this.typeRecords.containsKey(typeNameQual),
        "Record type cannot already exist."
      );

      final var typeRecord =
        new CATypeRecord(
          typeNameQual,
          type.description(),
          Map.copyOf(this.typeRecordFields)
        );

      this.typeRecords.put(typeNameQual, typeRecord);
    }
  }

  private static CATypeRecordFieldIdentifier createFieldName(
    final CATypeRecordIdentifier typeName,
    final CANameUnqualified fName)
  {
    return new CATypeRecordFieldIdentifier(
      typeName,
      new RDottedName(fName.value())
    );
  }

  private CATypeRecordIdentifier createQualifiedTypeName(
    final CANameUnqualified name)
  {
    return new CATypeRecordIdentifier(
      this.declaration.identifier().name(),
      new RDottedName(name.value())
    );
  }

  private CATypeScalarIdentifier createQualifiedTypeScalarName(
    final CANameUnqualified name)
  {
    return new CATypeScalarIdentifier(
      this.declaration.identifier().name(),
      new RDottedName(name.value())
    );
  }

  private SStructuredError<String> errorNameQualifiedInvalid(
    final CANameUnqualified name,
    final IllegalArgumentException ex)
  {
    final var m = new HashMap<String, String>();
    m.put(
      this.strings.format(NAME_BASE),
      this.declaration.identifier().name().value());
    m.put(this.strings.format(NAME), name.value());

    return new SStructuredError<>(
      "error-name-qualified-invalid",
      this.strings.format(TYPECHECKER_NAME_QUALIFIED_INVALID),
      Map.copyOf(m),
      Optional.empty(),
      Optional.of(ex)
    );
  }

  private SStructuredError<String> errorTypeFieldTypeNonexistent(
    final CANameUnqualified typeName,
    final CANameUnqualified fieldName,
    final CATypeScalarIdentifier fieldType)
  {
    final var m = new HashMap<String, String>();
    m.put(this.strings.format(TYPE), typeName.value());
    m.put(this.strings.format(FIELD_NAME), fieldName.value());
    m.put(this.strings.format(FIELD_TYPE), fieldType.toString());

    return new SStructuredError<>(
      "error-type-record-field-type-nonexistent",
      this.strings.format(TYPECHECKER_RECORD_FIELD_TYPE_NONEXISTENT),
      Map.copyOf(m),
      Optional.empty(),
      Optional.empty()
    );
  }

  private void checkTypeScalars()
  {
    this.typeScalars.clear();
    this.typeScalarsUnqualified.clear();

    for (final var e : this.declaration.scalarTypes().entrySet()) {
      final var name = e.getKey();
      final var type = e.getValue();

      final CATypeScalarIdentifier typeNameQual;
      try {
        typeNameQual = this.createQualifiedTypeScalarName(name);
      } catch (final IllegalArgumentException ex) {
        this.errors.add(this.errorNameQualifiedInvalid(name, ex));
        continue;
      }

      final var typeQual =
        type.withName(typeNameQual);

      Invariants.checkInvariantV(
        !this.typeScalars.containsKey(typeNameQual),
        "Scalar type cannot already exist."
      );

      this.typeScalars.put(typeNameQual, typeQual);
      this.typeScalarsUnqualified.put(name, typeQual);
    }
  }

  private CATypePackageCheckerResultType buildPackage()
  {
    return new CATypePackageCheckerSuccess(
      new CATypePackage(
        this.declaration.identifier(),
        this.declaration.description(),
        this.declaration.imports(),
        Map.copyOf(this.typeScalars),
        Map.copyOf(this.typeRecords)
      )
    );
  }

  private void checkImports()
  {
    for (final var importDecl : this.declaration.imports()) {
      final var existingOpt =
        this.resolver.findTypePackageId(
          importDecl.packageName(),
          importDecl.versionRange()
        );

      if (existingOpt.isEmpty()) {
        this.errors.add(this.errorNoSuchPackage(
          importDecl.packageName(),
          importDecl.versionRange())
        );
      }
    }
  }

  private SStructuredError<String> errorNoSuchPackage(
    final RDottedName name,
    final VersionRange versionRange)
  {
    final var m = new HashMap<String, String>();
    m.put(this.strings.format(PACKAGE), name.value());
    m.put(this.strings.format(VERSION_RANGE), versionRange.toString());

    return new SStructuredError<>(
      "error-import-unsatisfied",
      this.strings.format(TYPECHECKER_NO_SUCH_PACKAGE),
      Map.copyOf(m),
      Optional.empty(),
      Optional.empty()
    );
  }
}
