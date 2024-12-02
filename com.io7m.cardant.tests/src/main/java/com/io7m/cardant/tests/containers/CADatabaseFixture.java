/*
 * Copyright © 2024 Mark Raynsford <code@io7m.com> https://www.io7m.com
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


package com.io7m.cardant.tests.containers;

import com.io7m.cardant.database.api.CADatabaseConfiguration;
import com.io7m.cardant.database.api.CADatabaseCreate;
import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseLanguage;
import com.io7m.cardant.database.api.CADatabaseTelemetry;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.database.api.CADatabaseUpgrade;
import com.io7m.cardant.database.postgres.CAPGDatabases;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.cardant.type_packages.parsers.CATypePackageSerializers;
import io.opentelemetry.api.OpenTelemetry;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.time.Clock;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * The basic database fixture.
 *
 * @param postgres      The base database fixture
 * @param configuration The database configuration
 */

public record CADatabaseFixture(
  CAPostgresFixture postgres,
  CADatabaseConfiguration configuration)
{
  static final CAPGDatabases DATABASES =
    new CAPGDatabases();

  private static final String OWNER_ROLE =
    "postgres";

  public static CADatabaseFixture create(
    final CAPostgresFixture postgres)
    throws IOException, InterruptedException
  {
    final var configuration =
      new CADatabaseConfiguration(
        OWNER_ROLE,
        "12345678",
        "12345678",
        Optional.of("12345678"),
        "0.0.0.0",
        CAFixtures.postgresPort(),
        "cardant",
        CADatabaseCreate.CREATE_DATABASE,
        CADatabaseUpgrade.UPGRADE_DATABASE,
        new CADatabaseLanguage("english"),
        Clock.systemUTC(),
        CAStrings.create(Locale.ROOT),
        new CATypePackageSerializers(),
        0,
        10
      );

    final var r =
      postgres.container()
        .executeAndWait(
          List.of(
            "createdb",
            "-w",
            "-U",
            postgres.databaseOwner(),
            "cardant"
          ),
          10L,
          TimeUnit.SECONDS
        );

    Assertions.assertEquals(0, r);

    return new CADatabaseFixture(
      postgres,
      configuration
    );
  }

  /**
   * Create a database from this container and configuration.
   *
   * @return A new database
   *
   * @throws CADatabaseException On errors
   */

  public CADatabaseType createDatabase()
    throws CADatabaseException
  {
    return DATABASES.open(
      this.configuration,
      new CADatabaseTelemetry(
        true,
        OpenTelemetry.noop().getMeter("x"),
        OpenTelemetry.noop().getTracer("x")
      ),
      message -> {

      });
  }

  /**
   * Reset the container by dropping and recreating the database. This
   * is significantly faster than destroying and recreating the container.
   *
   * @throws IOException          On errors
   * @throws InterruptedException On interruption
   */

  public void reset()
    throws IOException, InterruptedException
  {
    {
      final var r =
        this.postgres.container()
          .executeAndWait(
            List.of(
              "dropdb",
              "-f",
              "-w",
              "-U",
              this.postgres.databaseOwner(),
              "cardant"
            ),
            10L,
            TimeUnit.SECONDS
          );
      Assertions.assertEquals(0, r);
    }

    {
      final var r =
        this.postgres.container()
          .executeAndWait(
            List.of(
              "createdb",
              "-w",
              "-U",
              this.postgres.databaseOwner(),
              "cardant"
            ),
            10L,
            TimeUnit.SECONDS
          );
      Assertions.assertEquals(0, r);
    }
  }
}
