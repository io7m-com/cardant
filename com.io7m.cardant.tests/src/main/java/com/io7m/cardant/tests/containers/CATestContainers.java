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


package com.io7m.cardant.tests.containers;

import com.io7m.cardant.database.api.CADatabaseConfiguration;
import com.io7m.cardant.database.api.CADatabaseCreate;
import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseTelemetry;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.database.api.CADatabaseUpgrade;
import com.io7m.cardant.database.postgres.CAPGDatabases;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.server.api.CAServerConfiguration;
import com.io7m.cardant.server.api.CAServerException;
import com.io7m.cardant.server.api.CAServerFactoryType;
import com.io7m.cardant.server.api.CAServerHTTPServiceConfiguration;
import com.io7m.cardant.server.api.CAServerIdstoreConfiguration;
import com.io7m.cardant.server.api.CAServerLimitsConfiguration;
import com.io7m.cardant.server.api.CAServerMaintenanceConfiguration;
import com.io7m.cardant.server.api.CAServerType;
import com.io7m.cardant.server.basic.CAServers;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.cardant.tests.CATestProperties;
import com.io7m.cardant.tls.CATLSDisabled;
import com.io7m.cardant.type_packages.parsers.CATypePackageSerializers;
import com.io7m.ervilla.api.EContainerSpec;
import com.io7m.ervilla.api.EContainerSupervisorType;
import com.io7m.ervilla.api.EContainerType;
import com.io7m.ervilla.api.EPortProtocol;
import com.io7m.ervilla.api.EPortPublish;
import com.io7m.ervilla.api.EVolumeMount;
import com.io7m.ervilla.postgres.EPgSpecs;
import com.io7m.idstore.admin_client.IdAClients;
import com.io7m.idstore.admin_client.api.IdAClientConfiguration;
import com.io7m.idstore.admin_client.api.IdAClientCredentials;
import com.io7m.idstore.admin_client.api.IdAClientException;
import com.io7m.idstore.model.IdEmail;
import com.io7m.idstore.model.IdName;
import com.io7m.idstore.model.IdPasswordAlgorithmPBKDF2HmacSHA256;
import com.io7m.idstore.model.IdRealName;
import com.io7m.idstore.protocol.admin.IdACommandUserCreate;
import com.io7m.idstore.protocol.admin.IdAResponseUserCreate;
import com.io7m.idstore.server.api.IdServerBrandingConfiguration;
import com.io7m.idstore.server.api.IdServerConfigurationFile;
import com.io7m.idstore.server.api.IdServerDatabaseConfiguration;
import com.io7m.idstore.server.api.IdServerDatabaseKind;
import com.io7m.idstore.server.api.IdServerHTTPConfiguration;
import com.io7m.idstore.server.api.IdServerHTTPServiceConfiguration;
import com.io7m.idstore.server.api.IdServerHistoryConfiguration;
import com.io7m.idstore.server.api.IdServerMailConfiguration;
import com.io7m.idstore.server.api.IdServerMailTransportSMTP;
import com.io7m.idstore.server.api.IdServerMaintenanceConfiguration;
import com.io7m.idstore.server.api.IdServerPasswordExpirationConfiguration;
import com.io7m.idstore.server.api.IdServerRateLimitConfiguration;
import com.io7m.idstore.server.api.IdServerSessionConfiguration;
import com.io7m.idstore.server.service.configuration.IdServerConfigurationSerializers;
import com.io7m.idstore.tls.IdTLSDisabled;
import io.opentelemetry.api.OpenTelemetry;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.util.Optional.empty;

public final class CATestContainers
{
  private static final CAPGDatabases DATABASES =
    new CAPGDatabases();
  private static final CAServerFactoryType SERVERS =
    new CAServers();

  private static final String OWNER_ROLE =
    "postgres";

  private CATestContainers()
  {

  }

  /**
   * The basic database fixture.
   *
   * @param configuration The database configuration
   * @param container     The database container
   */

  public record CADatabaseFixture(
    EContainerType container,
    CADatabaseConfiguration configuration)
  {
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
      this.container.executeAndWaitIndefinitely(
        List.of(
          "dropdb",
          "-w",
          "-U",
          OWNER_ROLE,
          "cardant"
        )
      );

      this.container.executeAndWaitIndefinitely(
        List.of(
          "createdb",
          "-w",
          "-U",
          OWNER_ROLE,
          "cardant"
        )
      );
    }
  }

  public static String primaryIP()
  {
    final var socket = new Socket();
    try {
      socket.connect(new InetSocketAddress("www.example.com", 80));
    } catch (final IOException e) {
      // Don't care
    }
    return socket.getLocalAddress().getHostAddress();
  }

  public static CADatabaseFixture createDatabase(
    final EContainerSupervisorType supervisor,
    final int port)
    throws IOException, InterruptedException
  {
    final var container =
      supervisor.start(
        EPgSpecs.builderFromDockerIO(
          CATestProperties.POSTGRESQL_VERSION,
          Optional.of("[::]"),
          port,
          "cardant",
          OWNER_ROLE,
          "12345678"
        ).build()
      );

    final var configuration =
      new CADatabaseConfiguration(
        OWNER_ROLE,
        "12345678",
        "12345678",
        Optional.of("12345678"),
        primaryIP(),
        port,
        "cardant",
        CADatabaseCreate.CREATE_DATABASE,
        CADatabaseUpgrade.UPGRADE_DATABASE,
        "english",
        Clock.systemUTC(),
        CAStrings.create(Locale.ROOT),
        new CATypePackageSerializers()
      );

    return new CADatabaseFixture(
      container,
      configuration
    );
  }

  public static CADatabaseFixture createDatabaseWithHostilePasswords(
    final EContainerSupervisorType supervisor,
    final int port)
    throws IOException, InterruptedException
  {
    final var ownerRolePassword = "''\\'1";
    final var workerRolePassword = "''\\'2";
    final var readerRolePassword = "''\\'3";

    final var container =
      supervisor.start(
        EPgSpecs.builderFromDockerIO(
          CATestProperties.POSTGRESQL_VERSION,
          Optional.of("[::]"),
          port,
          "cardant",
          OWNER_ROLE,
          ownerRolePassword
        ).build()
      );

    final var configuration =
      new CADatabaseConfiguration(
        OWNER_ROLE,
        ownerRolePassword,
        workerRolePassword,
        Optional.of(readerRolePassword),
        primaryIP(),
        port,
        "cardant",
        CADatabaseCreate.CREATE_DATABASE,
        CADatabaseUpgrade.UPGRADE_DATABASE,
        "english",
        Clock.systemUTC(),
        CAStrings.create(Locale.ROOT),
        new CATypePackageSerializers()
      );

    return new CADatabaseFixture(
      container,
      configuration
    );
  }

  public record CAIdstoreFixture(
    EContainerType serverContainer,
    CADatabaseFixture databaseFixture,
    UUID adminId,
    String adminName,
    String adminPassword,
    int adminAPIPort, int userAPIPort)
  {
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
      this.serverContainer.stop();

      this.databaseFixture.container.executeAndWaitIndefinitely(
        List.of(
          "dropdb",
          "-w",
          "-U",
          OWNER_ROLE,
          "idstore"
        )
      );

      this.databaseFixture.container.executeAndWaitIndefinitely(
        List.of(
          "createdb",
          "-w",
          "-U",
          OWNER_ROLE,
          "idstore"
        )
      );

      this.serverContainer.start();
      this.initialAdmin();
    }

    private void initialAdmin()
      throws IOException, InterruptedException
    {
      this.serverContainer.executeAndWaitIndefinitely(
        List.of(
          "idstore",
          "initial-admin",
          "--configuration",
          "/idstore/etc/server.xml",
          "--admin-id",
          this.adminId.toString(),
          "--admin-username",
          "admin",
          "--admin-password",
          "12345678",
          "--admin-email",
          "admin@example.com",
          "--admin-realname",
          "admin"
        )
      );
    }

    public CAUserID createUser(
      final String userName)
      throws Exception
    {
      final var clients = new IdAClients();
      try (var client =
             clients.openSynchronousClient(
               new IdAClientConfiguration(Locale.ROOT))) {

        client.loginOrElseThrow(
          new IdAClientCredentials(
            this.adminName,
            this.adminPassword,
            URI.create(
              "http://" + primaryIP() + ":%d".formatted(Integer.valueOf(this.adminAPIPort))
            ),
            Map.of()
          ),
          IdAClientException::ofError
        );

        final var response =
          (IdAResponseUserCreate) client.executeOrElseThrow(
            new IdACommandUserCreate(
              empty(),
              new IdName(userName),
              new IdRealName(userName),
              new IdEmail("%s@example.com".formatted(userName)),
              IdPasswordAlgorithmPBKDF2HmacSHA256.create()
                .createHashed("12345678")
            ),
            IdAClientException::ofError
          );

        return new CAUserID(response.user().id());
      }
    }
  }

  public static CAIdstoreFixture createIdstore(
    final EContainerSupervisorType supervisor,
    final CADatabaseFixture databaseFixture,
    final Path configurationDirectory,
    final String databaseName,
    final int adminAPIPort,
    final int userAPIPort,
    final int userViewPort)
    throws Exception
  {
    final var idstoreConfiguration =
      new IdServerConfigurationFile(
        new IdServerBrandingConfiguration("idstore", empty(), empty(), empty()),
        new IdServerMailConfiguration(
          new IdServerMailTransportSMTP("localhost", 25),
          empty(),
          "sender@example.com",
          Duration.ofHours(1L)
        ),
        new IdServerHTTPConfiguration(
          new IdServerHTTPServiceConfiguration(
            "[::]",
            adminAPIPort,
            URI.create("http://[::]:" + adminAPIPort + "/"),
            IdTLSDisabled.TLS_DISABLED
          ),
          new IdServerHTTPServiceConfiguration(
            "[::]",
            userAPIPort,
            URI.create("http://[::]:" + userAPIPort + "/"),
            IdTLSDisabled.TLS_DISABLED
          ),
          new IdServerHTTPServiceConfiguration(
            "[::]",
            userViewPort,
            URI.create("http://[::]:" + userViewPort + "/"),
            IdTLSDisabled.TLS_DISABLED
          )
        ),
        new IdServerDatabaseConfiguration(
          IdServerDatabaseKind.POSTGRESQL,
          OWNER_ROLE,
          "12345678",
          "12345678",
          empty(),
          primaryIP(),
          databaseFixture.configuration.port(),
          databaseName,
          true,
          true
        ),
        new IdServerHistoryConfiguration(1, 1),
        new IdServerSessionConfiguration(
          Duration.ofHours(1000L),
          Duration.ofHours(1000L)),
        new IdServerRateLimitConfiguration(
          Duration.ofSeconds(1L),
          Duration.ofSeconds(1L),
          Duration.ofSeconds(1L),
          Duration.ofSeconds(0L),
          Duration.ofSeconds(1L),
          Duration.ofSeconds(0L)
        ),
        new IdServerPasswordExpirationConfiguration(
          empty(),
          empty()
        ),
        new IdServerMaintenanceConfiguration(empty()),
        empty()
      );

    new IdServerConfigurationSerializers()
      .serializeFile(
        configurationDirectory.resolve("server.xml"),
        idstoreConfiguration
      );

    databaseFixture.container.executeAndWait(
      List.of(
        "createdb",
        "-w",
        "-U",
        OWNER_ROLE,
        databaseName
      ),
      1L,
      TimeUnit.SECONDS
    );

    final var serverContainer =
      supervisor.start(
        EContainerSpec.builder(
            "quay.io",
            "io7mcom/idstore",
            CATestProperties.IDSTORE_VERSION)
          .addVolumeMount(
            new EVolumeMount(
              configurationDirectory, "/idstore/etc")
          )
          .addPublishPort(new EPortPublish(
            Optional.of("[::]"),
            userAPIPort,
            userAPIPort,
            EPortProtocol.TCP
          ))
          .addPublishPort(new EPortPublish(
            Optional.of("[::]"),
            userViewPort,
            userViewPort,
            EPortProtocol.TCP
          ))
          .addPublishPort(new EPortPublish(
            Optional.of("[::]"),
            adminAPIPort,
            adminAPIPort,
            EPortProtocol.TCP
          ))
          .addArgument("server")
          .addArgument("--verbose")
          .addArgument("debug")
          .addArgument("--configuration")
          .addArgument("/idstore/etc/server.xml")
          .setReadyCheck(new CAIdstoreHealthcheck("[::]", adminAPIPort))
          .build()
      );

    final var fixture =
      new CAIdstoreFixture(
        serverContainer,
        databaseFixture,
        UUID.randomUUID(),
        "admin",
        "12345678",
        adminAPIPort,
        userAPIPort
      );

    fixture.initialAdmin();
    return fixture;
  }

  public record CAServerFixture(
    CAServerType server)
    implements AutoCloseable
  {
    @Override
    public void close()
      throws Exception
    {
      this.server.close();
    }

    public void setUserAsAdmin(
      final CAUserID userId,
      final String userName)
      throws CAServerException
    {
      this.server.setUserAsAdmin(userId, new IdName(userName));
    }
  }

  public static CAServerFixture createServer(
    final CAIdstoreFixture idstoreFixture,
    final CADatabaseFixture databaseFixture,
    final int apiPort)
    throws IOException, CAServerException
  {
    final var configuration =
      new CAServerConfiguration(
        Locale.ROOT,
        Clock.systemUTC(),
        CAStrings.create(Locale.ROOT),
        DATABASES,
        databaseFixture.configuration,
        new CAServerHTTPServiceConfiguration(
          primaryIP(),
          apiPort,
          URI.create("http://" + primaryIP() + ":" + apiPort),
          Optional.of(Duration.ofHours(1L)),
          CATLSDisabled.TLS_DISABLED
        ),
        new CAServerIdstoreConfiguration(
          URI.create("http://" + primaryIP() + ":" + idstoreFixture.userAPIPort),
          URI.create("http://" + primaryIP() + ":" + idstoreFixture.userAPIPort)
        ),
        new CAServerLimitsConfiguration(
          10_000_000L,
          1_000_000L
        ),
        new CAServerMaintenanceConfiguration(
          empty()
        ),
        empty()
      );

    final var fixture =
      new CAServerFixture(SERVERS.createServer(configuration));

    fixture.server.start();
    return fixture;
  }
}
