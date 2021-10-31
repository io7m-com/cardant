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

import com.io7m.anethum.common.ParseException;
import com.io7m.anethum.common.SerializeException;
import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.model.CAIds;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CAItems;
import com.io7m.cardant.model.CAModelDatabaseQueriesType;
import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CATagID;
import com.io7m.cardant.model.CATags;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.model.CAUsers;
import com.io7m.cardant.protocol.inventory.api.CACommandType;
import com.io7m.cardant.protocol.inventory.api.CAMessageParserFactoryType;
import com.io7m.cardant.protocol.inventory.api.CAMessageSerializerFactoryType;
import com.io7m.cardant.protocol.inventory.api.CAMessageServices;
import com.io7m.cardant.protocol.inventory.api.CAMessageServicesType;
import com.io7m.cardant.protocol.inventory.api.CAMessageType;
import com.io7m.cardant.protocol.inventory.api.CAResponseType;
import com.io7m.cardant.protocol.inventory.api.CATransaction;
import com.io7m.cardant.protocol.inventory.api.CATransactionResponse;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationsAll;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemCreate;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemGet;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemList;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemMetadataPut;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemMetadataRemove;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemUpdate;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemsRemove;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandLoginUsernamePassword;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandTagList;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandTagsDelete;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandTagsPut;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseError;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemGet;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemList;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemMetadataPut;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemMetadataRemove;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemUpdate;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemsRemove;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseLoginUsernamePassword;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseTagList;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseTagsDelete;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseTagsPut;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class CAServerV1Test
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAServerV1Test.class);

  private static final URI INPUT =
    URI.create("urn:input");
  private static final URI URI_LOGIN =
    URI.create("http://localhost:10000/v1/login");
  private static final URI URI_COMMAND =
    URI.create("http://localhost:10000/v1/command");

  private Path directory;
  private Path databaseDirectory;
  private Path sessionDirectory;
  private CAServerType server;
  private CADatabaseType database;
  private CAMessageServicesType messages;
  private CookieManager cookies;
  private HttpClient client;
  private CAServers servers;
  private CAMessageParserFactoryType parsers;
  private CAMessageSerializerFactoryType serializers;

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
    this.messages =
      new CAMessageServices();

    this.parsers =
      this.messages.findParserService(1);
    this.serializers =
      this.messages.findSerializerService(1);

    this.cookies =
      new CookieManager();
    this.client =
      HttpClient.newBuilder()
        .cookieHandler(this.cookies)
        .build();
  }

  @AfterEach
  public void tearDown()
    throws IOException
  {
    this.cookies.getCookieStore()
      .removeAll();

    this.server.close();
    LOG.debug("deleting {}", this.directory);
    CATestDirectories.deleteDirectory(this.directory);
  }

  /**
   * Trying to log in without any sending anything fails.
   *
   * @throws Exception On errors
   */

  @Test
  public void testLoginNothing()
    throws Exception
  {
    final var response =
      this.client.send(
        HttpRequest.newBuilder(URI_LOGIN)
          .POST(HttpRequest.BodyPublishers.noBody())
          .build(),
        HttpResponse.BodyHandlers.ofInputStream()
      );

    assertEquals(401, response.statusCode());

    final var message =
      (CAResponseError) this.parsers.parse(URI_LOGIN, response.body());
    assertEquals(401, message.statusCode());
    assertEquals("Login failed.", message.summary());
  }

  /**
   * Trying to log in with a nonexistent user fails.
   *
   * @throws Exception On errors
   */

  @Test
  public void testLoginUserNonexistent()
    throws Exception
  {
    this.createUser("someone", "1234");

    final var response =
      this.send(
        URI_LOGIN,
        new CACommandLoginUsernamePassword("someone_else", "1234"));

    assertEquals(401, response.statusCode());

    final var message =
      (CAResponseError) this.parsers.parse(URI_LOGIN, response.body());
    assertEquals(401, message.statusCode());
    assertEquals("Login failed.", message.summary());
  }

  /**
   * Trying to log in with an incorrect password fails.
   *
   * @throws Exception On errors
   */

  @Test
  public void testLoginUserPasswordWrong()
    throws Exception
  {
    this.createUser("someone", "1234");

    final var response =
      this.send(
        URI_LOGIN,
        new CACommandLoginUsernamePassword("someone", "12345"));

    assertEquals(401, response.statusCode());

    final var message =
      (CAResponseError) this.parsers.parse(URI_LOGIN, response.body());
    assertEquals(401, message.statusCode());
    assertEquals("Login failed.", message.summary());
  }

  /**
   * Logging in with a correct username and password succeeds.
   *
   * @throws Exception On errors
   */

  @Test
  public void testLoginOK()
    throws Exception
  {
    this.createUser("someone", "1234");

    final var response =
      this.send(
        URI_LOGIN,
        new CACommandLoginUsernamePassword("someone", "1234"));

    assertEquals(200, response.statusCode());

    final var message =
      (CAResponseLoginUsernamePassword)
        this.parsers.parse(URI_LOGIN, response.body());
  }

  /**
   * The server rejects nonsense.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRejectsNonsense0()
    throws Exception
  {
    this.createUser("someone", "1234");

    {
      final var response =
        this.send(
          URI_LOGIN,
          new CACommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var response =
      this.send(
        URI_COMMAND,
        new CAResponseError("Hello", 200, Map.of(), List.of()));
    final var message =
      (CAResponseError) this.parse(response);

    assertEquals(400, response.statusCode());
  }

  /**
   * Listing an empty database succeeds (tags).
   *
   * @throws Exception On errors
   */

  @Test
  public void testTagsListEmpty()
    throws Exception
  {
    this.createUser("someone", "1234");

    {
      final var response =
        this.send(
          URI_LOGIN,
          new CACommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var response =
      this.send(URI_COMMAND, new CACommandTagList());
    final var message =
      (CAResponseTagList) this.parse(response);

    assertEquals(200, response.statusCode());
    assertEquals(
      new CATags(Collections.emptySortedSet()),
      message.data()
    );
  }

  /**
   * Listing an empty database succeeds (items).
   *
   * @throws Exception On errors
   */

  @Test
  public void testListItemsEmpty()
    throws Exception
  {
    this.createUser("someone", "1234");

    {
      final var response =
        this.send(
          URI_LOGIN,
          new CACommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var response =
      this.send(URI_COMMAND, new CACommandItemList(new CAListLocationsAll()));
    final var message =
      (CAResponseItemList) this.parse(response);

    assertEquals(200, response.statusCode());
    assertEquals(new CAItems(Set.of()), message.data());
  }

  /**
   * Adding and retrieving tags works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTagsCreateList()
    throws Exception
  {
    this.createUser("someone", "1234");

    {
      final var response =
        this.send(
          URI_LOGIN,
          new CACommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var tagSet = new TreeSet<CATag>();
    tagSet.add(new CATag(CATagID.random(), "tag0"));
    tagSet.add(new CATag(CATagID.random(), "tag1"));
    tagSet.add(new CATag(CATagID.random(), "tag2"));
    final var tags = new CATags(tagSet);

    {
      final var response =
        this.send(URI_COMMAND, new CACommandTagsPut(tags));
      final var message =
        (CAResponseTagsPut) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(tags, message.data());
    }

    {
      final var response =
        this.send(URI_COMMAND, new CACommandTagList());
      final var message =
        (CAResponseTagList) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(tags, message.data());
    }

    {
      final var response =
        this.send(URI_COMMAND, new CACommandTagsDelete(tags));
      final var message =
        (CAResponseTagsDelete) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(tags, message.data());
    }

    {
      final var response =
        this.send(URI_COMMAND, new CACommandTagList());
      final var message =
        (CAResponseTagList) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(
        new CATags(Collections.emptySortedSet()),
        message.data());
    }
  }

  /**
   * Adding and retrieving items works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemsCreateList()
    throws Exception
  {
    this.createUser("someone", "1234");

    {
      final var response =
        this.send(
          URI_LOGIN,
          new CACommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var item0 = CAItem.create();

    {
      this.sendCommandAssumingSuccess(
        new CACommandItemCreate(item0.id(), item0.name()));
    }

    {
      final var response =
        this.send(URI_COMMAND, new CACommandItemList(new CAListLocationsAll()));
      final var message =
        (CAResponseItemList) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(new CAItems(Set.of(item0)), message.data());
    }

    final var item1 =
      new CAItem(
        item0.id(),
        "Item 0",
        0L,
        0L,
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    {
      final var response =
        this.send(
          URI_COMMAND,
          new CACommandItemUpdate(
            item0.id(),
            "Item 0"));
      final var message =
        (CAResponseItemUpdate) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(item1, message.data());
    }

    {
      final var response =
        this.send(URI_COMMAND, new CACommandItemList(new CAListLocationsAll()));
      final var message =
        (CAResponseItemList) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(new CAItems(Set.of(item1)), message.data());
    }

    {
      final var response =
        this.send(
          URI_COMMAND,
          new CACommandItemsRemove(Set.of(item0.id())));
      final var message =
        (CAResponseItemsRemove) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(new CAIds(Set.of(item0.id())), message.data());
    }

    {
      final var response =
        this.send(URI_COMMAND, new CACommandItemList(new CAListLocationsAll()));
      final var message =
        (CAResponseItemList) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(new CAItems(Set.of()), message.data());
    }
  }

  /**
   * Adding and retrieving items works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemsCreateGet()
    throws Exception
  {
    this.createUser("someone", "1234");

    {
      final var response =
        this.send(
          URI_LOGIN,
          new CACommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var item0 = CAItem.create();

    this.sendCommandAssumingSuccess(
      new CACommandItemCreate(item0.id(), item0.name()));

    {
      final var response =
        this.send(URI_COMMAND, new CACommandItemGet(item0.id()));
      final var message =
        (CAResponseItemGet) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(item0, message.data());
    }
  }

  /**
   * Retrieving nonexistent items fails.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemsGetNonexistent()
    throws Exception
  {
    this.createUser("someone", "1234");

    {
      final var response =
        this.send(
          URI_LOGIN,
          new CACommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    {
      final var response =
        this.send(URI_COMMAND, new CACommandItemGet(CAItemID.random()));
      final var message =
        (CAResponseError) this.parse(response);

      assertEquals(404, response.statusCode());
    }
  }

  /**
   * Adding duplicate items fails.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemsCreateDuplicate()
    throws Exception
  {
    this.createUser("someone", "1234");

    {
      final var response =
        this.send(
          URI_LOGIN,
          new CACommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var item0 = CAItem.create();

    {
      this.sendCommandAssumingSuccess(
        new CACommandItemCreate(item0.id(), item0.name()));
    }

    {
      final var response =
        this.send(
          URI_COMMAND,
          new CACommandItemCreate(
            item0.id(),
            item0.name()));
      final var message =
        (CAResponseError) this.parse(response);

      assertEquals(400, response.statusCode());
    }
  }

  /**
   * Updating a nonexistent item fails.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemsUpdateNonexistent()
    throws Exception
  {
    this.createUser("someone", "1234");

    {
      final var response =
        this.send(
          URI_LOGIN,
          new CACommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var item0 = CAItem.create();

    {
      final var response =
        this.send(
          URI_COMMAND,
          new CACommandItemUpdate(item0.id(), item0.name()));
      final var message =
        (CAResponseError) this.parse(response);

      assertEquals(400, response.statusCode());
    }
  }

  /**
   * Updating metadata for a nonexistent item fails.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemsUpdateMetadataNonexistent()
    throws Exception
  {
    this.createUser("someone", "1234");

    {
      final var response =
        this.send(
          URI_LOGIN,
          new CACommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var item0 = CAItem.create();

    final var metadata = new HashSet<CAItemMetadata>();
    metadata.add(new CAItemMetadata("A", "0"));
    metadata.add(new CAItemMetadata("B", "1"));
    metadata.add(new CAItemMetadata("C", "2"));

    {
      final var response =
        this.send(
          URI_COMMAND,
          new CACommandItemMetadataPut(item0.id(), metadata));
      final var message =
        (CAResponseError) this.parse(response);

      assertEquals(400, response.statusCode());
    }
  }

  /**
   * Removing metadata for a nonexistent item fails.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemsRemoveMetadataNonexistent()
    throws Exception
  {
    this.createUser("someone", "1234");

    {
      final var response =
        this.send(
          URI_LOGIN,
          new CACommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var item0 = CAItem.create();

    final var metadata = new HashSet<CAItemMetadata>();
    metadata.add(new CAItemMetadata("A", "0"));
    metadata.add(new CAItemMetadata("B", "1"));
    metadata.add(new CAItemMetadata("C", "2"));

    {
      final var response =
        this.send(
          URI_COMMAND,
          new CACommandItemMetadataRemove(
            item0.id(),
            metadata.stream()
              .map(CAItemMetadata::name)
              .collect(Collectors.toSet())));
      final var message =
        (CAResponseError) this.parse(response);

      assertEquals(400, response.statusCode());
    }
  }

  /**
   * Adding and retrieving items transactionally works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemsCreateListTransaction()
    throws Exception
  {
    this.createUser("someone", "1234");

    {
      final var response =
        this.send(
          URI_LOGIN,
          new CACommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var item0 = CAItem.create();
    final var item1 = CAItem.create();
    final var item2 = CAItem.create();

    {
      final var transaction =
        new CATransaction(
          List.of(
            new CACommandItemCreate(item0.id(), item0.name()),
            new CACommandItemCreate(item1.id(), item1.name()),
            new CACommandItemCreate(item2.id(), item2.name()),
            new CACommandItemList(new CAListLocationsAll())
          )
        );

      final var response =
        this.send(URI_COMMAND, transaction);
      final var message =
        (CATransactionResponse) this.parse(response);

      assertEquals(200, response.statusCode());
    }
  }

  /**
   * Adding and retrieving items transactionally works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemsCreateListTransactionFails()
    throws Exception
  {
    this.createUser("someone", "1234");

    {
      final var response =
        this.send(
          URI_LOGIN,
          new CACommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    {
      final var response =
        this.send(URI_COMMAND, new CACommandItemList(new CAListLocationsAll()));
      final var message =
        (CAResponseItemList) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(new CAItems(Set.of()), message.data());
    }

    final var item0 = CAItem.create();

    {
      final var transaction =
        new CATransaction(
          List.of(
            new CACommandItemCreate(item0.id(), item0.name()),
            new CACommandItemCreate(item0.id(), item0.name())
          )
        );

      final var response =
        this.send(URI_COMMAND, transaction);
      final var message =
        (CATransactionResponse) this.parse(response);

      assertEquals(500, response.statusCode());
    }

    {
      final var response =
        this.send(URI_COMMAND, new CACommandItemList(new CAListLocationsAll()));
      final var message =
        (CAResponseItemList) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(new CAItems(Set.of()), message.data());
    }
  }

  /**
   * Adding, updating, and removing metadatas works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemsCreateMetadata()
    throws Exception
  {
    this.createUser("someone", "1234");

    {
      final var response =
        this.send(
          URI_LOGIN,
          new CACommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var item0 = CAItem.create();

    {
      this.sendCommandAssumingSuccess(
        new CACommandItemCreate(item0.id(), item0.name()));
    }

    final var metadata = new HashSet<CAItemMetadata>();
    metadata.add(new CAItemMetadata("A", "0"));
    metadata.add(new CAItemMetadata("B", "1"));
    metadata.add(new CAItemMetadata("C", "2"));

    {
      final var response =
        this.send(
          URI_COMMAND,
          new CACommandItemMetadataPut(item0.id(), metadata));
      final var message =
        (CAResponseItemMetadataPut) this.parse(response);

      assertEquals(200, response.statusCode());
    }

    final var metaMap =
      metadata.stream()
        .map(m -> Map.entry(m.name(), m))
        .collect(new SortedMapEntryCollector<>());

    final var itemWith =
      new CAItem(
        item0.id(),
        item0.name(),
        item0.countTotal(),
        item0.countHere(),
        metaMap,
        item0.attachments(),
        item0.tags()
      );

    {
      final var response =
        this.send(URI_COMMAND, new CACommandItemList(new CAListLocationsAll()));
      final var message =
        (CAResponseItemList) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(new CAItems(Set.of(itemWith)), message.data());
    }

    final var metadataRemove = new TreeMap<String, CAItemMetadata>();
    metadataRemove.put(
      new CAItemMetadata("B", "1").name(),
      new CAItemMetadata("B", "1"));
    metadataRemove.put(
      new CAItemMetadata("C", "2").name(),
      new CAItemMetadata("C", "2"));

    {
      final var response =
        this.send(
          URI_COMMAND,
          new CACommandItemMetadataRemove(
            item0.id(),
            metadataRemove.keySet()));
      final var message =
        (CAResponseItemMetadataRemove) this.parse(response);

      assertEquals(200, response.statusCode());
    }

    final var metadataRemoved = new TreeMap<String, CAItemMetadata>();
    metadataRemoved.put(
      new CAItemMetadata("A", "0").name(),
      new CAItemMetadata("A", "0"));

    final var itemWithRemoved =
      new CAItem(
        item0.id(),
        item0.name(),
        item0.countTotal(),
        item0.countHere(),
        metadataRemoved,
        item0.attachments(),
        item0.tags()
      );

    {
      final var response =
        this.send(URI_COMMAND, new CACommandItemList(new CAListLocationsAll()));
      final var message =
        (CAResponseItemList) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(
        new CAItems(Set.of(itemWithRemoved)),
        message.data());
    }
  }

  private void sendCommandAssumingSuccess(
    final CACommandType msg)
    throws Exception
  {
    final var response =
      this.send(URI_COMMAND, msg);
    final var message =
      (CAResponseType) this.parse(response);

    assertEquals(200, response.statusCode());
  }

  private CAMessageType parse(
    final HttpResponse<InputStream> response)
    throws ParseException
  {
    try {
      final var data = new ByteArrayOutputStream();
      try (var bodyStream = response.body()) {
        bodyStream.transferTo(data);
      }

      final var tmpData =
        this.directory.resolve(UUID.randomUUID() + ".txt");

      LOG.debug("serializing server response to {}", tmpData);
      Files.write(tmpData, data.toByteArray());

      try (var inputStream = new ByteArrayInputStream(data.toByteArray())) {
        return this.parsers.parse(INPUT, inputStream);
      }
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private HttpResponse<InputStream> send(
    final URI target,
    final CAMessageType message)
    throws Exception
  {
    return this.client.send(
      this.request(target, message),
      HttpResponse.BodyHandlers.ofInputStream()
    );
  }

  private HttpRequest request(
    final URI target,
    final CAMessageType message)
  {
    return HttpRequest.newBuilder(target)
      .POST(this.serializedMessage(message))
      .build();
  }

  private HttpRequest.BodyPublisher serializedMessage(
    final CAMessageType message)
  {
    try (var output = new ByteArrayOutputStream()) {
      this.serializers.serialize(INPUT, output, message);
      return HttpRequest.BodyPublishers.ofByteArray(output.toByteArray());
    } catch (final IOException | SerializeException e) {
      throw new IllegalStateException(e);
    }
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
