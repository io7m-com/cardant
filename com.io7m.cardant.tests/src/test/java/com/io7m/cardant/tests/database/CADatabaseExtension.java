/*
 * Copyright © 2022 Mark Raynsford <code@io7m.com> https://www.io7m.com
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


package com.io7m.cardant.tests.database;

import com.io7m.cardant.database.api.CADatabaseConfiguration;
import com.io7m.cardant.database.api.CADatabaseCreate;
import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseRole;
import com.io7m.cardant.database.api.CADatabaseTelemetry;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.database.api.CADatabaseUpgrade;
import com.io7m.cardant.database.postgres.CAPGDatabases;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.jmulticlose.core.CloseableCollection;
import com.io7m.jmulticlose.core.CloseableCollectionType;
import com.io7m.jmulticlose.core.ClosingResourceFailedException;
import io.opentelemetry.api.OpenTelemetry;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static java.time.OffsetDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

public final class CADatabaseExtension
  implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback,
  ExtensionContext.Store.CloseableResource,
  ParameterResolver
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CADatabaseExtension.class);

  public static final String POSTGRES_VERSION =
    "15.2";

  private static final CAPGDatabases DATABASES =
    new CAPGDatabases();

  private static final PostgreSQLContainer<?> CONTAINER =
    new PostgreSQLContainer<>(
      DockerImageName.parse("postgres")
        .withTag(POSTGRES_VERSION))
      .withDatabaseName("cardant")
      .withUsername("postgres")
      .withPassword("12345678");

  private CloseableCollectionType<ClosingResourceFailedException> resources;
  private CloseableCollectionType<ClosingResourceFailedException> perTestResources;
  private boolean started;
  private CADatabaseType database;
  private CADatabaseConfiguration databaseConfiguration;

  public CADatabaseExtension()
  {
    this.resources =
      CloseableCollection.create();
    this.perTestResources =
      CloseableCollection.create();
  }

  @Override
  public void beforeAll(
    final ExtensionContext context)
    throws Exception
  {
    if (!this.started) {
      this.started = true;

      context.getRoot()
        .getStore(GLOBAL)
        .put("com.io7m.cardant.tests.database.CADatabaseExtension", this);

      CONTAINER.start();
      CONTAINER.addEnv("PGPASSWORD", "12345678");
    }
  }

  @Override
  public void close()
    throws Throwable
  {
    LOG.debug("tearing down database container");
    this.resources.close();
  }

  @Override
  public void beforeEach(
    final ExtensionContext context)
    throws Exception
  {
    LOG.debug("setting up database");

    this.resources = CloseableCollection.create();
    this.resources.add(CONTAINER::stop);

    final var r0 =
      CONTAINER.execInContainer(
        "dropdb",
        "-w",
        "-U",
        "postgres",
        "cardant"
      );
    LOG.debug("stderr: {}", r0.getStderr());

    final var r1 =
      CONTAINER.execInContainer(
        "createdb",
        "-w",
        "-U",
        "postgres",
        "cardant"
      );

    LOG.debug("stderr: {}", r0.getStderr());
    assertEquals(0, r1.getExitCode());

    this.databaseConfiguration =
      new CADatabaseConfiguration(
        Locale.getDefault(),
        "postgres",
        "12345678",
        "12345678",
        Optional.of("12345678"),
        CONTAINER.getHost(),
        CONTAINER.getFirstMappedPort().intValue(),
        "cardant",
        CADatabaseCreate.CREATE_DATABASE,
        CADatabaseUpgrade.UPGRADE_DATABASE,
        "english",
        Clock.systemUTC(),
        CAStrings.create(Locale.ROOT)
      );

    this.perTestResources = CloseableCollection.create();
    this.database =
      this.perTestResources.add(DATABASES.open(
        this.databaseConfiguration,
        new CADatabaseTelemetry(
          true,
          OpenTelemetry.noop().getMeter("x"),
          OpenTelemetry.noop().getTracer("x")
        ),
        message -> {

        }
      ));
  }

  @Override
  public void afterEach(
    final ExtensionContext context)
    throws Exception
  {
    LOG.debug("tearing down database");
    this.perTestResources.close();
  }

  @Override
  public boolean supportsParameter(
    final ParameterContext parameterContext,
    final ExtensionContext extensionContext)
    throws ParameterResolutionException
  {
    final var requiredType =
      parameterContext.getParameter().getType();

    return Objects.equals(requiredType, CADatabaseType.class)
           || Objects.equals(requiredType, CADatabaseTransactionType.class)
           || Objects.equals(requiredType, CADatabaseConfiguration.class);
  }

  @Override
  public Object resolveParameter(
    final ParameterContext parameterContext,
    final ExtensionContext extensionContext)
    throws ParameterResolutionException
  {
    try {
      final var requiredType =
        parameterContext.getParameter().getType();

      if (Objects.equals(requiredType, CADatabaseType.class)) {
        return this.database;
      }

      if (Objects.equals(requiredType, CADatabaseConfiguration.class)) {
        return this.databaseConfiguration;
      }

      if (Objects.equals(requiredType, CADatabaseTransactionType.class)) {
        final var connection =
          this.perTestResources.add(
            this.database.openConnection(CADatabaseRole.CARDANT));
        final var transaction =
          this.perTestResources.add(connection.openTransaction());
        return transaction;
      }

      throw new IllegalStateException(
        "Unrecognized requested parameter type: %s".formatted(requiredType)
      );
    } catch (final CADatabaseException e) {
      throw new ParameterResolutionException(e.getMessage(), e);
    }
  }

  public static OffsetDateTime timeNow()
  {
    /*
     * Postgres doesn't store times at as high a resolution as the JVM,
     * so trim the nanoseconds off in order to ensure we can correctly
     * compare results returned from the database.
     */

    return now().withNano(0);
  }
}
