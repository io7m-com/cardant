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

import com.io7m.cardant.database.api.CADatabaseEvent;
import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseParameters;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.database.derby.CADatabasesDerby;
import com.io7m.cardant.model.CAByteArray;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemAttachment;
import com.io7m.cardant.model.CAItemAttachmentID;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CAModelCADatabaseQueriesType;
import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.model.CAUserID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import static com.io7m.cardant.database.api.CADatabaseErrorCode.ERROR_DUPLICATE;
import static com.io7m.cardant.database.api.CADatabaseErrorCode.ERROR_NONEXISTENT;
import static com.io7m.cardant.database.api.CADatabaseErrorCode.ERROR_PARAMETERS_INVALID;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class CADerbyDatabaseTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CADerbyDatabaseTest.class);

  private Path directory;
  private ArrayList<CADatabaseEvent> events;
  private Path databaseDirectory;

  private static <T> SortedSet<T> setOf(
    final T... items)
  {
    return new TreeSet<T>(List.of(items));
  }

  @BeforeEach
  public void setup()
    throws IOException
  {
    this.directory =
      CATestDirectories.createTempDirectory();
    this.databaseDirectory =
      this.directory.resolve("database");

    this.events = new ArrayList<CADatabaseEvent>();
  }

  @AfterEach
  public void tearDown()
    throws IOException
  {
    LOG.debug("deleting {}", this.directory);
    CATestDirectories.deleteDirectory(this.directory);
  }

  private void logEvent(
    final CADatabaseEvent event)
  {
    LOG.debug("event: {}", event);
    this.events.add(event);
  }

  private void withDatabase(
    final WithDatabaseType withDatabase)
    throws Exception
  {
    final var databases = new CADatabasesDerby(Locale.getDefault());

    final var parameters =
      CADatabaseParameters.builder()
        .setCreate(true)
        .setPath(this.databaseDirectory.toAbsolutePath().toString())
        .build();

    try (var database = databases.open(parameters, this::logEvent)) {
      try (var connection = database.openConnection()) {
        try (var transaction = connection.beginTransaction()) {
          withDatabase.call(
            transaction,
            transaction.queries(CAModelCADatabaseQueriesType.class)
          );
        }
      }
    }
  }

  @Test
  public void testTagPutGet0()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var id = UUID.randomUUID();

      final var tag0 =
        new CATag(
          UUID.randomUUID(),
          "TAG0");

      queries.tagPut(tag0);

      {
        final var retrieved = queries.tagGet(tag0.id()).orElseThrow();
        assertEquals(tag0, retrieved);
      }

      final var tag0Renamed = new CATag(tag0.id(), "TAG0_CHANGED");
      queries.tagPut(tag0Renamed);

      {
        final var retrieved = queries.tagGet(tag0.id()).orElseThrow();
        assertEquals(tag0Renamed, retrieved);
      }
    });
  }

  @Test
  public void testTagPutDuplicate0()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var tag0 =
        new CATag(
          UUID.randomUUID(),
          "TAG0");

      final var tag1 =
        new CATag(
          UUID.randomUUID(),
          "TAG0");

      queries.tagPut(tag0);

      final var ex =
        assertThrows(
          CADatabaseException.class,
          () -> queries.tagPut(tag1)
        );
      assertTrue(ex.getMessage().contains(tag0.name()));
    });
  }

  @Test
  public void testTagList()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var tagsPut = new TreeSet<CATag>();
      tagsPut.addAll(List.of(
        new CATag(
          UUID.randomUUID(),
          "TAG0"),
        new CATag(
          UUID.randomUUID(),
          "TAG1"),
        new CATag(
          UUID.randomUUID(),
          "TAG2"),
        new CATag(
          UUID.randomUUID(),
          "TAG3"),
        new CATag(
          UUID.randomUUID(),
          "TAG4")
      ));

      for (final var tag : tagsPut) {
        queries.tagPut(tag);
      }

      final var tagsGet = queries.tagList();
      assertEquals(tagsPut, tagsGet);
    });
  }

  @Test
  public void testItemSetGet0()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var id = CAItemID.random();

      final var item0 =
        new CAItem(
          id,
          "",
          0L,
          new TreeMap<>(),
          new TreeMap<>(),
          Collections.emptySortedSet());

      queries.itemCreate(id);

      {
        final var retrieved = queries.itemGet(id).orElseThrow();
        assertEquals(item0, retrieved);
      }

      final var item0p0 =
        new CAItem(
          id,
          "ITEM0",
          0L,
          new TreeMap<>(),
          new TreeMap<>(),
          Collections.emptySortedSet());

      queries.itemNameSet(id, "ITEM0");

      {
        final var retrieved = queries.itemGet(id).orElseThrow();
        assertEquals(item0p0, retrieved);
      }

      final var item0p1 =
        new CAItem(
          id,
          "ITEM0",
          23L,
          new TreeMap<>(),
          new TreeMap<>(),
          Collections.emptySortedSet());

      queries.itemCountSet(id, 23L);

      {
        final var retrieved = queries.itemGet(id).orElseThrow();
        assertEquals(item0p1, retrieved);
      }
    });
  }

  @Test
  public void testItemSetCountNegative0()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var id = CAItemID.random();

      queries.itemCreate(id);

      final var ex = assertThrows(CADatabaseException.class, () -> {
        queries.itemCountSet(id, -1L);
      });
      assertEquals(ERROR_PARAMETERS_INVALID, ex.errorCode());
    });
  }

  @Test
  public void testItemSetCountNonexistent0()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var id = CAItemID.random();

      final var ex = assertThrows(CADatabaseException.class, () -> {
        queries.itemCountSet(id, 0L);
      });
      assertEquals(ERROR_NONEXISTENT, ex.errorCode());
    });
  }

  @Test
  public void testItemSetNameNonexistent0()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var id = CAItemID.random();

      final var ex = assertThrows(CADatabaseException.class, () -> {
        queries.itemNameSet(id, "HELLO");
      });
      assertEquals(ERROR_NONEXISTENT, ex.errorCode());
    });
  }

  @Test
  public void testItemCreateDuplicate0()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var id = CAItemID.random();
      queries.itemCreate(id);

      final var ex = assertThrows(CADatabaseException.class, () -> {
        queries.itemCreate(id);
      });
      assertEquals(ERROR_DUPLICATE, ex.errorCode());
    });
  }

  @Test
  public void testItemList()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var itemsPut = new HashSet<CAItemID>();
      itemsPut.add(CAItemID.random());
      itemsPut.add(CAItemID.random());
      itemsPut.add(CAItemID.random());
      itemsPut.add(CAItemID.random());
      itemsPut.add(CAItemID.random());

      for (final var item : itemsPut) {
        queries.itemCreate(item);
      }

      final var itemsGet = queries.itemList();
      assertEquals(itemsPut, itemsGet);
    });
  }

  @Test
  public void testItemTags()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var item0 = CAItemID.random();
      final var item1 = CAItemID.random();

      final var tag0 =
        new CATag(
          UUID.randomUUID(),
          "TAG0");

      final var tag1 =
        new CATag(
          UUID.randomUUID(),
          "TAG1");

      final var tag2 =
        new CATag(
          UUID.randomUUID(),
          "TAG2");

      queries.itemCreate(item0);
      queries.itemCreate(item1);
      queries.tagPut(tag0);
      queries.tagPut(tag1);
      queries.tagPut(tag2);

      queries.itemTagAdd(item0, tag0);
      queries.itemTagAdd(item1, tag1);
      queries.itemTagAdd(item1, tag2);

      assertEquals(setOf(tag0), queries.itemTagList(item0));
      assertEquals(setOf(tag1, tag2), queries.itemTagList(item1));

      queries.itemTagRemove(item0, tag0);
      assertEquals(setOf(), queries.itemTagList(item0));
      assertEquals(setOf(tag1, tag2), queries.itemTagList(item1));

      queries.itemTagRemove(item1, tag2);
      assertEquals(setOf(), queries.itemTagList(item0));
      assertEquals(setOf(tag1), queries.itemTagList(item1));

      queries.itemTagRemove(item1, tag1);
      assertEquals(setOf(), queries.itemTagList(item0));
      assertEquals(setOf(), queries.itemTagList(item1));
    });
  }

  @Test
  public void testItemTagsIntegrity0()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var item0 = CAItemID.random();

      final var tag0 =
        new CATag(
          UUID.randomUUID(),
          "TAG0");

      final var with = new TreeSet<CATag>();
      with.add(tag0);

      assertEquals(Collections.emptySortedSet(), queries.tagList());
      queries.tagPut(tag0);
      assertEquals(with, queries.tagList());

      queries.itemCreate(item0);
      queries.itemTagAdd(item0, tag0);
      assertEquals(with, queries.itemTagList(item0));

      queries.tagDelete(tag0);
      assertEquals(Collections.emptySortedSet(), queries.itemTagList(item0));
    });
  }

  @Test
  public void testItemIntegrity0()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var item0 = CAItemID.random();

      final var tag0 =
        new CATag(
          UUID.randomUUID(),
          "TAG0");

      assertEquals(setOf(), queries.tagList());
      queries.tagPut(tag0);
      assertEquals(setOf(tag0), queries.tagList());

      queries.itemCreate(item0);
      queries.itemTagAdd(item0, tag0);
      assertEquals(setOf(tag0), queries.itemTagList(item0));

      queries.itemDelete(item0);
    });
  }

  @Test
  public void testItemTagNonexistent0()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var item0 = CAItemID.random();

      final var tag0 =
        new CATag(
          UUID.randomUUID(),
          "TAG0");

      queries.itemCreate(item0);

      final var ex =
        assertThrows(
          CADatabaseException.class,
          () -> queries.itemTagAdd(item0, tag0)
        );
      assertTrue(ex.getMessage().contains(tag0.id().toString()));
      assertEquals(ERROR_NONEXISTENT, ex.errorCode());
    });
  }

  @Test
  public void testItemTagNonexistent1()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var item0 = CAItemID.random();

      final var tag0 =
        new CATag(
          UUID.randomUUID(),
          "TAG0");

      queries.tagPut(tag0);

      final var ex =
        assertThrows(
          CADatabaseException.class,
          () -> queries.itemTagAdd(item0, tag0)
        );
      assertTrue(ex.getMessage().contains(item0.id().toString()));
      assertEquals(ERROR_NONEXISTENT, ex.errorCode());
    });
  }

  @Test
  public void testItemMetadata0()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var item0 = CAItemID.random();

      queries.itemCreate(item0);

      final var meta0 =
        new CAItemMetadata(item0, "Type", "Vegetable");
      final var meta1 =
        new CAItemMetadata(item0, "Colour", "Red");

      queries.itemMetadataPut(meta0);
      queries.itemMetadataPut(meta1);

      {
        final var metas = queries.itemMetadata(item0);
        assertEquals(2, metas.size());
        assertEquals(meta0, metas.get("Type"));
        assertEquals(meta1, metas.get("Colour"));
      }

      queries.itemMetadataRemove(meta0);

      {
        final var metas = queries.itemMetadata(item0);
        assertEquals(1, metas.size());
        assertEquals(meta1, metas.get("Colour"));
      }

      final var meta1p = new CAItemMetadata(
        meta1.itemId(),
        meta1.name(),
        "Blue");
      queries.itemMetadataPut(meta1p);

      {
        final var metas = queries.itemMetadata(item0);
        assertEquals(1, metas.size());
        assertEquals(meta1p, metas.get("Colour"));
      }
    });
  }

  @Test
  public void testItemMetadataIntegrity0()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var item0 = CAItemID.random();

      queries.itemCreate(item0);

      final var meta0 =
        new CAItemMetadata(item0, "Type", "Vegetable");

      queries.itemMetadataPut(meta0);
      queries.itemDelete(item0);
    });
  }

  @Test
  public void testItemAttachment0()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var item0 = CAItemID.random();

      queries.itemCreate(item0);

      final var attachmentID =
        CAItemAttachmentID.random();

      final var attachment0 =
        new CAItemAttachment(
          attachmentID,
          item0,
          "text/plain",
          "nothing",
          5L,
          "SHA-256",
          "3733cd977ff8eb18b987357e22ced99f46097f31ecb239e878ae63760e83e4d5",
          Optional.of(new CAByteArray("HELLO".getBytes(UTF_8)))
        );

      queries.itemAttachmentPut(attachment0);

      {
        final var attachments = queries.itemAttachments(item0, true);
        assertEquals(1, attachments.size());
        assertEquals(attachment0, attachments.get(attachment0.id()));
      }

      final var attachment0p =
        new CAItemAttachment(
          attachmentID,
          item0,
          "text/plain",
          "nothing",
          7L,
          "SHA-256",
          "aa838baa048b2ca558d671b30f8c0a2ef7eeec70502a6fffccaefa837da07661",
          Optional.of(new CAByteArray("GOODBYE".getBytes(UTF_8)))
        );

      queries.itemAttachmentPut(attachment0p);

      {
        final var attachments = queries.itemAttachments(item0, true);
        assertEquals(1, attachments.size());
        assertEquals(attachment0p, attachments.get(attachment0.id()));
      }

      final var attachment0WithoutData =
        new CAItemAttachment(
          attachmentID,
          item0,
          "text/plain",
          "nothing",
          7L,
          "SHA-256",
          "aa838baa048b2ca558d671b30f8c0a2ef7eeec70502a6fffccaefa837da07661",
          Optional.empty()
        );

      {
        final var attachments = queries.itemAttachments(item0, false);
        assertEquals(1, attachments.size());
        assertEquals(
          attachment0WithoutData,
          attachments.get(attachment0.id()));
      }

      {
        final var attachment =
          queries.itemAttachmentGet(item0, attachment0p.id(), true)
            .orElseThrow();
        assertEquals(attachment0p, attachment);
      }

      {
        final var attachment =
          queries.itemAttachmentGet(item0, attachment0p.id(), false)
            .orElseThrow();
        assertEquals(attachment0WithoutData, attachment);
      }
    });
  }

  @Test
  public void testItemAttachmentIntegrity0()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var item0 = CAItemID.random();

      queries.itemCreate(item0);

      final var attachment0 =
        new CAItemAttachment(
          CAItemAttachmentID.random(),
          item0,
          "text/plain",
          "nothing",
          5L,
          "SHA-256",
          "3733cd977ff8eb18b987357e22ced99f46097f31ecb239e878ae63760e83e4d5",
          Optional.of(new CAByteArray("HELLO".getBytes(UTF_8)))
        );

      queries.itemAttachmentPut(attachment0);
      queries.itemDelete(item0);
    });
  }

  @Test
  public void testItemAttachmentPutWithoutData0()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var item0 = CAItemID.random();

      queries.itemCreate(item0);

      {
        final var attachment0 =
          new CAItemAttachment(
            CAItemAttachmentID.random(),
            item0,
            "text/plain",
            "nothing",
            5L,
            "SHA-256",
            "3733cd977ff8eb18b987357e22ced99f46097f31ecb239e878ae63760e83e4d5",
            Optional.empty()
          );

        assertThrows(CADatabaseException.class, () -> {
          queries.itemAttachmentPut(attachment0);
        });
      }

      {
        final var attachment0 =
          new CAItemAttachment(
            CAItemAttachmentID.random(),
            item0,
            "text/plain",
            "nothing",
            5L,
            "SHA-256",
            "3733cd977ff8eb18b987357e22ced99f46097f31ecb239e878ae63760e83e4d5",
            Optional.of(new CAByteArray("HELLO".getBytes(UTF_8)))
          );

        queries.itemAttachmentPut(attachment0);
      }

      {
        final var attachment0 =
          new CAItemAttachment(
            CAItemAttachmentID.random(),
            item0,
            "text/plain",
            "nothing",
            5L,
            "SHA-256",
            "3733cd977ff8eb18b987357e22ced99f46097f31ecb239e878ae63760e83e4d5",
            Optional.empty()
          );

        assertThrows(CADatabaseException.class, () -> {
          queries.itemAttachmentPut(attachment0);
        });
      }
    });
  }

  @Test
  public void testUserNonexistent()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      assertEquals(
        Optional.empty(),
        queries.userGet(CAUserID.random())
      );
    });
  }

  @Test
  public void testUserNone()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      assertEquals(
        Map.of(),
        queries.userList()
      );
    });
  }

  @Test
  public void testUserPutGet()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var user = new CAUser(
        CAUserID.random(),
        "user",
        "password",
        "salt",
        "algo"
      );

      queries.userPut(user);
      assertEquals(Optional.of(user), queries.userGet(user.id()));
      assertEquals(Optional.of(user), queries.userGetByName("user"));
      assertEquals(Optional.empty(), queries.userGetByName("user1"));

      final var userAlt = new CAUser(
        user.id(),
        "user1",
        "password1",
        "salt1",
        "algo1"
      );

      queries.userPut(userAlt);
      assertEquals(Optional.of(userAlt), queries.userGet(user.id()));
      assertEquals(Optional.of(userAlt), queries.userGetByName("user1"));
      assertEquals(
        List.of(userAlt),
        List.copyOf(queries.userList().values())
      );
    });
  }

  @Test
  public void testOpenTwice()
    throws Exception
  {
    final var tag =
      new CATag(
        UUID.randomUUID(),
        "TAG");

    this.withDatabase((transaction, queries) -> {
      queries.tagPut(tag);
      transaction.commit();
    });
    this.withDatabase((transaction, queries) -> {
      final var rtag = queries.tagGet(tag.id()).orElseThrow();
      assertEquals(tag, rtag);
    });
  }

  interface WithDatabaseType
  {
    void call(
      CADatabaseTransactionType transaction,
      CAModelCADatabaseQueriesType queries)
      throws Exception;
  }
}
