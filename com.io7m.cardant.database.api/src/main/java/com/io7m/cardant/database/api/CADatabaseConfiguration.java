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

package com.io7m.cardant.database.api;

import com.io7m.cardant.strings.CAStrings;
import com.io7m.cardant.type_packages.parser.api.CATypePackageSerializerFactoryType;

import java.time.Clock;
import java.util.Objects;
import java.util.Optional;

/**
 * The server database configuration.
 *
 * @param ownerRoleName          The name of the role that owns the database; used for database setup and migrations
 * @param ownerRolePassword      The password of the role that owns the database
 * @param workerRolePassword     The password of the worker role used for normal database operation
 * @param readerRolePassword     The password of the role used for read-only database access
 * @param port                   The database TCP/IP port
 * @param upgrade                The upgrade specification
 * @param create                 The creation specification
 * @param address                The database address
 * @param databaseName           The database name
 * @param clock                  A clock for time retrievals
 * @param strings                The string resources
 * @param language               The language used for databases (such as 'english')
 * @param typePackageSerializers The type package serializers
 * @param minimumConnections     The minimum number of database connections in the pool
 * @param maximumConnections     The maximum number of database connections in the pool
 */

public record CADatabaseConfiguration(
  String ownerRoleName,
  String ownerRolePassword,
  String workerRolePassword,
  Optional<String> readerRolePassword,
  String address,
  int port,
  String databaseName,
  CADatabaseCreate create,
  CADatabaseUpgrade upgrade,
  String language,
  Clock clock,
  CAStrings strings,
  CATypePackageSerializerFactoryType typePackageSerializers,
  int minimumConnections,
  int maximumConnections)
{
  /**
   * The server database configuration.
   *
   * @param ownerRoleName          The name of the role that owns the database;
   *                               used for database setup and migrations
   * @param ownerRolePassword      The password of the role that owns the database
   * @param workerRolePassword     The password of the worker role used for normal
   *                               database operation
   * @param readerRolePassword     The password of the role used for read-only
   *                               database access
   * @param port                   The database TCP/IP port
   * @param upgrade                The upgrade specification
   * @param create                 The creation specification
   * @param address                The database address
   * @param databaseName           The database name
   * @param clock                  A clock for time retrievals
   * @param strings                The string resources
   * @param language               The language used for databases
   *                               (such as 'english')
   * @param typePackageSerializers The type package serializers
   * @param minimumConnections     The minimum number of database connections in the pool
   * @param maximumConnections     The maximum number of database connections in the pool
   */

  public CADatabaseConfiguration
  {
    Objects.requireNonNull(ownerRoleName, "ownerRoleName");
    Objects.requireNonNull(ownerRolePassword, "ownerRolePassword");
    Objects.requireNonNull(workerRolePassword, "workerRolePassword");
    Objects.requireNonNull(readerRolePassword, "readerRolePassword");
    Objects.requireNonNull(address, "address");
    Objects.requireNonNull(databaseName, "databaseName");
    Objects.requireNonNull(create, "create");
    Objects.requireNonNull(upgrade, "upgrade");
    Objects.requireNonNull(clock, "clock");
    Objects.requireNonNull(strings, "strings");
    Objects.requireNonNull(language, "language");
    Objects.requireNonNull(typePackageSerializers, "typePackageSerializers");

    minimumConnections = Math.max(0, minimumConnections);
    maximumConnections = Math.max(minimumConnections, maximumConnections);
  }

  /**
   * @return This database configuration without database creation or
   * upgrades enabled
   */

  public CADatabaseConfiguration withoutUpgradeOrCreate()
  {
    return new CADatabaseConfiguration(
      this.ownerRoleName,
      this.ownerRolePassword,
      this.workerRolePassword,
      this.readerRolePassword,
      this.address,
      this.port,
      this.databaseName,
      CADatabaseCreate.DO_NOT_CREATE_DATABASE,
      CADatabaseUpgrade.DO_NOT_UPGRADE_DATABASE,
      this.language,
      this.clock,
      this.strings,
      this.typePackageSerializers,
      this.minimumConnections,
      this.maximumConnections
    );
  }
}
