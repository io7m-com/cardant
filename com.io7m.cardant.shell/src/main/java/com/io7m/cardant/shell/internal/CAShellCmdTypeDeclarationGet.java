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
import com.io7m.cardant.protocol.inventory.CAICommandTypeDeclarationGet;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeDeclarationGet;
import com.io7m.lanark.core.RDottedName;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.util.List;
import java.util.Optional;

import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "type-get"
 */

public final class CAShellCmdTypeDeclarationGet
  extends CAShellCmdAbstractCR<CAICommandTypeDeclarationGet, CAIResponseTypeDeclarationGet>
{
  private static final QParameterNamed1<RDottedName> TYPE_NAME =
    new QParameterNamed1<>(
      "--name",
      List.of(),
      new QConstant("The type name."),
      Optional.empty(),
      RDottedName.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The shell context
   */

  public CAShellCmdTypeDeclarationGet(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "type-get",
        new QConstant("Retrieve a type declaration"),
        Optional.empty()
      ),
      CAICommandTypeDeclarationGet.class
    );
  }


  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(TYPE_NAME);
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

    final var type =
      ((CAIResponseTypeDeclarationGet) client.executeOrElseThrow(
        new CAICommandTypeDeclarationGet(typeName),
        CAClientException::ofError
      )).type();

    this.formatter().formatTypeDeclaration(type);
    return SUCCESS;
  }
}
