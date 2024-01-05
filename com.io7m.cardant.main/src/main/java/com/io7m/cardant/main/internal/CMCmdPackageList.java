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

import com.io7m.cardant.model.type_package.CATypePackageProviderType;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QCommandType;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * The "list" command.
 */

public final class CMCmdPackageList implements QCommandType
{
  private final QCommandMetadata metadata;

  /**
   * Construct a command.
   */

  public CMCmdPackageList()
  {
    this.metadata = new QCommandMetadata(
      "list",
      new QConstant("List the available packages."),
      Optional.empty()
    );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of();
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
  {
    final var services =
      ServiceLoader.load(CATypePackageProviderType.class)
        .stream()
        .map(ServiceLoader.Provider::get)
        .sorted(Comparator.comparing(CATypePackageProviderType::identifier))
        .toList();

    final var nameLength =
      services.stream()
        .map(p -> p.identifier().name())
        .mapToInt(p -> p.value().length())
        .max()
        .orElse(10) + 2;

    final var versionLength =
      services.stream()
        .map(p -> p.identifier().version().toString())
        .mapToInt(String::length)
        .max()
        .orElse(8) + 2;

    final var formatB = new StringBuilder();
    formatB.append("%-");
    formatB.append(nameLength);
    formatB.append('s');
    formatB.append(" %-");
    formatB.append(versionLength);
    formatB.append('s');
    final var format = formatB.toString();

    System.out.printf(
      format,
      "# Package",
      "Version"
    );
    System.out.println();

    for (final var provider : services) {
      System.out.printf(
        format,
        provider.identifier().name(),
        provider.identifier().version()
      );
      System.out.println();
    }

    return SUCCESS;
  }

  @Override
  public QCommandMetadata metadata()
  {
    return this.metadata;
  }
}
