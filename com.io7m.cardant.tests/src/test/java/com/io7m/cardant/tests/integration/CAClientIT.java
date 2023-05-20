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
import com.io7m.cardant.protocol.inventory.CAIResponseLogin;
import com.io7m.cardant.server.api.CAServerType;
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
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

  private static UUID createUser(
    final IdServerType idstore,
    final UUID adminId,
    final String user)
    throws IdDatabaseException, IdPasswordException
  {
    try (var connection =
           idstore.database()
             .openConnection(IdDatabaseRole.ADMIN)) {
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
    this.clients =
      new CAClients();
    this.client =
      this.clients.openSynchronousClient(
        new CAClientConfiguration(Locale.ROOT));
  }

  @AfterEach
  public void tearDown()
    throws CAClientException
  {
    this.client.close();
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

    final var response =
      (CAIResponseLogin)
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
            new IdName("someone-else"),
            "12345678",
            Map.of()
          ),
          CAClientException::ofError
        );
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
}
