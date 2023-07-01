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
import com.io7m.cardant.server.api.CAServerConfiguration;
import com.io7m.cardant.server.api.CAServerException;
import com.io7m.cardant.server.api.CAServerFactoryType;
import com.io7m.cardant.server.api.CAServerHTTPServiceConfiguration;
import com.io7m.cardant.server.api.CAServerIdstoreConfiguration;
import com.io7m.cardant.server.api.CAServerLimitsConfiguration;
import com.io7m.cardant.server.api.CAServerType;
import com.io7m.cardant.server.basic.CAServers;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.cardant.tests.CATestProperties;
import com.io7m.ervilla.api.EContainerSpec;
import com.io7m.ervilla.api.EContainerSupervisorType;
import com.io7m.ervilla.api.EContainerType;
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
import io.opentelemetry.api.OpenTelemetry;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.io7m.ervilla.api.EPortProtocol.TCP;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.util.Optional.empty;

public final class CATestContainers
{
  private static final CAPGDatabases DATABASES =
    new CAPGDatabases();
  private static final CAServerFactoryType SERVERS =
    new CAServers();

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
          "cardant_install",
          "cardant"
        )
      );

      this.container.executeAndWaitIndefinitely(
        List.of(
          "createdb",
          "-w",
          "-U",
          "cardant_install",
          "cardant"
        )
      );
    }
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
          Optional.empty(),
          port,
          "cardant",
          "cardant_install",
          "12345678"
        ).build()
      );

    final var configuration =
      new CADatabaseConfiguration(
        "cardant_install",
        "12345678",
        "12345678",
        Optional.of("12345678"),
        "localhost",
        port,
        "cardant",
        CADatabaseCreate.CREATE_DATABASE,
        CADatabaseUpgrade.UPGRADE_DATABASE,
        "english",
        Clock.systemUTC(),
        CAStrings.create(Locale.ROOT)
      );

    return new CADatabaseFixture(
      container,
      configuration
    );
  }

  public record CAIdstoreFixture(
    EContainerType serverContainer,
    EContainerType databaseContainer,
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

      this.databaseContainer.executeAndWaitIndefinitely(
        List.of(
          "dropdb",
          "-w",
          "-U",
          "idstore_install",
          "idstore"
        )
      );

      this.databaseContainer.executeAndWaitIndefinitely(
        List.of(
          "createdb",
          "-w",
          "-U",
          "idstore_install",
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

    public UUID createUser(
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
              "http://localhost:%d".formatted(Integer.valueOf(this.adminAPIPort))
            ),
            Map.of()
          ),
          IdAClientException::ofError
        );

        final IdAResponseUserCreate response =
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

        return response.user().id();
      }
    }
  }

  public static CAIdstoreFixture createIdstore(
    final EContainerSupervisorType supervisor,
    final Path configurationDirectory,
    final int databasePort,
    final int adminAPIPort,
    final int userAPIPort,
    final int userViewPort)
    throws IOException, InterruptedException
  {
    final var pod =
      supervisor.createPod(List.of(
        new EPortPublish(empty(), databasePort, 5432, TCP),
        new EPortPublish(empty(), adminAPIPort, 51000, TCP),
        new EPortPublish(empty(), userAPIPort, 50000, TCP),
        new EPortPublish(empty(), userViewPort, 50001, TCP)
      ));

    final var databaseContainer =
      pod.start(
        EPgSpecs.builderFromDockerIO(
          CATestProperties.POSTGRESQL_VERSION,
          Optional.empty(),
          databasePort,
          "idstore",
          "idstore_install",
          "12345678"
        ).build()
      );

    Files.writeString(
      configurationDirectory.resolve("server.xml"),
      IDSTORE_CONFIGURATION_TEMPLATE.trim(),
      StandardCharsets.UTF_8,
      CREATE,
      TRUNCATE_EXISTING
    );

    final var serverContainer =
      pod.start(
        EContainerSpec.builder(
            "quay.io",
            "io7mcom/idstore",
            CATestProperties.IDSTORE_VERSION)
          .addVolumeMount(
            new EVolumeMount(
              configurationDirectory, "/idstore/etc")
          )
          .addArgument("server")
          .addArgument("--verbose")
          .addArgument("debug")
          .addArgument("--configuration")
          .addArgument("/idstore/etc/server.xml")
          .setReadyCheck(new CAIdstoreHealthcheck("localhost", 51000))
          .build()
      );

    final var fixture =
      new CAIdstoreFixture(
        serverContainer,
        databaseContainer,
        UUID.randomUUID(),
        "admin",
        "12345678",
        adminAPIPort,
        userAPIPort
      );

    fixture.initialAdmin();
    return fixture;
  }

  private static final String IDSTORE_CONFIGURATION_TEMPLATE = """
    <?xml version="1.0" encoding="UTF-8" ?>
    <Configuration xmlns="urn:com.io7m.idstore:configuration:1">
      <Branding ProductTitle="idstore"/>
      <Database Name="idstore"
                Kind="POSTGRESQL"
                OwnerRoleName="idstore_install"
                OwnerRolePassword="12345678"
                WorkerRolePassword="12345678"
                Address="localhost"
                Port="5432"
                Create="true"
                Upgrade="true"/>
      <HTTPServices>
        <HTTPServiceAdminAPI ListenAddress="[::]" ListenPort="51000" ExternalURI="http://[::]:51000/"/>
        <HTTPServiceUserAPI ListenAddress="[::]" ListenPort="50000" ExternalURI="http://[::]:50000/"/>
        <HTTPServiceUserView ListenAddress="[::]" ListenPort="50001" ExternalURI="http://[::]:50001/"/>
      </HTTPServices>
      <History UserLoginHistoryLimit="10" AdminLoginHistoryLimit="100"/>
      <Mail SenderAddress="cardant@example.com" VerificationExpiration="PT24H">
        <SMTP Host="localhost" Port="25"/>
      </Mail>
      <RateLimiting EmailVerificationRateLimit="PT1M"
                    UserLoginRateLimit="PT0S"
                    AdminLoginRateLimit="PT0S"
                    PasswordResetRateLimit="PT1M"/>
      <Sessions UserSessionExpiration="PT60M"
                AdminSessionExpiration="PT60M"/>
    </Configuration>
    """;

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
      final UUID userId,
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
    final CAServerConfiguration configuration =
      new CAServerConfiguration(
        Locale.ROOT,
        Clock.systemUTC(),
        CAStrings.create(Locale.ROOT),
        DATABASES,
        databaseFixture.configuration,
        new CAServerHTTPServiceConfiguration(
          "localhost",
          apiPort,
          URI.create("http://localhost:" + apiPort),
          Optional.of(Duration.ofHours(1L))
        ),
        new CAServerIdstoreConfiguration(
          URI.create("http://localhost:" + idstoreFixture.userAPIPort),
          URI.create("http://localhost:" + idstoreFixture.userAPIPort)
        ),
        new CAServerLimitsConfiguration(
          10_000_000L,
          1_000_000L
        ),
        Optional.empty()
      );

    final var fixture =
      new CAServerFixture(SERVERS.createServer(configuration));

    fixture.server.start();
    return fixture;
  }
}
