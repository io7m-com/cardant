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
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageUninstall;
import com.io7m.cardant.protocol.inventory.CAIResponseTypePackageUninstall;
import com.io7m.lanark.core.RDottedName;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import com.io7m.verona.core.Version;

import java.util.List;
import java.util.Optional;

import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "type-package-uninstall"
 */

public final class CAShellCmdTypePackageUninstall
  extends CAShellCmdAbstractCR<CAICommandTypePackageUninstall, CAIResponseTypePackageUninstall>
{
  private static final QParameterNamed1<RDottedName> NAME =
    new QParameterNamed1<>(
      "--name",
      List.of(),
      new QConstant("The type package name."),
      Optional.empty(),
      RDottedName.class
    );

  private static final QParameterNamed1<Version> VERSION =
    new QParameterNamed1<>(
      "--version",
      List.of(),
      new QConstant("The type package version."),
      Optional.empty(),
      Version.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The shell context
   */

  public CAShellCmdTypePackageUninstall(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "type-package-uninstall",
        new QConstant("Uninstall a type package."),
        Optional.empty()
      ),
      CAICommandTypePackageUninstall.class
    );
  }


  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(NAME, VERSION);
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    final var client =
      this.client();

    final var packName =
      context.parameterValue(NAME);
    final var packVersion =
      context.parameterValue(VERSION);

    client.executeOrElseThrow(
      new CAICommandTypePackageUninstall(
        new CATypePackageIdentifier(packName, packVersion)
      ),
      CAClientException::ofError
    );

    return SUCCESS;
  }
}
