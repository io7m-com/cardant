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
import com.io7m.cardant.model.CATypeDeclaration;
import com.io7m.cardant.model.CATypeField;
import com.io7m.cardant.protocol.inventory.CAICommandTypeDeclarationGet;
import com.io7m.cardant.protocol.inventory.CAICommandTypeDeclarationPut;
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarGet;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeDeclarationGet;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeDeclarationPut;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeScalarGet;
import com.io7m.lanark.core.RDottedName;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "type-field-put"
 */

public final class CAShellCmdTypeDeclarationFieldPut
  extends CAShellCmdAbstractCR<CAICommandTypeDeclarationPut, CAIResponseTypeDeclarationPut>
{
  private static final QParameterNamed1<RDottedName> TYPE_NAME =
    new QParameterNamed1<>(
      "--type",
      List.of(),
      new QConstant("The type name."),
      Optional.empty(),
      RDottedName.class
    );

  private static final QParameterNamed1<RDottedName> FIELD_NAME =
    new QParameterNamed1<>(
      "--field-name",
      List.of(),
      new QConstant("The field name."),
      Optional.empty(),
      RDottedName.class
    );

  private static final QParameterNamed1<RDottedName> FIELD_TYPE =
    new QParameterNamed1<>(
      "--field-type",
      List.of(),
      new QConstant("The field type."),
      Optional.empty(),
      RDottedName.class
    );

  private static final QParameterNamed1<String> FIELD_DESCRIPTION =
    new QParameterNamed1<>(
      "--field-description",
      List.of(),
      new QConstant("The field description."),
      Optional.empty(),
      String.class
    );

  private static final QParameterNamed1<Boolean> FIELD_REQUIRED =
    new QParameterNamed1<>(
      "--field-required",
      List.of(),
      new QConstant("Whether the field is required."),
      Optional.of(Boolean.TRUE),
      Boolean.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The shell context
   */

  public CAShellCmdTypeDeclarationFieldPut(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "type-field-put",
        new QConstant("Add or update a field on a type declaration"),
        Optional.empty()
      ),
      CAICommandTypeDeclarationPut.class
    );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(
      TYPE_NAME,
      FIELD_NAME,
      FIELD_TYPE,
      FIELD_REQUIRED,
      FIELD_DESCRIPTION
    );
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    final var client =
      this.client();

    final var typeName =
      context.parameterValue(TYPE_NAME);
    final var fieldName =
      context.parameterValue(FIELD_NAME);
    final var fieldType =
      context.parameterValue(FIELD_TYPE);
    final var description =
      context.parameterValue(FIELD_DESCRIPTION);
    final var required =
      context.parameterValue(FIELD_REQUIRED).booleanValue();

    final var existing =
      ((CAIResponseTypeDeclarationGet) client.executeOrElseThrow(
        new CAICommandTypeDeclarationGet(typeName),
        CAClientException::ofError
      )).type();

    final var scalarType =
      ((CAIResponseTypeScalarGet) client.executeOrElseThrow(
        new CAICommandTypeScalarGet(fieldType),
        CAClientException::ofError
      )).type();

    final var fields =
      new HashMap<>(existing.fields());
    final var newField =
      new CATypeField(fieldName, description, scalarType, required);

    fields.put(fieldName, newField);

    final var newDeclaration =
      new CATypeDeclaration(
        existing.name(),
        existing.description(),
        Map.copyOf(fields)
      );

    client.executeOrElseThrow(
      new CAICommandTypeDeclarationPut(Set.of(newDeclaration)),
      CAClientException::ofError
    );
    return SUCCESS;
  }
}
