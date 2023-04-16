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

import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.model.CAByteArray;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.model.CAIds;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemLocation;
import com.io7m.cardant.model.CAItemLocations;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CAItemRepositAdd;
import com.io7m.cardant.model.CAItemRepositMove;
import com.io7m.cardant.model.CAItemRepositRemove;
import com.io7m.cardant.model.CAItems;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocations;
import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CATagID;
import com.io7m.cardant.model.CATags;
import com.io7m.cardant.protocol.inventory.CAICommandFilePut;
import com.io7m.cardant.protocol.inventory.CAICommandFileRemove;
import com.io7m.cardant.protocol.inventory.CAICommandItemCreate;
import com.io7m.cardant.protocol.inventory.CAICommandItemGet;
import com.io7m.cardant.protocol.inventory.CAICommandItemList;
import com.io7m.cardant.protocol.inventory.CAICommandItemLocationsList;
import com.io7m.cardant.protocol.inventory.CAICommandItemMetadataPut;
import com.io7m.cardant.protocol.inventory.CAICommandItemMetadataRemove;
import com.io7m.cardant.protocol.inventory.CAICommandItemReposit;
import com.io7m.cardant.protocol.inventory.CAICommandItemsRemove;
import com.io7m.cardant.protocol.inventory.CAICommandLocationList;
import com.io7m.cardant.protocol.inventory.CAICommandLocationPut;
import com.io7m.cardant.protocol.inventory.CAICommandLogin;
import com.io7m.cardant.protocol.inventory.CAICommandTagList;
import com.io7m.cardant.protocol.inventory.CAICommandTagsDelete;
import com.io7m.cardant.protocol.inventory.CAICommandTagsPut;
import com.io7m.cardant.protocol.inventory.CAIEventType;
import com.io7m.cardant.protocol.inventory.CAIMessageType;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseFilePut;
import com.io7m.cardant.protocol.inventory.CAIResponseFileRemove;
import com.io7m.cardant.protocol.inventory.CAIResponseItemAttachmentRemove;
import com.io7m.cardant.protocol.inventory.CAIResponseItemCreate;
import com.io7m.cardant.protocol.inventory.CAIResponseItemGet;
import com.io7m.cardant.protocol.inventory.CAIResponseItemList;
import com.io7m.cardant.protocol.inventory.CAIResponseItemLocationsList;
import com.io7m.cardant.protocol.inventory.CAIResponseItemMetadataPut;
import com.io7m.cardant.protocol.inventory.CAIResponseItemMetadataRemove;
import com.io7m.cardant.protocol.inventory.CAIResponseItemsRemove;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationList;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationPut;
import com.io7m.cardant.protocol.inventory.CAIResponseLogin;
import com.io7m.cardant.protocol.inventory.CAIResponseTagList;
import com.io7m.cardant.protocol.inventory.CAIResponseTagsDelete;
import com.io7m.cardant.protocol.inventory.CAIResponseTagsPut;
import com.io7m.cardant.protocol.inventory.CAITransaction;
import com.io7m.cardant.protocol.inventory.CAITransactionResponse;
import com.io7m.cardant.protocol.inventory.cb.CAI1Messages;
import com.io7m.idstore.model.IdName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import static com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationExact;
import static com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationWithDescendants;
import static com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationsAll;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class CAMessageServicesV1Test
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAMessageServicesV1Test.class);

  public static final CAFileType.CAFileWithData FILE_0 =
    new CAFileType.CAFileWithData(
      CAFileID.random(),
      "description",
      "text/plain",
      5L,
      "SHA-256",
      "3733cd977ff8eb18b987357e22ced99f46097f31ecb239e878ae63760e83e4d5",
      new CAByteArray("HELLO".getBytes(UTF_8))
    );

  private static final URI SOURCE = URI.create("urn:loop");
  private static final CAItem ITEM_0 = makeItem(0);
  private static final CAItem ITEM_1 = makeItem(1);
  private static final CAItem ITEM_2 = makeItem(2);
  private CAI1Messages messages;

  private static CAItem makeItem(
    final int index)
  {
    return new CAItem(
      CAItemID.random(),
      "Item " + index,
      200L,
      300L,
      new TreeMap<>(
        Map.ofEntries(
          Map.entry("M0", new CAItemMetadata("M0", "V0")),
          Map.entry("M1", new CAItemMetadata("M1", "V1")),
          Map.entry("M2", new CAItemMetadata("M2", "V2"))
        )
      ),
      new TreeMap<>(
      ),
      new TreeSet<>(
        Set.of(
          new CATag(CATagID.random(), "Tag0"),
          new CATag(CATagID.random(), "Tag1"),
          new CATag(CATagID.random(), "Tag2")
        )
      )
    );
  }

  @BeforeEach
  public void setup()
  {
    this.messages = new CAI1Messages();
  }

  @Test
  public void testCommandFilePut()
    throws Exception
  {
    final var message =
      new CAICommandFilePut(FILE_0);
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandFileRemove()
    throws Exception
  {
    final var message =
      new CAICommandFileRemove(FILE_0.id());
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandLoginUsernamePassword()
    throws Exception
  {
    final var message =
      new CAICommandLogin(new IdName("user"), "password", Map.of());

    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandItemList0()
    throws Exception
  {
    final var message =
      new CAICommandItemList(new CAListLocationsAll());
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandItemList1()
    throws Exception
  {
    final var message =
      new CAICommandItemList(
        new CAListLocationExact(CALocationID.random()));
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandItemList2()
    throws Exception
  {
    final var message =
      new CAICommandItemList(
        new CAListLocationWithDescendants(CALocationID.random()));
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandItemCreate()
    throws Exception
  {
    final var message =
      new CAICommandItemCreate(CAItemID.random(), "Item 0");
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandItemGet()
    throws Exception
  {
    final var message =
      new CAICommandItemGet(CAItemID.random());
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandItemRemove()
    throws Exception
  {
    final var message =
      new CAICommandItemsRemove(
        Set.of(
          CAItemID.random(),
          CAItemID.random(),
          CAItemID.random())
      );
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandItemMetadataRemove()
    throws Exception
  {
    final var message =
      new CAICommandItemMetadataRemove(
        CAItemID.random(),
        Set.of("Meta0", "Meta1", "Meta2"));
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandItemMetadataPut()
    throws Exception
  {
    final var message =
      new CAICommandItemMetadataPut(
        CAItemID.random(),
        Set.of(
          new CAItemMetadata("Meta0", "Value0"),
          new CAItemMetadata("Meta1", "Value1"),
          new CAItemMetadata("Meta2", "Value2")
        ));
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandTagsPut()
    throws Exception
  {
    final var message =
      new CAICommandTagsPut(new CATags(ITEM_0.tags()));
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandTagsRemove()
    throws Exception
  {
    final var message =
      new CAICommandTagsDelete(new CATags(ITEM_0.tags()));
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandTagList()
    throws Exception
  {
    final var message =
      new CAICommandTagList();
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandLocationPut0()
    throws Exception
  {
    final var message =
      new CAICommandLocationPut(
        new CALocation(
          CALocationID.random(),
          Optional.of(CALocationID.random()),
          "Name",
          "Description"
        )
      );
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandLocationPut1()
    throws Exception
  {
    final var message =
      new CAICommandLocationPut(
        new CALocation(
          CALocationID.random(),
          Optional.empty(),
          "Name",
          "Description"
        )
      );
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandLocationList()
    throws Exception
  {
    final var message =
      new CAICommandLocationList();
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandItemRepositAdd()
    throws Exception
  {
    final var message =
      new CAICommandItemReposit(new CAItemRepositAdd(
        CAItemID.random(),
        CALocationID.random(),
        0xffff_ffff_ffff_ffffL
      ));
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandItemRepositRemove()
    throws Exception
  {
    final var message =
      new CAICommandItemReposit(new CAItemRepositRemove(
        CAItemID.random(),
        CALocationID.random(),
        0xffff_ffff_ffff_ffffL
      ));
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandItemRepositMove()
    throws Exception
  {
    final var message =
      new CAICommandItemReposit(new CAItemRepositMove(
        CAItemID.random(),
        CALocationID.random(),
        CALocationID.random(),
        0xffff_ffff_ffff_ffffL
      ));
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandItemLocationsList()
    throws Exception
  {
    final var message =
      new CAICommandItemLocationsList(CAItemID.random());
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testTransaction()
    throws Exception
  {
    final var message =
      new CAITransaction(
        List.of(
          new CAICommandItemLocationsList(CAItemID.random()),
          new CAICommandLogin(
            new IdName("user"), "pass", Map.of()),
          new CAICommandItemList(
            new CAListLocationsAll()),
          new CAICommandItemCreate(
            CAItemID.random(), "Item"),
          new CAICommandItemGet(
            CAItemID.random()),
          new CAICommandItemsRemove(
            Set.of(
              CAItemID.random(),
              CAItemID.random(),
              CAItemID.random()
            )),
          new CAICommandItemMetadataPut(
            CAItemID.random(),
            new HashSet<>(ITEM_0.metadata().values())),
          new CAICommandItemMetadataRemove(
            CAItemID.random(),
            Set.of("Meta0")),
          new CAICommandTagList(),
          new CAICommandTagsDelete(
            new CATags(ITEM_0.tags())),
          new CAICommandTagsPut(
            new CATags(ITEM_0.tags())),
          new CAICommandLocationList(),
          new CAICommandLocationPut(
            new CALocation(CALocationID.random(), Optional.empty(), "N", "D")
          ),
          new CAICommandItemReposit(new CAItemRepositAdd(
            CAItemID.random(),
            CALocationID.random(),
            0xffff_ffff_ffff_ffffL
          )),
          new CAICommandItemReposit(new CAItemRepositRemove(
            CAItemID.random(),
            CALocationID.random(),
            0xffff_ffff_ffff_ffffL
          )),
          new CAICommandItemReposit(new CAItemRepositMove(
            CAItemID.random(),
            CALocationID.random(),
            CALocationID.random(),
            0xffff_ffff_ffff_ffffL
          ))
        ));
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testTransactionResponse0()
    throws Exception
  {
    final var message =
      new CAITransactionResponse(
        true,
        List.of(
          new CAIResponseItemsRemove(
            UUID.randomUUID(),
            new CAIds(Set.of(CAItemID.random()))),
          new CAIResponseError(
            UUID.randomUUID(),
            "Failed",
            CAStandardErrorCodes.errorIo(),
            Map.of(),
            Optional.empty())
        )
      );
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testTransactionResponse1()
    throws Exception
  {
    final var message =
      new CAITransactionResponse(
        false,
        List.of(
          new CAIResponseItemsRemove(UUID.randomUUID(),
                                     new CAIds(Set.of(CAItemID.random()))),
          new CAIResponseItemsRemove(UUID.randomUUID(),
                                     new CAIds(Set.of(CAItemID.random()))),
          new CAIResponseItemsRemove(UUID.randomUUID(),
                                     new CAIds(Set.of(CAItemID.random())))
        )
      );
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseFilePut()
    throws Exception
  {
    final var message = new CAIResponseFilePut(UUID.randomUUID(), FILE_0);
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseFileRemove()
    throws Exception
  {
    final var message = new CAIResponseFileRemove(UUID.randomUUID(),
                                                  FILE_0.id());
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseLoginUsernamePassword()
    throws Exception
  {
    final var message =
      new CAIResponseLogin(UUID.randomUUID());

    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseItemList0()
    throws Exception
  {
    final var message =
      new CAIResponseItemList(UUID.randomUUID(),
                              new CAItems(Set.of(ITEM_0, ITEM_1, ITEM_2)));
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseItemCreate()
    throws Exception
  {
    final var message =
      new CAIResponseItemCreate(UUID.randomUUID(), ITEM_0);
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseItemGet()
    throws Exception
  {
    final var message =
      new CAIResponseItemGet(UUID.randomUUID(), ITEM_0);
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseItemRemove()
    throws Exception
  {
    final var message =
      new CAIResponseItemsRemove(
        UUID.randomUUID(),
        new CAIds(Set.of(
          CAItemID.random(),
          CAItemID.random(),
          CAItemID.random()
        )));
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseItemAttachmentRemove()
    throws Exception
  {
    final var message =
      new CAIResponseItemAttachmentRemove(UUID.randomUUID(), ITEM_0);
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseItemMetadataRemove()
    throws Exception
  {
    final var message =
      new CAIResponseItemMetadataRemove(UUID.randomUUID(), ITEM_0);
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseItemMetadataPut()
    throws Exception
  {
    final var message =
      new CAIResponseItemMetadataPut(UUID.randomUUID(), ITEM_0);
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseTagsPut()
    throws Exception
  {
    final var message =
      new CAIResponseTagsPut(UUID.randomUUID(), new CATags(ITEM_0.tags()));
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseTagsRemove()
    throws Exception
  {
    final var message =
      new CAIResponseTagsDelete(UUID.randomUUID(), new CATags(ITEM_0.tags()));
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseTagList()
    throws Exception
  {
    final var message =
      new CAIResponseTagList(UUID.randomUUID(), new CATags(ITEM_0.tags()));
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseLocationPut()
    throws Exception
  {
    final var message =
      new CAIResponseLocationPut(
        UUID.randomUUID(),
        new CALocation(
          CALocationID.random(),
          Optional.of(CALocationID.random()),
          "Name",
          "Description"
        ));
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseLocationList()
    throws Exception
  {
    final var location0 =
      new CALocation(
        CALocationID.random(),
        Optional.of(CALocationID.random()),
        "Name",
        "Description"
      );
    final var location1 =
      new CALocation(
        CALocationID.random(),
        Optional.of(CALocationID.random()),
        "Name",
        "Description"
      );
    final var location2 =
      new CALocation(
        CALocationID.random(),
        Optional.of(CALocationID.random()),
        "Name",
        "Description"
      );

    final var message =
      new CAIResponseLocationList(
        UUID.randomUUID(),
        new CALocations(
          new TreeMap<>(
            Map.ofEntries(
              Map.entry(location0.id(), location0),
              Map.entry(location1.id(), location1),
              Map.entry(location2.id(), location2)
            )
          )
        )
      );
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseItemLocationsList()
    throws Exception
  {
    final var locations =
      new TreeMap<CALocationID, SortedMap<CAItemID, CAItemLocation>>();

    final var location0 =
      new CAItemLocation(
        CAItemID.random(),
        CALocationID.random(),
        1000L
      );

    final var location1 =
      new CAItemLocation(
        CAItemID.random(),
        CALocationID.random(),
        2000L
      );

    final var location2 =
      new CAItemLocation(
        CAItemID.random(),
        CALocationID.random(),
        3000L
      );

    final var locationMap0 = new TreeMap<CAItemID, CAItemLocation>();
    locationMap0.put(location0.item(), location0);
    final var locationMap1 = new TreeMap<CAItemID, CAItemLocation>();
    locationMap1.put(location1.item(), location1);
    final var locationMap2 = new TreeMap<CAItemID, CAItemLocation>();
    locationMap2.put(location2.item(), location2);

    locations.put(location0.location(), locationMap0);
    locations.put(location1.location(), locationMap1);
    locations.put(location2.location(), locationMap2);

    final var message =
      new CAIResponseItemLocationsList(
        UUID.randomUUID(),
        new CAItemLocations(locations)
      );
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testEventUpdated()
    throws Exception
  {
    final var message =
      new CAIEventType.CAEventUpdated(
        Set.of(
          CAItemID.random(),
          CALocationID.random(),
          CATagID.random()
        ),
        Set.of(
          CAItemID.random(),
          CALocationID.random(),
          CATagID.random()
        )
      );
    assertEquals(message, this.roundTrip(message));
  }

  private CAIMessageType roundTrip(
    final CAIMessageType message)
    throws Exception
  {
    final var serialized = this.messages.serialize(message);
    return this.messages.parse(serialized);
  }
}
