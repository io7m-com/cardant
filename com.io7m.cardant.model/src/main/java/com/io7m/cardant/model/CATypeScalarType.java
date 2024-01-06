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

import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.jranges.RangeInclusiveD;
import com.io7m.jranges.RangeInclusiveL;
import com.io7m.lanark.core.RDottedName;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * The type of scalar types.
 */

public sealed interface CATypeScalarType
{
  /**
   * @return The package to which this type belongs
   */

  CATypePackageIdentifier packageIdentifier();

  /**
   * The kind of scalar type.
   */

  enum Kind
  {
    /**
     * SCALAR_INTEGRAL
     */

    SCALAR_INTEGRAL,

    /**
     * SCALAR_TEXT
     */

    SCALAR_TEXT,

    /**
     * SCALAR_TIME
     */

    SCALAR_TIME,

    /**
     * SCALAR_MONETARY
     */

    SCALAR_MONETARY,

    /**
     * SCALAR_REAL
     */

    SCALAR_REAL
  }

  /**
   * @param newName The new name
   *
   * @return This type with a new name
   */

  CATypeScalarType withName(RDottedName newName);

  /**
   * @return The type name
   */

  RDottedName name();

  /**
   * @return A humanly-readable description of the type
   */

  String description();

  /**
   * @return The internal type kind
   */

  Kind kind();

  /**
   * @return A humanly-readable description of this type's value constraints
   */

  String showConstraint();

  /**
   * An integer type.
   *
   * @param packageIdentifier The package to which this type belongs
   * @param name              The type name
   * @param description       A humanly-readable description of the type
   * @param rangeLower        The lower bound (inclusive)
   * @param rangeUpper        The upper bound (inclusive)
   */

  record Integral(
    CATypePackageIdentifier packageIdentifier,
    RDottedName name,
    String description,
    long rangeLower,
    long rangeUpper)
    implements CATypeScalarType
  {
    /**
     * An integer type.
     */

    public Integral
    {
      Objects.requireNonNull(packageIdentifier, "packageIdentifier");
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(description, "description");
    }

    @Override
    public CATypeScalarType withName(
      final RDottedName newName)
    {
      return new Integral(
        this.packageIdentifier,
        newName,
        this.description,
        this.rangeLower,
        this.rangeUpper
      );
    }

    @Override
    public Kind kind()
    {
      return Kind.SCALAR_INTEGRAL;
    }

    @Override
    public String showConstraint()
    {
      return String.format(
        "∀x, %d <= x <= %d",
        Long.valueOf(this.rangeLower),
        Long.valueOf(this.rangeUpper)
      );
    }

    /**
     * @param value The value
     *
     * @return {@code true} if the given value is in the range for this type
     */

    public boolean isValid(
      final long value)
    {
      return RangeInclusiveL.of(this.rangeLower, this.rangeUpper)
        .includesValue(value);
    }
  }

  /**
   * A text type.
   *
   * @param packageIdentifier The package to which this type belongs
   * @param name              The type name
   * @param description       A humanly-readable description of the type
   * @param pattern           The pattern that constrains text values
   */

  record Text(
    CATypePackageIdentifier packageIdentifier,
    RDottedName name,
    String description,
    String pattern)
    implements CATypeScalarType
  {
    /**
     * A text type.
     */

    public Text
    {
      Objects.requireNonNull(packageIdentifier, "packageIdentifier");
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(description, "description");
      Objects.requireNonNull(pattern, "pattern");

      Pattern.compile(pattern);
    }

    @Override
    public Kind kind()
    {
      return Kind.SCALAR_TEXT;
    }

    @Override
    public CATypeScalarType withName(
      final RDottedName newName)
    {
      return new Text(
        this.packageIdentifier,
        newName,
        this.description,
        this.pattern
      );
    }

    @Override
    public String showConstraint()
    {
      return String.format("∀x, x ~= %s", this.pattern);
    }

    /**
     * @param value The value
     *
     * @return {@code true} if the given value is in the range for this type
     */

    public boolean isValid(
      final String value)
    {
      return Pattern.compile(this.pattern)
        .matcher(value)
        .matches();
    }
  }

  /**
   * A time type.
   *
   * @param packageIdentifier The package to which this type belongs
   * @param name              The type name
   * @param description       A humanly-readable description of the type
   * @param rangeLower        The lower bound (inclusive)
   * @param rangeUpper        The upper bound (inclusive)
   */

  record Time(
    CATypePackageIdentifier packageIdentifier,
    RDottedName name,
    String description,
    OffsetDateTime rangeLower,
    OffsetDateTime rangeUpper)
    implements CATypeScalarType
  {
    /**
     * A time type.
     */

    public Time
    {
      Objects.requireNonNull(packageIdentifier, "packageIdentifier");
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(description, "description");
      Objects.requireNonNull(rangeLower, "rangeLower");
      Objects.requireNonNull(rangeUpper, "rangeUpper");
    }

    @Override
    public Kind kind()
    {
      return Kind.SCALAR_TIME;
    }

    @Override
    public CATypeScalarType withName(
      final RDottedName newName)
    {
      return new Time(
        this.packageIdentifier,
        newName,
        this.description,
        this.rangeLower,
        this.rangeUpper
      );
    }

    @Override
    public String showConstraint()
    {
      return String.format(
        "∀x, %s <= x <= %s",
        this.rangeLower,
        this.rangeUpper
      );
    }

    /**
     * @param value The value
     *
     * @return {@code true} if the given value is in the range for this type
     */

    public boolean isValid(
      final OffsetDateTime value)
    {
      return value.compareTo(this.rangeLower) >= 0
             && value.compareTo(this.rangeUpper) <= 0;
    }
  }

  /**
   * A monetary type.
   *
   * @param packageIdentifier The package to which this type belongs
   * @param name              The type name
   * @param description       A humanly-readable description of the type
   * @param rangeLower        The lower bound (inclusive)
   * @param rangeUpper        The upper bound (inclusive)
   */

  record Monetary(
    CATypePackageIdentifier packageIdentifier,
    RDottedName name,
    String description,
    BigDecimal rangeLower,
    BigDecimal rangeUpper)
    implements CATypeScalarType
  {
    /**
     * A monetary type.
     */

    public Monetary
    {
      Objects.requireNonNull(packageIdentifier, "packageIdentifier");
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(description, "description");
      Objects.requireNonNull(rangeLower, "rangeLower");
      Objects.requireNonNull(rangeUpper, "rangeUpper");
    }

    @Override
    public CATypeScalarType withName(
      final RDottedName newName)
    {
      return new Monetary(
        this.packageIdentifier,
        newName,
        this.description,
        this.rangeLower,
        this.rangeUpper
      );
    }

    @Override
    public String showConstraint()
    {
      return String.format(
        "∀x, %s <= x <= %s",
        this.rangeLower,
        this.rangeUpper
      );
    }

    @Override
    public Kind kind()
    {
      return Kind.SCALAR_MONETARY;
    }

    /**
     * @param value The value
     *
     * @return {@code true} if the given value is in the range for this type
     */

    public boolean isValid(
      final BigDecimal value)
    {
      return value.compareTo(this.rangeLower) >= 0
             && value.compareTo(this.rangeUpper) <= 0;
    }
  }

  /**
   * A real type.
   *
   * @param packageIdentifier The package to which this type belongs
   * @param name              The type name
   * @param description       A humanly-readable description of the type
   * @param rangeLower        The lower bound (inclusive)
   * @param rangeUpper        The upper bound (inclusive)
   */

  record Real(
    CATypePackageIdentifier packageIdentifier,
    RDottedName name,
    String description,
    double rangeLower,
    double rangeUpper)
    implements CATypeScalarType
  {
    /**
     * A real type.
     */

    public Real
    {
      Objects.requireNonNull(packageIdentifier, "packageIdentifier");
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(description, "description");
    }

    @Override
    public CATypeScalarType withName(
      final RDottedName newName)
    {
      return new Real(
        this.packageIdentifier,
        newName,
        this.description,
        this.rangeLower,
        this.rangeUpper
      );
    }

    @Override
    public String showConstraint()
    {
      return String.format(
        "∀x, %f <= x <= %f",
        Double.valueOf(this.rangeLower),
        Double.valueOf(this.rangeUpper)
      );
    }

    @Override
    public Kind kind()
    {
      return Kind.SCALAR_REAL;
    }

    /**
     * @param value The value
     *
     * @return {@code true} if the given value is in the range for this type
     */

    public boolean isValid(
      final double value)
    {
      return RangeInclusiveD.of(this.rangeLower, this.rangeUpper)
        .includesValue(value);
    }
  }
}
