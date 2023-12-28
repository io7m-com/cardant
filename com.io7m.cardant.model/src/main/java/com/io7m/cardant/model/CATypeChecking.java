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


package com.io7m.cardant.model;

import com.io7m.cardant.error_codes.CAErrorCode;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.strings.CAStringConstants;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.lanark.core.RDottedName;
import com.io7m.seltzer.api.SStructuredError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Functions for performing type checking on metadata.
 */

public final class CATypeChecking
{
  private final CAStrings strings;
  private final Set<CATypeRecord> typeDeclarations;
  private final Set<CAMetadataType> metadata;

  private CATypeChecking(
    final CAStrings inStrings,
    final Set<CATypeRecord> inTypeDeclarations,
    final Set<CAMetadataType> inMetadata)
  {
    this.strings =
      Objects.requireNonNull(inStrings, "inStrings");
    this.typeDeclarations =
      Objects.requireNonNull(inTypeDeclarations, "typeDeclarations");
    this.metadata =
      Objects.requireNonNull(inMetadata, "metadata");
  }

  /**
   * Create a type checker for the given set of types and metadata.
   *
   * @param inStrings          The strings
   * @param inTypeDeclarations The type declarations against which to check metadata
   * @param inMetadata         The metadata
   *
   * @return A type checker
   */

  public static CATypeChecking create(
    final CAStrings inStrings,
    final Set<CATypeRecord> inTypeDeclarations,
    final Set<CAMetadataType> inMetadata)
  {
    return new CATypeChecking(inStrings, inTypeDeclarations, inMetadata);
  }

  /**
   * Execute the type checker.
   *
   * @return The list of type errors, if any occurred
   */

  public List<SStructuredError<CAErrorCode>> execute()
  {
    final var errors =
      new ArrayList<SStructuredError<CAErrorCode>>();

    for (final var typeDeclaration : this.typeDeclarations) {
      errors.addAll(this.executeFor(typeDeclaration));
    }

    return List.copyOf(errors);
  }

  private Collection<SStructuredError<CAErrorCode>> executeFor(
    final CATypeRecord typeDeclaration)
  {
    final var errors =
      new ArrayList<SStructuredError<CAErrorCode>>();
    final var fields =
      typeDeclaration.fields();

    for (final var field : fields.values()) {
      final var foundMetaOpt =
        this.metadata.stream()
          .filter(m -> Objects.equals(m.name(), field.name()))
          .findFirst();

      if (foundMetaOpt.isEmpty()) {
        if (field.isRequired()) {
          errors.add(
            this.errorFieldRequiredMissing(
              typeDeclaration.name(),
              field.name()
            )
          );
        }
        continue;
      }

      this.checkTypeValue(errors, field.type(), foundMetaOpt.get());
    }

    return errors;
  }

  private void checkTypeValue(
    final ArrayList<SStructuredError<CAErrorCode>> errors,
    final CATypeScalarType type,
    final CAMetadataType meta)
  {
    if (type instanceof final CATypeScalarType.Integral typeT
      && meta instanceof final CAMetadataType.Integral metaT) {
      if (!typeT.isValid(metaT.value())) {
        errors.add(
          this.errorFieldInvalid(
            type.name(),
            meta.name(),
            type.name(),
            type.showConstraint(),
            Long.toString(metaT.value())
          )
        );
      }
      return;
    }

    if (type instanceof final CATypeScalarType.Monetary typeT
      && meta instanceof final CAMetadataType.Monetary metaT) {
      if (!typeT.isValid(metaT.value())) {
        errors.add(
          this.errorFieldInvalid(
            type.name(),
            meta.name(),
            type.name(),
            type.showConstraint(),
            metaT.value().toString()
          )
        );
      }
      return;
    }

    if (type instanceof final CATypeScalarType.Real typeT
      && meta instanceof final CAMetadataType.Real metaT) {
      if (!typeT.isValid(metaT.value())) {
        errors.add(
          this.errorFieldInvalid(
            type.name(),
            meta.name(),
            type.name(),
            type.showConstraint(),
            Double.toString(metaT.value())
          )
        );
      }
      return;
    }

    if (type instanceof final CATypeScalarType.Time typeT
      && meta instanceof final CAMetadataType.Time metaT) {
      if (!typeT.isValid(metaT.value())) {
        errors.add(
          this.errorFieldInvalid(
            type.name(),
            meta.name(),
            type.name(),
            type.showConstraint(),
            metaT.value().toString()
          )
        );
      }
      return;
    }

    if (type instanceof final CATypeScalarType.Text typeT
      && meta instanceof final CAMetadataType.Text metaT) {
      if (!typeT.isValid(metaT.value())) {
        errors.add(
          this.errorFieldInvalid(
            type.name(),
            meta.name(),
            type.name(),
            type.showConstraint(),
            metaT.value()
          )
        );
      }
      return;
    }

    errors.add(
      this.errorFieldInvalidUnknown(
        type.name(),
        meta.name(),
        type.showConstraint(),
        meta.valueString()
      )
    );
  }

  private SStructuredError<CAErrorCode> errorFieldInvalidUnknown(
    final RDottedName typeName,
    final RDottedName fieldName,
    final String constraint,
    final String value)
  {
    return new SStructuredError<>(
      CAStandardErrorCodes.errorTypeCheckFieldInvalid(),
      this.strings.format(CAStringConstants.ERROR_TYPE_FIELD_INVALID),
      Map.ofEntries(
        Map.entry("Type", typeName.value()),
        Map.entry("Field", fieldName.value()),
        Map.entry("Constraint", constraint),
        Map.entry("Value", value)
      ),
      Optional.empty(),
      Optional.empty()
    );
  }

  private SStructuredError<CAErrorCode> errorFieldInvalid(
    final RDottedName typeName,
    final RDottedName fieldName,
    final RDottedName fieldTypeName,
    final String constraint,
    final String value)
  {
    return new SStructuredError<>(
      CAStandardErrorCodes.errorTypeCheckFieldInvalid(),
      this.strings.format(CAStringConstants.ERROR_TYPE_FIELD_INVALID),
      Map.ofEntries(
        Map.entry("Type", typeName.value()),
        Map.entry("Field", fieldName.value()),
        Map.entry("Field Type", fieldTypeName.value()),
        Map.entry("Constraint", constraint),
        Map.entry("Value", value)
      ),
      Optional.empty(),
      Optional.empty()
    );
  }

  private SStructuredError<CAErrorCode> errorFieldRequiredMissing(
    final RDottedName typeName,
    final RDottedName fieldName)
  {
    return new SStructuredError<>(
      CAStandardErrorCodes.errorTypeCheckFieldRequiredMissing(),
      this.strings.format(CAStringConstants.ERROR_TYPE_MISSING_REQUIRED_FIELD),
      Map.ofEntries(
        Map.entry("Type", typeName.value()),
        Map.entry("Field", fieldName.value())
      ),
      Optional.empty(),
      Optional.empty()
    );
  }

  @Override
  public String toString()
  {
    return "[CATypeChecking 0x%s]"
      .formatted(Long.toUnsignedString(this.hashCode(), 16));
  }
}
