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
import com.io7m.cardant.protocol.inventory.CAICommandTypeDeclarationGet;
import com.io7m.cardant.protocol.inventory.CAICommandTypeDeclarationPut;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeDeclarationPut;
import com.io7m.lanark.core.RDottedName;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "type-create"
 */

public final class CAShellCmdTypeDeclarationCreate
  extends CAShellCmdAbstractCR<CAICommandTypeDeclarationPut, CAIResponseTypeDeclarationPut>
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
      Optional.empty(),
      String.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The shell context
   */

  public CAShellCmdTypeDeclarationCreate(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "type-create",
        new QConstant("Create a type declaration"),
        Optional.empty()
      ),
      CAICommandTypeDeclarationPut.class
    );
  }


  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(TYPE_NAME, DESCRIPTION);
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

    /*
     * Check if the type already exists.
     */

    try {
      client.executeOrElseThrow(
        new CAICommandTypeDeclarationGet(typeName),
        CAClientException::ofError
      );
    } catch (final CAClientException e) {
      if (!Objects.equals(e.errorCode(), errorNonexistent())) {
        throw e;
      }
    }

    final var description =
      context.parameterValue(DESCRIPTION);

    client.executeOrElseThrow(
      new CAICommandTypeDeclarationPut(
        Set.of(new CATypeDeclaration(typeName, description, Map.of()))
      ),
      CAClientException::ofError
    );
    return SUCCESS;
  }
}
