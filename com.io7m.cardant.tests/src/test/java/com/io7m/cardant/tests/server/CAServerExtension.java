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


package com.io7m.cardant.tests.server;

import com.io7m.cardant.database.api.CADatabaseConfiguration;
import com.io7m.cardant.database.api.CADatabaseCreate;
import com.io7m.cardant.database.api.CADatabaseUpgrade;
import com.io7m.cardant.database.postgres.CAPGDatabases;
import com.io7m.cardant.server.api.CAServerConfiguration;
import com.io7m.cardant.server.api.CAServerHTTPServiceConfiguration;
import com.io7m.cardant.server.api.CAServerIdstoreConfiguration;
import com.io7m.cardant.server.api.CAServerLimitsConfiguration;
import com.io7m.cardant.server.api.CAServerType;
import com.io7m.cardant.server.basic.CAServers;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.idstore.tests.extensions.IdTestExtension;
import com.io7m.jmulticlose.core.CloseableCollection;
import com.io7m.jmulticlose.core.CloseableCollectionType;
import com.io7m.jmulticlose.core.ClosingResourceFailedException;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.net.URI;
import java.time.Clock;
import java.time.Duration;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@ExtendWith(IdTestExtension.class)
public final class CAServerExtension
  implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback,
  ExtensionContext.Store.CloseableResource,
  ParameterResolver
{
  /**
   * The PostgreSQL server version.
   */

  public static final String POSTGRES_VERSION =
    "15.2";

  private static final Logger LOG =
    LoggerFactory.getLogger(CAServerExtension.class);

  private static final CAPGDatabases DATABASES =
    new CAPGDatabases();

  private static final CAServers SERVERS =
    new CAServers();

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
  private CADatabaseConfiguration databaseConfiguration;
  private CAServerConfiguration serverConfiguration;
  private CAServerType server;
  private CAServerHTTPServiceConfiguration apiConfiguration;

  /**
   * An extension that provides a working cardant server and database.
   */

  public CAServerExtension()
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
        .put(CAServerExtension.class.getCanonicalName(), this);

      CONTAINER.start();
      CONTAINER.addEnv("PGPASSWORD", "12345678");
    }
  }

  @Override
  public void close()
    throws Throwable
  {
    LOG.debug("tearing down server");
    this.resources.close();
  }

  @Override
  public void beforeEach(
    final ExtensionContext context)
    throws Exception
  {
    LOG.debug("setting up server");

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
        Locale.ROOT,
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

    this.perTestResources =
      CloseableCollection.create();

    this.apiConfiguration =
      new CAServerHTTPServiceConfiguration(
        "localhost",
        33000,
        URI.create("http://localhost:33000/"),
        Optional.of(Duration.ofHours(1L))
      );

    final var idTestExtension =
      context.getStore(GLOBAL)
        .get(IdTestExtension.class.getCanonicalName(), IdTestExtension.class);

    if (idTestExtension == null) {
      throw new IllegalStateException(
        ("The %s extension is not present; the test class must also be "
         + "annotated with @ExtendWith(IdTestExtension.class)")
          .formatted(IdTestExtension.class.getCanonicalName()));
    }

    final var idstoreConfiguration =
      idTestExtension.serverConfiguration();

    final var idstoreServerConfiguration =
      new CAServerIdstoreConfiguration(
        idstoreConfiguration.userApiAddress()
          .externalAddress(),
        idstoreConfiguration.userViewAddress()
          .externalAddress()
      );

    final var limitsConfiguration =
      new CAServerLimitsConfiguration(
        1000000L,
        1000000L
      );

    this.serverConfiguration =
      new CAServerConfiguration(
        Locale.ROOT,
        Clock.systemUTC(),
        CAStrings.create(Locale.ROOT),
        DATABASES,
        this.databaseConfiguration,
        this.apiConfiguration,
        idstoreServerConfiguration,
        limitsConfiguration,
        Optional.empty()
      );

    this.server = SERVERS.createServer(this.serverConfiguration);
    this.perTestResources.add(this.server);
    this.server.start();
  }

  @Override
  public void afterEach(
    final ExtensionContext context)
    throws Exception
  {
    LOG.debug("tearing down server");
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

    return Objects.equals(requiredType, CAServerType.class);
  }

  @Override
  public Object resolveParameter(
    final ParameterContext parameterContext,
    final ExtensionContext extensionContext)
    throws ParameterResolutionException
  {
    final var requiredType =
      parameterContext.getParameter().getType();

    if (Objects.equals(requiredType, CAServerType.class)) {
      return this.server;
    }

    throw new IllegalStateException(
      "Unrecognized requested parameter type: %s".formatted(requiredType)
    );
  }
}
