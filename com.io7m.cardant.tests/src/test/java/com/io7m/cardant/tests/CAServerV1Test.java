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
import com.io7m.cardant.model.CAByteArray;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemAttachment;
import com.io7m.cardant.model.CAItemAttachmentID;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CAItemMetadatas;
import com.io7m.cardant.model.CAItemRepositAdd;
import com.io7m.cardant.model.CAItems;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocations;
import com.io7m.cardant.model.CAModelDatabaseQueriesType;
import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CATagID;
import com.io7m.cardant.model.CATags;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.model.CAUsers;
import com.io7m.cardant.protocol.inventory.v1.CA1InventoryMessageParsers;
import com.io7m.cardant.protocol.inventory.v1.CA1InventoryMessageSerializers;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemAttachmentPut;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemAttachmentRemove;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemCreate;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemGet;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemList;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemMetadataPut;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemMetadataRemove;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemRemove;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemReposit;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemUpdate;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandLocationGet;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandLocationList;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandLocationPut;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandLoginUsernamePassword;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandTagList;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandTagsDelete;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandTagsPut;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1InventoryMessageType;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1InventoryTransaction;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1ResponseError;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1ResponseOK;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static java.nio.charset.StandardCharsets.UTF_8;
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
  private CA1InventoryMessageParsers parsers;
  private CookieManager cookies;
  private HttpClient client;
  private CA1InventoryMessageSerializers serializers;
  private CAServers servers;

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

    this.parsers =
      new CA1InventoryMessageParsers();
    this.serializers =
      new CA1InventoryMessageSerializers();

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
      (CA1ResponseError) this.parsers.parse(URI_LOGIN, response.body());
    assertEquals(401, message.status());
    assertEquals("Login failed", message.message());
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
        new CA1CommandLoginUsernamePassword("someone_else", "1234"));

    assertEquals(401, response.statusCode());

    final var message =
      (CA1ResponseError) this.parsers.parse(URI_LOGIN, response.body());
    assertEquals(401, message.status());
    assertEquals("Login failed", message.message());
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
        new CA1CommandLoginUsernamePassword("someone", "12345"));

    assertEquals(401, response.statusCode());

    final var message =
      (CA1ResponseError) this.parsers.parse(URI_LOGIN, response.body());
    assertEquals(401, message.status());
    assertEquals("Login failed", message.message());
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
        new CA1CommandLoginUsernamePassword("someone", "1234"));

    assertEquals(200, response.statusCode());

    final var message =
      (CA1ResponseOK) this.parsers.parse(URI_LOGIN, response.body());
    assertEquals(Optional.empty(), message.data());
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
          new CA1CommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var response =
      this.send(URI_COMMAND, new CA1ResponseError(200, "Hello", List.of()));
    final var message =
      (CA1ResponseError) this.parse(response);

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
          new CA1CommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var response =
      this.send(URI_COMMAND, new CA1CommandTagList());
    final var message =
      (CA1ResponseOK) this.parse(response);

    assertEquals(200, response.statusCode());
    assertEquals(
      Optional.of(new CATags(Collections.emptySortedSet())),
      message.data());
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
          new CA1CommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var response =
      this.send(URI_COMMAND, new CA1CommandItemList());
    final var message =
      (CA1ResponseOK) this.parse(response);

    assertEquals(200, response.statusCode());
    assertEquals(Optional.of(new CAItems(Set.of())), message.data());
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
          new CA1CommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var tagSet = new TreeSet<CATag>();
    tagSet.add(new CATag(CATagID.random(), "tag0"));
    tagSet.add(new CATag(CATagID.random(), "tag1"));
    tagSet.add(new CATag(CATagID.random(), "tag2"));
    final var tags = new CATags(tagSet);

    {
      final var response =
        this.send(URI_COMMAND, new CA1CommandTagsPut(tags));
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.empty(), message.data());
    }

    {
      final var response =
        this.send(URI_COMMAND, new CA1CommandTagList());
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.of(tags), message.data());
    }

    {
      final var response =
        this.send(URI_COMMAND, new CA1CommandTagsDelete(tags));
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.empty(), message.data());
    }

    {
      final var response =
        this.send(URI_COMMAND, new CA1CommandTagList());
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(
        Optional.of(new CATags(Collections.emptySortedSet())),
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
          new CA1CommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var item0 = CAItem.create();

    {
      final var response =
        this.send(
          URI_COMMAND,
          new CA1CommandItemCreate(
            item0.id(),
            item0.name()));
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.empty(), message.data());
    }

    {
      final var response =
        this.send(URI_COMMAND, new CA1CommandItemList());
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.of(new CAItems(Set.of(item0))), message.data());
    }

    {
      final var response =
        this.send(
          URI_COMMAND,
          new CA1CommandItemUpdate(
            item0.id(),
            "Item 0"));
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.empty(), message.data());
    }

    {
      final var item1 =
        new CAItem(
          item0.id(),
          "Item 0",
          0L,
          Collections.emptySortedMap(),
          Collections.emptySortedMap(),
          Collections.emptySortedSet()
        );

      final var response =
        this.send(URI_COMMAND, new CA1CommandItemList());
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.of(new CAItems(Set.of(item1))), message.data());
    }

    {
      final var response =
        this.send(
          URI_COMMAND,
          new CA1CommandItemRemove(item0.id()));
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.empty(), message.data());
    }

    {
      final var response =
        this.send(URI_COMMAND, new CA1CommandItemList());
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.of(new CAItems(Set.of())), message.data());
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
          new CA1CommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var item0 = CAItem.create();

    {
      final var response =
        this.send(
          URI_COMMAND,
          new CA1CommandItemCreate(
            item0.id(),
            item0.name()));
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.empty(), message.data());
    }

    {
      final var response =
        this.send(URI_COMMAND, new CA1CommandItemGet(item0.id()));
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.of(item0), message.data());
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
          new CA1CommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    {
      final var response =
        this.send(URI_COMMAND, new CA1CommandItemGet(CAItemID.random()));
      final var message =
        (CA1ResponseError) this.parse(response);

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
          new CA1CommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var item0 = CAItem.create();

    {
      final var response =
        this.send(
          URI_COMMAND,
          new CA1CommandItemCreate(
            item0.id(),
            item0.name()));
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.empty(), message.data());
    }

    {
      final var response =
        this.send(
          URI_COMMAND,
          new CA1CommandItemCreate(
            item0.id(),
            item0.name()));
      final var message =
        (CA1ResponseError) this.parse(response);

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
          new CA1CommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var item0 = CAItem.create();

    {
      final var response =
        this.send(
          URI_COMMAND,
          new CA1CommandItemUpdate(
            item0.id(),
            item0.name()));
      final var message =
        (CA1ResponseError) this.parse(response);

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
          new CA1CommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var item0 = CAItem.create();

    final var metadata = new TreeMap<String, CAItemMetadata>();
    final var meta0 = new CAItemMetadata(item0.id(), "A", "0");
    metadata.put(meta0.name(), meta0);
    final var meta1 = new CAItemMetadata(item0.id(), "B", "1");
    metadata.put(meta1.name(), meta1);
    final var meta2 = new CAItemMetadata(item0.id(), "C", "2");
    metadata.put(meta2.name(), meta2);

    {
      final var response =
        this.send(
          URI_COMMAND,
          new CA1CommandItemMetadataPut(
            item0.id(),
            new CAItemMetadatas(metadata)));
      final var message =
        (CA1ResponseError) this.parse(response);

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
          new CA1CommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var item0 = CAItem.create();

    final var metadata = new TreeMap<String, CAItemMetadata>();
    final var meta0 = new CAItemMetadata(item0.id(), "A", "0");
    metadata.put(meta0.name(), meta0);
    final var meta1 = new CAItemMetadata(item0.id(), "B", "1");
    metadata.put(meta1.name(), meta1);
    final var meta2 = new CAItemMetadata(item0.id(), "C", "2");
    metadata.put(meta2.name(), meta2);

    {
      final var response =
        this.send(
          URI_COMMAND,
          new CA1CommandItemMetadataRemove(
            item0.id(),
            new CAItemMetadatas(metadata)));
      final var message =
        (CA1ResponseError) this.parse(response);

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
          new CA1CommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var item0 = CAItem.create();
    final var item1 = CAItem.create();
    final var item2 = CAItem.create();

    {
      final var transaction =
        new CA1InventoryTransaction(
          List.of(
            new CA1CommandItemCreate(item0.id(), item0.name()),
            new CA1CommandItemCreate(item1.id(), item1.name()),
            new CA1CommandItemCreate(item2.id(), item2.name()),
            new CA1CommandItemList()
          )
        );

      final var response =
        this.send(URI_COMMAND, transaction);
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(
        Optional.of(new CAItems(Set.of(item0, item1, item2))),
        message.data()
      );
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
          new CA1CommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    {
      final var response =
        this.send(URI_COMMAND, new CA1CommandItemList());
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.of(new CAItems(Set.of())), message.data());
    }

    final var item0 = CAItem.create();

    {
      final var transaction =
        new CA1InventoryTransaction(
          List.of(
            new CA1CommandItemCreate(item0.id(), item0.name()),
            new CA1CommandItemCreate(item0.id(), item0.name())
          )
        );

      final var response =
        this.send(URI_COMMAND, transaction);
      final var message =
        (CA1ResponseError) this.parse(response);

      assertEquals(500, response.statusCode());
    }

    {
      final var response =
        this.send(URI_COMMAND, new CA1CommandItemList());
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.of(new CAItems(Set.of())), message.data());
    }
  }

  /**
   * Adding, updating, and removing attachments works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemsCreateAttachments()
    throws Exception
  {
    this.createUser("someone", "1234");

    {
      final var response =
        this.send(
          URI_LOGIN,
          new CA1CommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var item0 = CAItem.create();

    {
      final var response =
        this.send(
          URI_COMMAND,
          new CA1CommandItemCreate(
            item0.id(),
            item0.name()));
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.empty(), message.data());
    }

    final var itemAttachment =
      new CAItemAttachment(
        CAItemAttachmentID.random(),
        item0.id(),
        "An attachment.",
        "text/plain",
        "Datasheet",
        10L,
        "SHA-256",
        "fbf8a33d8c8a1dccd71642c39b2f4b6622d93fccb4d73de2924c355603db9043",
        Optional.of(new CAByteArray("ABCD123456".getBytes(UTF_8)))
      );

    {
      final var response =
        this.send(URI_COMMAND, new CA1CommandItemAttachmentPut(itemAttachment));

      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.empty(), message.data());
    }

    final var attachmentWithout = itemAttachment.withoutData();
    final var attachments = new TreeMap<CAItemAttachmentID, CAItemAttachment>();
    attachments.put(itemAttachment.id(), attachmentWithout);

    final var itemWith =
      new CAItem(
        item0.id(),
        item0.name(),
        item0.count(),
        Collections.emptySortedMap(),
        attachments,
        Collections.emptySortedSet()
      );

    {
      final var response =
        this.send(URI_COMMAND, new CA1CommandItemList());
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.of(new CAItems(Set.of(itemWith))), message.data());
    }

    {
      final var response =
        this.send(
          URI_COMMAND,
          new CA1CommandItemAttachmentRemove(itemAttachment.id()));
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
    }

    {
      final var response =
        this.send(URI_COMMAND, new CA1CommandItemList());
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.of(new CAItems(Set.of(item0))), message.data());
    }
  }

  /**
   * Exceeding the attachment size limit produces an error.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemsCreateAttachmentTooLarge()
    throws Exception
  {
    this.createUser("someone", "1234");

    {
      final var response =
        this.send(
          URI_LOGIN,
          new CA1CommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var item0 = CAItem.create();

    {
      final var response =
        this.send(
          URI_COMMAND,
          new CA1CommandItemCreate(
            item0.id(),
            item0.name()));
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.empty(), message.data());
    }

    final var itemAttachment =
      new CAItemAttachment(
        CAItemAttachmentID.random(),
        item0.id(),
        "An attachment.",
        "text/plain",
        "Datasheet",
        256,
        "SHA-256",
        "5341e6b2646979a70e57653007a1f310169421ec9bdd9f1a5648f75ade005af1",
        Optional.of(new CAByteArray(new byte[256]))
      );

    {
      final var response =
        this.send(URI_COMMAND, new CA1CommandItemAttachmentPut(itemAttachment));

      final var message =
        (CA1ResponseError) this.parse(response);

      assertEquals(400, response.statusCode());
    }

    {
      final var response =
        this.send(URI_COMMAND, new CA1CommandItemList());
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.of(new CAItems(Set.of(item0))), message.data());
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
          new CA1CommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var item0 = CAItem.create();

    {
      final var response =
        this.send(
          URI_COMMAND,
          new CA1CommandItemCreate(
            item0.id(),
            item0.name()));
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.empty(), message.data());
    }

    final var metadata = new TreeMap<String, CAItemMetadata>();
    final var meta0 = new CAItemMetadata(item0.id(), "A", "0");
    metadata.put(meta0.name(), meta0);
    final var meta1 = new CAItemMetadata(item0.id(), "B", "1");
    metadata.put(meta1.name(), meta1);
    final var meta2 = new CAItemMetadata(item0.id(), "C", "2");
    metadata.put(meta2.name(), meta2);

    {
      final var response =
        this.send(
          URI_COMMAND,
          new CA1CommandItemMetadataPut(
            item0.id(),
            new CAItemMetadatas(metadata)));
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
    }

    final var itemWith =
      new CAItem(
        item0.id(),
        item0.name(),
        item0.count(),
        metadata,
        item0.attachments(),
        item0.tags()
      );

    {
      final var response =
        this.send(URI_COMMAND, new CA1CommandItemList());
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.of(new CAItems(Set.of(itemWith))), message.data());
    }

    final var metadataRemove = new TreeMap<String, CAItemMetadata>();
    metadataRemove.put(meta1.name(), meta1);
    metadataRemove.put(meta2.name(), meta2);

    {
      final var response =
        this.send(
          URI_COMMAND,
          new CA1CommandItemMetadataRemove(
            item0.id(),
            new CAItemMetadatas(metadataRemove)));
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
    }

    final var metadataRemoved = new TreeMap<String, CAItemMetadata>();
    metadataRemoved.put(meta0.name(), meta0);

    final var itemWithRemoved =
      new CAItem(
        item0.id(),
        item0.name(),
        item0.count(),
        metadataRemoved,
        item0.attachments(),
        item0.tags()
      );

    {
      final var response =
        this.send(URI_COMMAND, new CA1CommandItemList());
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(
        Optional.of(new CAItems(Set.of(itemWithRemoved))),
        message.data());
    }
  }

  /**
   * Adding and retrieving locations works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testLocationsCreateList()
    throws Exception
  {
    this.createUser("someone", "1234");

    {
      final var response =
        this.send(
          URI_LOGIN,
          new CA1CommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var location = new CALocation(
      CALocationID.random(),
      "location",
      "location description"
    );

    final var locationsMap = new TreeMap<CALocationID, CALocation>();
    locationsMap.put(location.id(), location);
    final var locations = new CALocations(locationsMap);

    {
      final var response =
        this.send(URI_COMMAND, new CA1CommandLocationPut(location));
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.empty(), message.data());
    }

    {
      final var response =
        this.send(URI_COMMAND, new CA1CommandLocationList());
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.of(locations), message.data());
    }
  }

  /**
   * Item repositing works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemReposit()
    throws Exception
  {
    this.createUser("someone", "1234");

    {
      final var response =
        this.send(
          URI_LOGIN,
          new CA1CommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var location =
      new CALocation(
        CALocationID.random(),
        "location",
        "location description"
      );

    {
      final var response =
        this.send(URI_COMMAND, new CA1CommandLocationPut(location));
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.empty(), message.data());
    }

    final var itemId = CAItemID.random();

    {
      final var response =
        this.send(URI_COMMAND, new CA1CommandItemCreate(itemId, "Item"));
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.empty(), message.data());
    }

    {
      final var response =
        this.send(
          URI_COMMAND,
          new CA1CommandItemReposit(
            new CAItemRepositAdd(itemId, location.id(), 1000L)));
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.empty(), message.data());
    }

    {
      final var response =
        this.send(URI_COMMAND, new CA1CommandItemList());
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());

      final var items = (CAItems) message.data().orElseThrow();
      final var item = items.items().iterator().next();
      assertEquals(1000L, item.count());
    }
  }


  /**
   * Adding and retrieving locations works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testLocationsCreateGet()
    throws Exception
  {
    this.createUser("someone", "1234");

    {
      final var response =
        this.send(
          URI_LOGIN,
          new CA1CommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    final var location0 =
      new CALocation(
        CALocationID.random(),
        "location",
        "location description");

    {
      final var response =
        this.send(
          URI_COMMAND,
          new CA1CommandLocationPut(location0));
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.empty(), message.data());
    }

    {
      final var response =
        this.send(URI_COMMAND, new CA1CommandLocationGet(location0.id()));
      final var message =
        (CA1ResponseOK) this.parse(response);

      assertEquals(200, response.statusCode());
      assertEquals(Optional.of(location0), message.data());
    }
  }

  /**
   * Retrieving nonexistent locations fails.
   *
   * @throws Exception On errors
   */

  @Test
  public void testLocationsGetNonexistent()
    throws Exception
  {
    this.createUser("someone", "1234");

    {
      final var response =
        this.send(
          URI_LOGIN,
          new CA1CommandLoginUsernamePassword("someone", "1234"));
      assertEquals(200, response.statusCode());
    }

    {
      final var response =
        this.send(
          URI_COMMAND,
          new CA1CommandLocationGet(CALocationID.random()));
      final var message =
        (CA1ResponseError) this.parse(response);

      assertEquals(404, response.statusCode());
    }
  }

  private CA1InventoryMessageType parse(
    final HttpResponse<InputStream> response)
    throws ParseException
  {
    return this.parsers.parse(INPUT, response.body());
  }

  private HttpResponse<InputStream> send(
    final URI target,
    final CA1InventoryMessageType message)
    throws Exception
  {
    return this.client.send(
      this.request(target, message),
      HttpResponse.BodyHandlers.ofInputStream()
    );
  }

  private HttpRequest request(
    final URI target,
    final CA1InventoryMessageType message)
  {
    return HttpRequest.newBuilder(target)
      .POST(this.serializedMessage(message))
      .build();
  }

  private HttpRequest.BodyPublisher serializedMessage(
    final CA1InventoryMessageType message)
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
