/*
 * Copyright © 2021 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.tests;

import com.io7m.cardant.client.api.CAClientCommandOK;
import com.io7m.cardant.client.api.CAClientConfiguration;
import com.io7m.cardant.client.api.CAClientEventStatusChanged;
import com.io7m.cardant.client.api.CAClientEventType;
import com.io7m.cardant.client.api.CAClientType;
import com.io7m.cardant.client.vanilla.CAClients;
import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.model.CAItems;
import com.io7m.cardant.model.CAListLocationBehaviourType;
import com.io7m.cardant.model.CAModelDatabaseQueriesType;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.model.CAUsers;
import com.io7m.cardant.server.CAServers;
import com.io7m.cardant.server.api.CAServerConfiguration;
import com.io7m.cardant.server.api.CAServerConfigurationLimits;
import com.io7m.cardant.server.api.CAServerDatabaseLocalConfiguration;
import com.io7m.cardant.server.api.CAServerHTTPConfiguration;
import com.io7m.cardant.server.api.CAServerType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.OptionalLong;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import static com.io7m.cardant.client.api.CAClientEventStatusChanged.CLIENT_CONNECTED;
import static com.io7m.cardant.client.api.CAClientEventStatusChanged.CLIENT_NEGOTIATING_PROTOCOLS;
import static com.io7m.cardant.client.api.CAClientEventStatusChanged.CLIENT_NEGOTIATING_PROTOCOLS_FAILED;
import static com.io7m.cardant.model.CAListLocationBehaviourType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;

public final class CAClientTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAClientTest.class);

  private static final Duration TEST_TIMEOUT =
    Duration.of(10L, ChronoUnit.SECONDS);

  private Path directory;
  private Path databaseDirectory;
  private Path sessionDirectory;
  private CAServerType server;
  private CADatabaseType database;
  private CAServers servers;
  private CAClients clients;
  private CAClientConfiguration clientConfiguration;
  private CAClientType client;

  @BeforeEach
  public void setup()
    throws Exception
  {
    this.directory =
      CATestDirectories.createTempDirectory();
    this.databaseDirectory =
      this.directory.resolve("database");
    this.sessionDirectory =
      this.directory.resolve("sessions");

    final var serverConfiguration =
      new CAServerConfiguration(
        new CAServerHTTPConfiguration(10000, this.sessionDirectory),
        new CAServerDatabaseLocalConfiguration(this.databaseDirectory, true),
        new CAServerConfigurationLimits(
          OptionalLong.of(100L)
        )
      );

    this.servers =
      new CAServers();
    this.server =
      this.servers.createServer(serverConfiguration);
    this.database =
      this.server.database();

    this.clients =
      new CAClients();
    this.clientConfiguration =
      new CAClientConfiguration(
        "localhost",
        10000,
        false,
        "someone",
        "12345678");
  }

  @AfterEach
  public void tearDown()
    throws IOException
  {
    this.server.close();
    this.client.close();
    LOG.debug("deleting {}", this.directory);
    CATestDirectories.deleteDirectory(this.directory);
  }

  /**
   * Opening a client succeeds if the versioning succeeds.
   *
   * @throws Exception On errors
   */

  @Test
  public void testVersioningOK()
    throws Exception
  {
    this.createStandardUser();

    final var queue =
      new ArrayBlockingQueue<CAClientEventType>(2);
    final var queueSubscriber =
      new CAQueueSubscriber<>(
        queue, o -> o instanceof CAClientEventStatusChanged);

    assertTimeout(TEST_TIMEOUT, () -> {
      this.client = this.clients.open(this.clientConfiguration);
      this.client.events().subscribe(queueSubscriber);
      assertEquals(CLIENT_NEGOTIATING_PROTOCOLS, queue.take());
      assertEquals(CLIENT_CONNECTED, queue.take());
    });
  }

  /**
   * Opening a client fails if the versioning fails.
   *
   * @throws Exception On errors
   */

  @Test
  public void testVersioningFails()
    throws Exception
  {
    this.server.close();

    final var queue =
      new ArrayBlockingQueue<CAClientEventType>(2);
    final var queueSubscriber =
      new CAQueueSubscriber<>(
        queue, o -> o instanceof CAClientEventStatusChanged);

    assertTimeout(TEST_TIMEOUT, () -> {
      this.client = this.clients.open(this.clientConfiguration);
      this.client.events().subscribe(queueSubscriber);
      assertEquals(CLIENT_NEGOTIATING_PROTOCOLS, queue.take());
      assertEquals(CLIENT_NEGOTIATING_PROTOCOLS_FAILED, queue.take());
    });
  }

  /**
   * Listing items works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testListItemsOK()
    throws Exception
  {
    this.createStandardUser();

    final var queue =
      new ArrayBlockingQueue<CAClientEventType>(2);
    final var queueSubscriber =
      new CAQueueSubscriber<>(
        queue, o -> o instanceof CAClientEventStatusChanged);

    assertTimeout(TEST_TIMEOUT, () -> {
      this.client = this.clients.open(this.clientConfiguration);
      this.client.events().subscribe(queueSubscriber);
      assertEquals(CLIENT_NEGOTIATING_PROTOCOLS, queue.take());
      assertEquals(CLIENT_CONNECTED, queue.take());
      final CAClientCommandOK<CAItems> items =
        (CAClientCommandOK<CAItems>)
          this.client.itemsList(new CAListLocationsAll())
          .get();
      assertEquals(Set.of(), items.result().items());
    });
  }

  private void createStandardUser()
    throws CADatabaseException, GeneralSecurityException
  {
    this.createUser(
      this.clientConfiguration.username(),
      this.clientConfiguration.password()
    );
  }

  private void createUser(
    final String someone,
    final String password)
    throws CADatabaseException, GeneralSecurityException
  {
    try (final var connection = this.database.openConnection()) {
      try (var transaction = connection.beginTransaction()) {
        final var queries =
          transaction.queries(CAModelDatabaseQueriesType.class);
        queries.userPut(CAUsers.createUser(
          SecureRandom.getInstanceStrong(),
          CAUserID.random(),
          someone,
          password
        ));
        transaction.commit();
      }
    }
  }
}
