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

import com.io7m.cardant.model.type_package.CATypePackageTypeRemovalBehavior;
import com.io7m.cardant.model.type_package.CATypePackageVersionBehavior;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageUpgrade;
import com.io7m.cardant.protocol.inventory.CAIResponseTypePackageUpgrade;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "type-package-upgrade"
 */

public final class CAShellCmdTypePackageUpgrade
  extends CAShellCmdAbstractCR<CAICommandTypePackageUpgrade, CAIResponseTypePackageUpgrade>
{
  private static final QParameterNamed1<Path> FILE =
    new QParameterNamed1<>(
      "--file",
      List.of(),
      new QConstant("The type package file."),
      Optional.empty(),
      Path.class
    );

  private static final QParameterNamed1<CATypePackageTypeRemovalBehavior> TYPE_REMOVAL_BEHAVIOR =
    new QParameterNamed1<>(
      "--type-removal-behavior",
      List.of(),
      new QConstant("The type removal behavior."),
      Optional.of(
        CATypePackageTypeRemovalBehavior.TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED
      ),
      CATypePackageTypeRemovalBehavior.class
    );

  private static final QParameterNamed1<CATypePackageVersionBehavior> VERSION_BEHAVIOR =
    new QParameterNamed1<>(
      "--version-behavior",
      List.of(),
      new QConstant("The version behavior."),
      Optional.of(
        CATypePackageVersionBehavior.VERSION_DISALLOW_DOWNGRADES
      ),
      CATypePackageVersionBehavior.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The shell context
   */

  public CAShellCmdTypePackageUpgrade(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "type-package-upgrade",
        new QConstant("Upgrade a type package."),
        Optional.empty()
      ),
      CAICommandTypePackageUpgrade.class
    );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(FILE, TYPE_REMOVAL_BEHAVIOR, VERSION_BEHAVIOR);
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    final var client =
      this.client();

    final var inputFile =
      context.parameterValue(FILE);
    final var typeRemovalBehavior =
      context.parameterValue(TYPE_REMOVAL_BEHAVIOR);
    final var versionBehavior =
      context.parameterValue(VERSION_BEHAVIOR);

    final var identifier =
      client.sendAndWaitOrThrow(
        new CAICommandTypePackageUpgrade(
          typeRemovalBehavior,
          versionBehavior,
          Files.readString(inputFile)
        ),
        this.commandTimeout()
      ).data();

    this.formatter().printLine(identifier.toString());
    return SUCCESS;
  }
}
