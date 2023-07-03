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


package com.io7m.cardant.tests.integration;

import com.io7m.cardant.client.api.CAClientConfiguration;
import com.io7m.cardant.client.api.CAClientCredentials;
import com.io7m.cardant.client.api.CAClientException;
import com.io7m.cardant.client.api.CAClientSynchronousType;
import com.io7m.cardant.client.basic.CAClients;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.protocol.inventory.CAICommandFileGet;
import com.io7m.cardant.protocol.inventory.CAIResponseFileGet;
import com.io7m.cardant.tests.CATestDirectories;
import com.io7m.cardant.tests.containers.CATestContainers;
import com.io7m.ervilla.api.EContainerSupervisorType;
import com.io7m.ervilla.test_extension.ErvillaCloseAfterAll;
import com.io7m.ervilla.test_extension.ErvillaConfiguration;
import com.io7m.ervilla.test_extension.ErvillaExtension;
import com.io7m.idstore.model.IdName;
import com.io7m.zelador.test_extension.CloseableResourcesType;
import com.io7m.zelador.test_extension.ZeladorExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorApiMisuse;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorIo;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSecurityPolicyDenied;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
@Tag("client")
@ExtendWith({ErvillaExtension.class, ZeladorExtension.class})
@ErvillaConfiguration(disabledIfUnsupported = true)
public final class CAClientIT
{
  private static UUID USER_ADMIN;
  private static UUID USER;
  private static CATestContainers.CAIdstoreFixture IDSTORE;
  private static CATestContainers.CADatabaseFixture DATABASE;
  private static Path DIRECTORY;
  private CATestContainers.CAServerFixture server;

  @BeforeAll
  public static void setupOnce(
    final @ErvillaCloseAfterAll EContainerSupervisorType supervisor,
    final CloseableResourcesType closeables)
    throws Exception
  {
    DIRECTORY =
      Files.createTempDirectory("cardant-");
    DATABASE =
      CATestContainers.createDatabase(supervisor, 15433);
    IDSTORE =
      CATestContainers.createIdstore(
        supervisor,
        DIRECTORY,
        15432,
        51000,
        50000,
        50001
      );

    USER_ADMIN = IDSTORE.createUser("someone-admin");
    USER = IDSTORE.createUser("someone");

    closeables.addPerTestClassResource(
      () -> CATestDirectories.deleteDirectory(DIRECTORY)
    );
  }

  @BeforeEach
  public void setupEach(
    final CloseableResourcesType closeables)
    throws Exception
  {
    DATABASE.reset();

    this.directory =
      CATestDirectories.createTempDirectory();
    this.downloads =
      this.directory.resolve("downloads");

    this.clients =
      new CAClients();
    this.client =
      closeables.addPerTestResource(
        this.clients.openSynchronousClient(
          new CAClientConfiguration(Locale.ROOT, Clock.systemUTC())
        )
      );

    this.server =
      closeables.addPerTestResource(
        CATestContainers.createServer(
          IDSTORE,
          DATABASE,
          30000
        )
      );

    this.server.setUserAsAdmin(USER_ADMIN, "someone-admin");

    closeables.addPerTestResource(
      () -> CATestDirectories.deleteDirectory(this.directory)
    );
  }

  private CAClients clients;
  private CAClientSynchronousType client;
  private Path directory;
  private Path downloads;

  /**
   * Logging in fails if the user does not exist.
   */

  @Test
  public void testLoginNoSuchUser()
  {
    final var ex =
      assertThrows(CAClientException.class, () -> {
        this.client.loginOrElseThrow(
          new CAClientCredentials(
            "localhost",
            30000,
            false,
            new IdName("nonexistent"),
            "12345678",
            Map.of()
          ),
          CAClientException::ofError
        );
      });

    assertEquals(CAStandardErrorCodes.errorAuthentication(), ex.errorCode());
  }

  /**
   * Logging in succeeds if the user exists.
   *
   * @throws Exception On errors
   */

  @Test
  public void testLoginOK()
    throws Exception
  {
    this.login(new IdName("someone"));
  }

  /**
   * The version endpoint returns something sensible.
   *
   * @throws Exception On errors
   */

  @Test
  public void testServerVersionEndpoint()
    throws Exception
  {
    final var httpClient =
      HttpClient.newHttpClient();

    final var request =
      HttpRequest.newBuilder(
          this.server.server()
            .inventoryAPI()
            .resolve("/version")
            .normalize()
        ).GET()
        .build();

    final var response =
      httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(
      "text/plain",
      response.headers()
        .firstValue("content-type")
        .orElseThrow()
    );
    assertTrue(response.body().startsWith("com.io7m.cardant "));
  }

  /**
   * Uploading a file works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testFileUploadOK()
    throws Exception
  {
    this.login(new IdName("someone-admin"));

    final var file = this.directory.resolve("data.txt");
    Files.writeString(file, "HELLO!");

    final CAFileID fileId = CAFileID.random();
    this.client.fileUploadOrThrow(
      fileId,
      file,
      "text/plain",
      "A text file.",
      statistics -> {

      }
    );

    final var response =
      (CAIResponseFileGet)
        this.client.executeOrElseThrow(new CAICommandFileGet(fileId));

    final var data = response.data();
    assertEquals(
      "a2f6017f1fab81333a4288f68557b74495a27337c7d37b3eba46c866aa885098",
      data.hashValue()
    );
    assertEquals(
      "SHA-256",
      data.hashAlgorithm()
    );
    assertEquals(6L, data.size());

    this.client.fileDownloadOrThrow(
      fileId,
      this.directory.resolve("data2.txt"),
      this.directory.resolve("data2.txt.tmp"),
      6L,
      "SHA-256",
      "a2f6017f1fab81333a4288f68557b74495a27337c7d37b3eba46c866aa885098",
      statistics -> {

      }
    );

    assertEquals(
      "HELLO!",
      Files.readString(this.directory.resolve("data2.txt"))
    );
  }

  /**
   * Uploading a file fails without permissions.
   *
   * @throws Exception On errors
   */

  @Test
  public void testFileUploadNotPermitted()
    throws Exception
  {
    this.login(new IdName("someone"));

    final var file = this.directory.resolve("data.txt");
    Files.writeString(file, "HELLO!");

    final CAFileID fileId = CAFileID.random();

    final var ex =
      assertThrows(CAClientException.class, () -> {
        this.client.fileUploadOrThrow(
          fileId,
          file,
          "text/plain",
          "A text file.",
          statistics -> {

          }
        );
      });

    assertEquals(errorSecurityPolicyDenied(), ex.errorCode());
  }

  /**
   * Downloading a file fails without permissions.
   *
   * @throws Exception On errors
   */

  @Test
  public void testFileDownloadNotPermitted()
    throws Exception
  {
    this.login(new IdName("someone-admin"));

    final var file = this.directory.resolve("data.txt");
    final var fileTmp = this.directory.resolve("data.txt");
    Files.writeString(file, "HELLO!");

    final CAFileID fileId = CAFileID.random();
    this.client.fileUploadOrThrow(
      fileId,
      file,
      "text/plain",
      "A text file.",
      statistics -> {

      }
    );

    this.login(new IdName("someone"));

    {
      final var ex =
        assertThrows(CAClientException.class, () -> {
          this.client.executeOrElseThrow(new CAICommandFileGet(fileId));
        });

      assertEquals(errorSecurityPolicyDenied(), ex.errorCode());
    }

    {
      final var ex =
        assertThrows(CAClientException.class, () -> {
          this.client.fileDownloadOrThrow(
            fileId,
            file,
            fileTmp,
            6L,
            "SHA-256",
            "a2f6017f1fab81333a4288f68557b74495a27337c7d37b3eba46c866aa885098",
            statistics -> {

            }
          );
        });

      assertEquals(errorSecurityPolicyDenied(), ex.errorCode());
    }
  }

  /**
   * Sending garbage results in errors.
   *
   * @throws Exception On errors
   */

  @Test
  public void testGarbage()
    throws Exception
  {
    this.login(new IdName("someone-admin"));

    final var ex =
      assertThrows(CAClientException.class, () -> {
        this.client.garbageOrElseThrow();
      });

    assertEquals(errorIo(), ex.errorCode());
  }

  /**
   * Sending invalid messages results in errors.
   *
   * @throws Exception On errors
   */

  @Test
  public void testInvalid()
    throws Exception
  {
    this.login(new IdName("someone-admin"));

    final var ex =
      assertThrows(CAClientException.class, () -> {
        this.client.invalidOrElseThrow();
      });

    assertEquals(errorApiMisuse(), ex.errorCode());
  }

  private void login(
    final IdName userName)
    throws CAClientException, InterruptedException
  {
    this.client.loginOrElseThrow(
      new CAClientCredentials(
        this.server.server()
          .inventoryAPI()
          .getHost(),
        this.server.server()
          .inventoryAPI()
          .getPort(),
        false,
        userName,
        "12345678",
        Map.of()
      ),
      CAClientException::ofError
    );
  }
}
