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


package com.io7m.cardant.main.internal;

import com.io7m.cardant.error_codes.CAException;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.model.type_package.CATypePackageProviderType;
import com.io7m.lanark.core.RDottedName;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QCommandType;
import com.io7m.quarrel.core.QParameterNamed01;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.verona.core.VersionException;
import com.io7m.verona.core.VersionParser;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;

import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

/**
 * The "get" command.
 */

public final class CMCmdPackageGet implements QCommandType
{
  private static final OpenOption[] OPEN_OPTIONS =
    {WRITE, CREATE, TRUNCATE_EXISTING};

  private final QCommandMetadata metadata;

  private static final QParameterNamed1<String> PACKAGE_NAME =
    new QParameterNamed1<>(
      "--name",
      List.of(),
      new QConstant("The package name."),
      Optional.empty(),
      String.class
    );

  private static final QParameterNamed1<String> PACKAGE_VERSION =
    new QParameterNamed1<>(
      "--version",
      List.of(),
      new QConstant("The package version."),
      Optional.empty(),
      String.class
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
   */

  public CMCmdPackageGet()
  {
    this.metadata = new QCommandMetadata(
      "get",
      new QConstant("Get the text of an available package."),
      Optional.empty()
    );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(PACKAGE_NAME, PACKAGE_VERSION, OUTPUT);
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws VersionException, CAException, IOException
  {
    final var name =
      new RDottedName(context.parameterValue(PACKAGE_NAME));
    final var version =
      VersionParser.parse(context.parameterValue(PACKAGE_VERSION));
    final var identifier =
      new CATypePackageIdentifier(name, version);

    final var packageProvider =
      ServiceLoader.load(CATypePackageProviderType.class)
        .stream()
        .map(ServiceLoader.Provider::get)
        .filter(p -> Objects.equals(p.identifier(), identifier))
        .findFirst()
        .orElseThrow(() -> {
          return new CAException(
            "No such package.",
            CAStandardErrorCodes.errorNonexistent(),
            Map.ofEntries(
              Map.entry("Package", name.value()),
              Map.entry("Version", version.toString())
            ),
            Optional.empty()
          );
        });

    final var output =
      context.parameterValue(OUTPUT);

    final OutputStream outputStream;
    if (output.isPresent()) {
      outputStream = Files.newOutputStream(output.get(), OPEN_OPTIONS);
    } else {
      outputStream = System.out;
    }

    try (outputStream) {
      packageProvider.packageText().transferTo(outputStream);
      outputStream.flush();
    }

    return SUCCESS;
  }

  @Override
  public QCommandMetadata metadata()
  {
    return this.metadata;
  }
}
