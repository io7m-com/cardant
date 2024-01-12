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


package com.io7m.cardant.parsers;

import com.io7m.cardant.error_codes.CAException;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.model.CAMoney;
import com.io7m.cardant.model.CATypeRecordFieldIdentifier;
import com.io7m.cardant.strings.CAStringConstantType;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.SExpressionType.SAtomType;
import com.io7m.jsx.SExpressionType.SList;
import com.io7m.jsx.SExpressionType.SQuotedString;
import com.io7m.jsx.SExpressionType.SSymbol;
import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_METADATA;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_METADATA_INTEGER;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_METADATA_INTEGER_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_METADATA_MONEY;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_METADATA_MONEY_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_METADATA_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_METADATA_REAL;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_METADATA_REAL_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_METADATA_TEXT;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_METADATA_TEXT_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_METADATA_TIME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_METADATA_TIME_NAME;
import static com.io7m.jlexing.core.LexicalPositions.zero;
import static java.util.Map.entry;

/**
 * Expressions over metadata values.
 */

public final class CAMetadataExpressions extends CAExpressions
{
  private static final Map<CAStringConstantType, CAStringConstantType> SYNTAX =
    Map.ofEntries(
      entry(SYNTAX_METADATA_INTEGER_NAME, SYNTAX_METADATA_INTEGER),
      entry(SYNTAX_METADATA_TIME_NAME, SYNTAX_METADATA_TIME),
      entry(SYNTAX_METADATA_TEXT_NAME, SYNTAX_METADATA_TEXT),
      entry(SYNTAX_METADATA_REAL_NAME, SYNTAX_METADATA_REAL),
      entry(SYNTAX_METADATA_MONEY_NAME, SYNTAX_METADATA_MONEY),
      entry(SYNTAX_METADATA_NAME, SYNTAX_METADATA)
    );

  /**
   * Expression parsers for metadata values.
   *
   * @param inStrings The string resources
   */

  public CAMetadataExpressions(
    final CAStrings inStrings)
  {
    super(inStrings);
  }

  @Override
  protected Map<CAStringConstantType, CAStringConstantType> syntax()
  {
    return SYNTAX;
  }

  /**
   * Parse a metadata expression.
   *
   * @param text The expression
   *
   * @return A metadata value
   *
   * @throws CAException On errors
   */

  public CAMetadataType metadataParse(
    final String text)
    throws CAException
  {
    return this.metadataExpr(CAExpressions.parse(text));
  }

  /**
   * Serialize metadata to a string.
   *
   * @param meta The metadata
   *
   * @return The serialized value
   *
   * @throws CAException On errors
   */

  public String metadataSerializeToString(
    final CAMetadataType meta)
    throws CAException
  {
    return CAExpressions.serialize(this.metadataSerialize(meta));
  }

  /**
   * Serialize metadata.
   *
   * @param meta The metadata
   *
   * @return The serialized value
   */

  public SExpressionType metadataSerialize(
    final CAMetadataType meta)
  {
    if (meta instanceof final CAMetadataType.Text text) {
      return new SList(
        zero(), true,
        List.of(
          new SSymbol(zero(), "Text"),
          new SSymbol(zero(), text.name().toString()),
          new SQuotedString(zero(), text.value())
        )
      );
    }

    if (meta instanceof final CAMetadataType.Time time) {
      return new SList(
        zero(), true,
        List.of(
          new SSymbol(zero(), "Time"),
          new SSymbol(zero(), time.name().toString()),
          new SSymbol(zero(), time.value().toString())
        )
      );
    }

    if (meta instanceof final CAMetadataType.Integral integral) {
      return new SList(
        zero(), true,
        List.of(
          new SSymbol(zero(), "Integer"),
          new SSymbol(zero(), integral.name().toString()),
          new SSymbol(zero(), Long.toString(integral.value()))
        )
      );
    }

    if (meta instanceof final CAMetadataType.Real real) {
      return new SList(
        zero(), true,
        List.of(
          new SSymbol(zero(), "Real"),
          new SSymbol(zero(), real.name().toString()),
          new SSymbol(zero(), Double.toString(real.value()))
        )
      );
    }

    if (meta instanceof final CAMetadataType.Monetary monetary) {
      return new SList(
        zero(), true,
        List.of(
          new SSymbol(zero(), "Money"),
          new SSymbol(zero(), monetary.name().toString()),
          new SSymbol(zero(), monetary.value().toString()),
          new SSymbol(zero(), monetary.currency().getCode())
        )
      );
    }

    throw new IllegalStateException();
  }

  private CAMetadataType metadataExpr(
    final SExpressionType expression)
    throws CAException
  {
    if (expression instanceof final SList list
      && list.size() >= 3
      && list.get(0) instanceof final SAtomType head) {

      return switch (head.text().toUpperCase(Locale.ROOT)) {
        case "INTEGER" -> {
          yield this.metadataMatchExprInteger(list);
        }
        case "REAL" -> {
          yield this.metadataMatchExprReal(list);
        }
        case "MONEY" -> {
          yield this.metadataMatchExprMoney(list);
        }
        case "TIME" -> {
          yield this.metadataMatchExprTime(list);
        }
        case "TEXT" -> {
          yield this.metadataMatchExprText(list);
        }
        default -> throw this.createParseError(head);
      };
    }

    throw this.createParseError(expression);
  }

  private CAMetadataType metadataMatchExprText(
    final SList list)
    throws CAException
  {
    if (list.size() == 3) {
      return new CAMetadataType.Text(
        this.recordFieldIdentifier(list.get(1), this.text(list.get(1))),
        this.text(list.get(2))
      );
    }
    throw this.createParseError(list);
  }

  private CAMetadataType metadataMatchExprTime(
    final SList list)
    throws CAException
  {
    if (list.size() == 3) {
      return new CAMetadataType.Time(
        this.recordFieldIdentifier(list.get(1), this.text(list.get(1))),
        this.time(list.get(2))
      );
    }
    throw this.createParseError(list);
  }

  private CAMetadataType metadataMatchExprMoney(
    final SList list)
    throws CAException
  {
    if (list.size() == 4) {
      return new CAMetadataType.Monetary(
        this.recordFieldIdentifier(list.get(1), this.text(list.get(1))),
        this.monetary(list.get(2)),
        this.currency(list.get(3))
      );
    }
    throw this.createParseError(list);
  }

  private CAMetadataType metadataMatchExprReal(
    final SList list)
    throws CAException
  {
    if (list.size() == 3) {
      return new CAMetadataType.Real(
        this.recordFieldIdentifier(list.get(1), this.text(list.get(1))),
        this.real(list.get(2))
      );
    }
    throw this.createParseError(list);
  }

  private CAMetadataType metadataMatchExprInteger(
    final SList list)
    throws CAException
  {
    if (list.size() == 3) {
      return new CAMetadataType.Integral(
        this.recordFieldIdentifier(list.get(1), this.text(list.get(1))),
        this.integer(list.get(2))
      );
    }
    throw this.createParseError(list);
  }

  private String text(
    final SExpressionType expr)
    throws CAException
  {
    if (expr instanceof final SAtomType atom) {
      return atom.text();
    }

    throw this.createParseError(expr);
  }

  private CurrencyUnit currency(
    final SExpressionType expr)
    throws CAException
  {
    if (expr instanceof final SAtomType atom) {
      try {
        return CurrencyUnit.of(atom.text());
      } catch (final Exception e) {
        throw this.createParseError(expr, e);
      }
    }

    throw this.createParseError(expr);
  }

  private BigDecimal monetary(
    final SExpressionType expr)
    throws CAException
  {
    if (expr instanceof final SAtomType atom) {
      try {
        return CAMoney.money(atom.text());
      } catch (final Exception e) {
        throw this.createParseError(expr, e);
      }
    }

    throw this.createParseError(expr);
  }

  private OffsetDateTime time(
    final SExpressionType expr)
    throws CAException
  {
    if (expr instanceof final SAtomType atom) {
      try {
        return OffsetDateTime.parse(atom.text());
      } catch (final Exception e) {
        throw this.createParseError(expr, e);
      }
    }

    throw this.createParseError(expr);
  }

  private double real(
    final SExpressionType expr)
    throws CAException
  {
    if (expr instanceof final SAtomType atom) {
      try {
        return Double.parseDouble(atom.text());
      } catch (final Exception e) {
        throw this.createParseError(expr, e);
      }
    }

    throw this.createParseError(expr);
  }

  private long integer(
    final SExpressionType expr)
    throws CAException
  {
    if (expr instanceof final SAtomType atom) {
      try {
        return Long.parseLong(atom.text());
      } catch (final Exception e) {
        throw this.createParseError(expr, e);
      }
    }

    throw this.createParseError(expr);
  }

  private CATypeRecordFieldIdentifier recordFieldIdentifier(
    final SExpressionType expr,
    final String text)
    throws CAException
  {
    try {
      return CATypeRecordFieldIdentifier.of(text);
    } catch (final Exception e) {
      throw this.createParseError(expr, e);
    }
  }
}
