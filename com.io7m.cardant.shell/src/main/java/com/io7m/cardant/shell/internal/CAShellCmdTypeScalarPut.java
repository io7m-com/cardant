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


package com.io7m.cardant.shell.internal;

import com.io7m.cardant.client.api.CAClientException;
import com.io7m.cardant.model.CAMonetaryRange;
import com.io7m.cardant.model.CATimeRange;
import com.io7m.cardant.model.CATypeScalarType;
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarPut;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeScalarPut;
import com.io7m.jranges.RangeInclusiveD;
import com.io7m.jranges.RangeInclusiveL;
import com.io7m.lanark.core.RDottedName;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed01;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.io7m.quarrel.core.QCommandStatus.FAILURE;
import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "type-scalar-put"
 */

public final class CAShellCmdTypeScalarPut
  extends CAShellCmdAbstractCR<CAICommandTypeScalarPut, CAIResponseTypeScalarPut>
{
  private static final QParameterNamed1<RDottedName> TYPE_NAME =
    new QParameterNamed1<>(
      "--name",
      List.of(),
      new QConstant("The type name."),
      Optional.empty(),
      RDottedName.class
    );

  private static final QParameterNamed1<String> DESCRIPTION =
    new QParameterNamed1<>(
      "--description",
      List.of(),
      new QConstant("The type description."),
      Optional.of(""),
      String.class
    );

  private static final QParameterNamed01<RangeInclusiveL> INTEGRAL =
    new QParameterNamed01<>(
      "--base-is-integral",
      List.of(),
      new QConstant(
        "Specify that the base type is integral and provide an inclusive range of values."),
      Optional.of(RangeInclusiveL.of(Long.MIN_VALUE, Long.MAX_VALUE)),
      RangeInclusiveL.class
    );

  private static final QParameterNamed01<RangeInclusiveD> REAL =
    new QParameterNamed01<>(
      "--base-is-real",
      List.of(),
      new QConstant(
        "Specify that the base type is real and provide an inclusive range of values."),
      Optional.of(RangeInclusiveD.of(-Double.MAX_VALUE, Double.MAX_VALUE)),
      RangeInclusiveD.class
    );

  private static final QParameterNamed01<CATimeRange> TIME =
    new QParameterNamed01<>(
      "--base-is-time",
      List.of(),
      new QConstant(
        "Specify that the base type is a timestamp and provide an inclusive range of values."),
      Optional.of(new CATimeRange(
        OffsetDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
        OffsetDateTime.now()
      )),
      CATimeRange.class
    );

  private static final QParameterNamed01<CAMonetaryRange> MONETARY =
    new QParameterNamed01<>(
      "--base-is-monetary",
      List.of(),
      new QConstant(
        "Specify that the base type is monetary and provide an inclusive range of values."),
      Optional.of(new CAMonetaryRange(BigDecimal.ZERO, new BigDecimal("1000000000000"))),
      CAMonetaryRange.class
    );

  private static final QParameterNamed01<String> TEXT =
    new QParameterNamed01<>(
      "--base-is-text",
      List.of(),
      new QConstant(
        "Specify that the base type is text and provide a validating pattern."),
      Optional.of(".*"),
      String.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The shell context
   */

  public CAShellCmdTypeScalarPut(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "type-scalar-put",
        new QConstant("Create or update a scalar type."),
        Optional.empty()
      ),
      CAICommandTypeScalarPut.class
    );
  }


  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(TYPE_NAME, DESCRIPTION, INTEGRAL, REAL, TIME, MONETARY, TEXT);
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    final var typeName =
      context.parameterValue(TYPE_NAME);
    final var description =
      context.parameterValue(DESCRIPTION);

    final var integral =
      context.parameterValue(INTEGRAL);
    final var real =
      context.parameterValue(REAL);
    final var time =
      context.parameterValue(TIME);
    final var monetary =
      context.parameterValue(MONETARY);
    final var text =
      context.parameterValue(TEXT);

    final Set<CATypeScalarType> types;
    if (integral.isPresent()) {
      types = this.putIntegral(typeName, description, integral.get());
    } else if (real.isPresent()) {
      types = this.putReal(typeName, description, real.get());
    } else if (time.isPresent()) {
      types = this.putTime(typeName, description, time.get());
    } else if (monetary.isPresent()) {
      types = this.putMonetary(typeName, description, monetary.get());
    } else if (text.isPresent()) {
      types = this.putText(typeName, description, text.get());
    } else {
      context.output().println("A base type must be specified.");
      return FAILURE;
    }

    this.formatter().formatTypesScalar(types);
    return SUCCESS;
  }

  private Set<CATypeScalarType> putText(
    final RDottedName typeName,
    final String description,
    final String pattern)
    throws CAClientException, InterruptedException
  {
    final var client = this.client();

    return ((CAIResponseTypeScalarPut) client.executeOrElseThrow(
      new CAICommandTypeScalarPut(
        Set.of(
          new CATypeScalarType.Text(
            typeName,
            description,
            pattern
          )
        )
      ),
      CAClientException::ofError
    )).types();
  }

  private Set<CATypeScalarType> putMonetary(
    final RDottedName typeName,
    final String description,
    final CAMonetaryRange range)
    throws CAClientException, InterruptedException
  {
    final var client = this.client();

    return ((CAIResponseTypeScalarPut) client.executeOrElseThrow(
      new CAICommandTypeScalarPut(
        Set.of(
          new CATypeScalarType.Monetary(
            typeName,
            description,
            range.lower(),
            range.upper()
          )
        )
      ),
      CAClientException::ofError
    )).types();
  }

  private Set<CATypeScalarType> putTime(
    final RDottedName typeName,
    final String description,
    final CATimeRange range)
    throws CAClientException, InterruptedException
  {
    final var client = this.client();

    return ((CAIResponseTypeScalarPut) client.executeOrElseThrow(
      new CAICommandTypeScalarPut(
        Set.of(
          new CATypeScalarType.Time(
            typeName,
            description,
            range.lower(),
            range.upper()
          )
        )
      ),
      CAClientException::ofError
    )).types();
  }

  private Set<CATypeScalarType> putReal(
    final RDottedName typeName,
    final String description,
    final RangeInclusiveD range)
    throws CAClientException, InterruptedException
  {
    final var client = this.client();

    return ((CAIResponseTypeScalarPut) client.executeOrElseThrow(
      new CAICommandTypeScalarPut(
        Set.of(
          new CATypeScalarType.Real(
            typeName,
            description,
            range.lower(),
            range.upper()
          )
        )
      ),
      CAClientException::ofError
    )).types();
  }

  private Set<CATypeScalarType> putIntegral(
    final RDottedName typeName,
    final String description,
    final RangeInclusiveL range)
    throws CAClientException, InterruptedException
  {
    final var client = this.client();

    return ((CAIResponseTypeScalarPut) client.executeOrElseThrow(
      new CAICommandTypeScalarPut(
        Set.of(
          new CATypeScalarType.Integral(
            typeName,
            description,
            range.lower(),
            range.upper()
          )
        )
      ),
      CAClientException::ofError
    )).types();
  }
}
