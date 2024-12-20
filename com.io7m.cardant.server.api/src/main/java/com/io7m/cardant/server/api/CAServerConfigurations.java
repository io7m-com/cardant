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

package com.io7m.cardant.server.api;

import com.io7m.cardant.database.api.CADatabaseConfiguration;
import com.io7m.cardant.database.api.CADatabaseFactoryType;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.cardant.type_packages.parser.api.CATypePackageSerializerFactoryType;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import static com.io7m.cardant.database.api.CADatabaseCreate.CREATE_DATABASE;
import static com.io7m.cardant.database.api.CADatabaseCreate.DO_NOT_CREATE_DATABASE;
import static com.io7m.cardant.database.api.CADatabaseUpgrade.DO_NOT_UPGRADE_DATABASE;
import static com.io7m.cardant.database.api.CADatabaseUpgrade.UPGRADE_DATABASE;

/**
 * Functions to produce server configurations.
 */

public final class CAServerConfigurations
{
  private CAServerConfigurations()
  {

  }

  /**
   * Read a server configuration from the given file.
   *
   * @param locale      The locale
   * @param clock       The clock
   * @param file        The file
   * @param serializers The serializers
   *
   * @return A server configuration
   */

  public static CAServerConfiguration ofFile(
    final Locale locale,
    final Clock clock,
    final CATypePackageSerializerFactoryType serializers,
    final CAServerConfigurationFile file)
  {
    Objects.requireNonNull(locale, "locale");
    Objects.requireNonNull(clock, "clock");
    Objects.requireNonNull(file, "file");
    Objects.requireNonNull(serializers, "serializers");

    final var strings =
      CAStrings.create(locale);

    final var fileDbConfig =
      file.databaseConfiguration();

    final var databaseConfiguration =
      new CADatabaseConfiguration(
        fileDbConfig.ownerRoleName(),
        fileDbConfig.ownerRolePassword(),
        fileDbConfig.workerRolePassword(),
        fileDbConfig.readerRolePassword(),
        fileDbConfig.address(),
        fileDbConfig.port(),
        fileDbConfig.databaseName(),
        fileDbConfig.create() ? CREATE_DATABASE : DO_NOT_CREATE_DATABASE,
        fileDbConfig.upgrade() ? UPGRADE_DATABASE : DO_NOT_UPGRADE_DATABASE,
        fileDbConfig.databaseLanguage(),
        clock,
        strings,
        serializers,
        fileDbConfig.minimumConnections(),
        fileDbConfig.maximumConnections()
      );

    final var databaseFactories =
      ServiceLoader.load(CADatabaseFactoryType.class)
        .iterator();

    final var database =
      findDatabase(databaseFactories, fileDbConfig.kind());

    return new CAServerConfiguration(
      locale,
      clock,
      strings,
      database,
      databaseConfiguration,
      file.inventoryService(),
      file.idstoreConfiguration(),
      file.limitsConfiguration(),
      file.maintenanceConfiguration(),
      file.openTelemetry()
    );
  }

  private static CADatabaseFactoryType findDatabase(
    final Iterator<CADatabaseFactoryType> databaseFactories,
    final CAServerDatabaseKind kind)
  {
    if (!databaseFactories.hasNext()) {
      throw new ServiceConfigurationError(
        "No available implementations of type %s"
          .formatted(CADatabaseFactoryType.class)
      );
    }

    final var kinds = new ArrayList<String>();
    while (databaseFactories.hasNext()) {
      final var database = databaseFactories.next();
      kinds.add(database.kind());
      if (Objects.equals(database.kind(), kind.name())) {
        return database;
      }
    }

    throw new ServiceConfigurationError(
      "No available databases of kind %s (Available databases include: %s)"
        .formatted(CADatabaseFactoryType.class, kinds)
    );
  }
}
