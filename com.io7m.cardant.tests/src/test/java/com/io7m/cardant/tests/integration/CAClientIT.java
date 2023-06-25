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
import com.io7m.cardant.server.api.CAServerType;
import com.io7m.cardant.tests.CATestDirectories;
import com.io7m.cardant.tests.server.CAServerExtension;
import com.io7m.idstore.database.api.IdDatabaseException;
import com.io7m.idstore.database.api.IdDatabaseRole;
import com.io7m.idstore.database.api.IdDatabaseUsersQueriesType;
import com.io7m.idstore.model.IdEmail;
import com.io7m.idstore.model.IdName;
import com.io7m.idstore.model.IdPasswordAlgorithmPBKDF2HmacSHA256;
import com.io7m.idstore.model.IdPasswordException;
import com.io7m.idstore.model.IdRealName;
import com.io7m.idstore.server.api.IdServerException;
import com.io7m.idstore.server.api.IdServerType;
import com.io7m.idstore.tests.extensions.IdTestExtension;
import org.junit.jupiter.api.AfterEach;
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
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorApiMisuse;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorIo;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSecurityPolicyDenied;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
@Tag("client")
@ExtendWith({IdTestExtension.class, CAServerExtension.class})
public final class CAClientIT
{
  private CAClients clients;
  private CAClientSynchronousType client;
  private Path directory;
  private Path downloads;

  private static UUID createUser(
    final IdServerType idstore,
    final UUID adminId,
    final String user)
    throws IdDatabaseException, IdPasswordException
  {
    try (var connection =
           idstore.database()
             .openConnection(IdDatabaseRole.IDSTORE)) {
      try (var transaction = connection.openTransaction()) {
        transaction.adminIdSet(adminId);
        final var users =
          transaction.queries(IdDatabaseUsersQueriesType.class);
        final var userId = UUID.randomUUID();
        users.userCreate(
          userId,
          new IdName(user),
          new IdRealName("U"),
          new IdEmail(user + "@example.com"),
          OffsetDateTime.now(),
          IdPasswordAlgorithmPBKDF2HmacSHA256.create()
            .createHashed("12345678")
        );
        transaction.commit();
        return userId;
      }
    }
  }

  private static UUID createAdmin(
    final IdServerType idstore)
    throws IdServerException
  {
    final var adminId = UUID.randomUUID();

    idstore.close();
    idstore.setup(
      Optional.of(adminId),
      new IdName("admin"),
      new IdEmail("someone@example.com"),
      new IdRealName("AM"),
      "12345678"
    );
    idstore.start();
    return adminId;
  }

  @BeforeEach
  public void setup()
    throws Exception
  {
    this.directory =
      CATestDirectories.createTempDirectory();
    this.downloads =
      this.directory.resolve("downloads");

    this.clients =
      new CAClients();
    this.client =
      this.clients.openSynchronousClient(
        new CAClientConfiguration(Locale.ROOT, Clock.systemUTC()));
  }

  @AfterEach
  public void tearDown()
    throws Exception
  {
    this.client.close();
    CATestDirectories.deleteDirectory(this.directory);
  }

  /**
   * Logging in fails if the user does not exist.
   *
   * @param idstore The idstore server
   * @param server  The server
   */

  @Test
  public void testLoginNoSuchUser(
    final IdServerType idstore,
    final CAServerType server)
  {
    final var ex =
      assertThrows(CAClientException.class, () -> {
        this.client.loginOrElseThrow(
          new CAClientCredentials(
            server.configuration()
              .inventoryApiAddress()
              .externalAddress()
              .getHost(),
            server.configuration()
              .inventoryApiAddress()
              .externalAddress()
              .getPort(),
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
   * @param idstore The idstore server
   * @param server  The server
   *
   * @throws Exception On errors
   */

  @Test
  public void testLoginOK(
    final IdServerType idstore,
    final CAServerType server)
    throws Exception
  {
    final var adminId =
      createAdmin(idstore);
    final var userId =
      createUser(idstore, adminId, "someone-else");

    this.login(server, new IdName("someone-else"));
  }

  /**
   * The version endpoint returns something sensible.
   *
   * @param server The server
   *
   * @throws Exception On errors
   */

  @Test
  public void testServerVersionEndpoint(
    final CAServerType server)
    throws Exception
  {
    final var httpClient =
      HttpClient.newHttpClient();

    final var request =
      HttpRequest.newBuilder(
          server.configuration()
            .inventoryApiAddress()
            .externalAddress()
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
   * @param idstore The idstore server
   * @param server  The server
   *
   * @throws Exception On errors
   */

  @Test
  public void testFileUploadOK(
    final IdServerType idstore,
    final CAServerType server)
    throws Exception
  {
    final var adminId =
      createAdmin(idstore);
    final var userName =
      new IdName("someone-else");
    final var userId =
      createUser(idstore, adminId, userName.value());

    server.close();
    server.setUserAsAdmin(userId, userName);
    server.start();

    this.login(server, userName);

    final var file = this.directory.resolve("data.txt");
    Files.writeString(file, "HELLO!");

    final CAFileID fileId = CAFileID.random();
    this.client.fileUploadOrThrow(
      fileId,
      file,
      "text/plain",
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
   * @param idstore The idstore server
   * @param server  The server
   *
   * @throws Exception On errors
   */

  @Test
  public void testFileUploadNotPermitted(
    final IdServerType idstore,
    final CAServerType server)
    throws Exception
  {
    final var adminId =
      createAdmin(idstore);
    final var userName =
      new IdName("someone-else");
    final var userId =
      createUser(idstore, adminId, userName.value());

    this.login(server, userName);

    final var file = this.directory.resolve("data.txt");
    Files.writeString(file, "HELLO!");

    final CAFileID fileId = CAFileID.random();

    final var ex =
      assertThrows(CAClientException.class, () -> {
        this.client.fileUploadOrThrow(
          fileId,
          file,
          "text/plain",
          statistics -> {

          }
        );
      });

    assertEquals(errorSecurityPolicyDenied(), ex.errorCode());
  }

  /**
   * Downloading a file fails without permissions.
   *
   * @param idstore The idstore server
   * @param server  The server
   *
   * @throws Exception On errors
   */

  @Test
  public void testFileDownloadNotPermitted(
    final IdServerType idstore,
    final CAServerType server)
    throws Exception
  {
    final var adminId =
      createAdmin(idstore);

    final var userName0 =
      new IdName("someone-else0");
    final var userId0 =
      createUser(idstore, adminId, userName0.value());

    final var userName1 =
      new IdName("someone-else1");
    final var userId1 =
      createUser(idstore, adminId, userName1.value());

    server.close();
    server.setUserAsAdmin(userId0, userName0);
    server.start();

    this.login(server, userName0);

    final var file = this.directory.resolve("data.txt");
    final var fileTmp = this.directory.resolve("data.txt");
    Files.writeString(file, "HELLO!");

    final CAFileID fileId = CAFileID.random();
    this.client.fileUploadOrThrow(
      fileId,
      file,
      "text/plain",
      statistics -> {

      }
    );

    this.login(server, userName1);

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
   * @param idstore The idstore server
   * @param server  The server
   *
   * @throws Exception On errors
   */

  @Test
  public void testGarbage(
    final IdServerType idstore,
    final CAServerType server)
    throws Exception
  {
    final var adminId =
      createAdmin(idstore);
    final var userName =
      new IdName("someone-else");
    final var userId =
      createUser(idstore, adminId, userName.value());

    this.login(server, userName);

    final var ex =
      assertThrows(CAClientException.class, () -> {
        this.client.garbageOrElseThrow();
      });

    assertEquals(errorIo(), ex.errorCode());
  }

  /**
   * Sending invalid messages results in errors.
   *
   * @param idstore The idstore server
   * @param server  The server
   *
   * @throws Exception On errors
   */

  @Test
  public void testInvalid(
    final IdServerType idstore,
    final CAServerType server)
    throws Exception
  {
    final var adminId =
      createAdmin(idstore);
    final var userName =
      new IdName("someone-else");
    final var userId =
      createUser(idstore, adminId, userName.value());

    this.login(server, userName);

    final var ex =
      assertThrows(CAClientException.class, () -> {
        this.client.invalidOrElseThrow();
      });

    assertEquals(errorApiMisuse(), ex.errorCode());
  }

  private void login(
    final CAServerType server,
    final IdName userName)
    throws CAClientException, InterruptedException
  {
    this.client.loginOrElseThrow(
      new CAClientCredentials(
        server.configuration()
          .inventoryApiAddress()
          .externalAddress()
          .getHost(),
        server.configuration()
          .inventoryApiAddress()
          .externalAddress()
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
