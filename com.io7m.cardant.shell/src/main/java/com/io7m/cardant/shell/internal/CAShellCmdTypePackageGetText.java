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

import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageGetText;
import com.io7m.cardant.protocol.inventory.CAIResponseTypePackageGetText;
import com.io7m.lanark.core.RDottedName;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed01;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import com.io7m.verona.core.Version;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;

import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "type-package-get-text"
 */

public final class CAShellCmdTypePackageGetText
  extends CAShellCmdAbstractCR<CAICommandTypePackageGetText, CAIResponseTypePackageGetText>
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

  private static final QParameterNamed01<Path> OUTPUT =
    new QParameterNamed01<>(
      "--output",
      List.of(),
      new QConstant("The output file."),
      Optional.empty(),
      Path.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The shell context
   */

  public CAShellCmdTypePackageGetText(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "type-package-get-text",
        new QConstant("Retrieve the text of a type package."),
        Optional.empty()
      ),
      CAICommandTypePackageGetText.class
    );
  }


  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(NAME, VERSION, OUTPUT);
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
    final var outputFile =
      context.parameterValue(OUTPUT);

    final var text =
      client.sendAndWaitOrThrow(
        new CAICommandTypePackageGetText(
          new CATypePackageIdentifier(packName, packVersion)
        ),
        this.commandTimeout()
      ).data();

    if (outputFile.isEmpty()) {
      this.formatter().printLine(text);
    } else {
      Files.writeString(
        outputFile.get(),
        text,
        StandardOpenOption.TRUNCATE_EXISTING,
        StandardOpenOption.CREATE,
        StandardOpenOption.WRITE
      );
    }

    return SUCCESS;
  }
}
