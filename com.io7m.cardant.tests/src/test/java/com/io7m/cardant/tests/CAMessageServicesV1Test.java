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
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandFilePut;
import com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandFileRemove;
import com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemReposit;
import com.io7m.cardant.protocol.inventory.api.CAEventType;
import com.io7m.cardant.protocol.inventory.api.CAMessageParserFactoryType;
import com.io7m.cardant.protocol.inventory.api.CAMessageSerializerFactoryType;
import com.io7m.cardant.protocol.inventory.api.CAMessageServices;
import com.io7m.cardant.protocol.inventory.api.CAMessageType;
import com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseFilePut;
import com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseFileRemove;
import com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseLoginUsernamePassword;
import com.io7m.cardant.protocol.inventory.api.CATransaction;
import com.io7m.cardant.protocol.inventory.api.CATransactionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import static com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationExact;
import static com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationWithDescendants;
import static com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationsAll;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemCreate;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemGet;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemList;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemLocationsList;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemMetadataPut;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemMetadataRemove;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemsRemove;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandLocationList;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandLocationPut;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandLoginUsernamePassword;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandTagList;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandTagsDelete;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandTagsPut;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseError;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemAttachmentRemove;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemCreate;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemGet;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemList;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemLocationsList;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemMetadataPut;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemMetadataRemove;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemsRemove;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseLocationList;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseLocationPut;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseTagList;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseTagsDelete;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseTagsPut;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class CAMessageServicesV1Test
{
  public static final CAFileType.CAFileWithData FILE_0 = new CAFileType.CAFileWithData(
    CAFileID.random(),
    "description",
    "text/plain",
    5L,
    "SHA-256",
    "3733cd977ff8eb18b987357e22ced99f46097f31ecb239e878ae63760e83e4d5",
    new CAByteArray("HELLO".getBytes(UTF_8))
  );
  private static final Logger LOG =
    LoggerFactory.getLogger(CAMessageServicesV1Test.class);
  private static final URI SOURCE = URI.create("urn:loop");
  private static final CAItem ITEM_0 = makeItem(0);
  private static final CAItem ITEM_1 = makeItem(1);
  private static final CAItem ITEM_2 = makeItem(2);
  private CAMessageServices services;
  private CAMessageParserFactoryType parsers;
  private CAMessageSerializerFactoryType serializers;

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
    this.services =
      new CAMessageServices();
    this.parsers =
      this.services.findParserService(1);
    this.serializers =
      this.services.findSerializerService(1);
  }

  @Test
  public void testCommandFilePut()
    throws Exception
  {
    final var message =
      new CACommandFilePut(FILE_0);
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandFileRemove()
    throws Exception
  {
    final var message =
      new CACommandFileRemove(FILE_0.id());
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandLoginUsernamePassword()
    throws Exception
  {
    final var message =
      new CACommandLoginUsernamePassword("user", "password");

    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandItemList0()
    throws Exception
  {
    final var message =
      new CACommandItemList(new CAListLocationsAll());
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandItemList1()
    throws Exception
  {
    final var message =
      new CACommandItemList(
        new CAListLocationExact(CALocationID.random()));
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandItemList2()
    throws Exception
  {
    final var message =
      new CACommandItemList(
        new CAListLocationWithDescendants(CALocationID.random()));
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandItemCreate()
    throws Exception
  {
    final var message =
      new CACommandItemCreate(CAItemID.random(), "Item 0");
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandItemGet()
    throws Exception
  {
    final var message =
      new CACommandItemGet(CAItemID.random());
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandItemRemove()
    throws Exception
  {
    final var message =
      new CACommandItemsRemove(
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
      new CACommandItemMetadataRemove(
        CAItemID.random(),
        Set.of("Meta0", "Meta1", "Meta2"));
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandItemMetadataPut()
    throws Exception
  {
    final var message =
      new CACommandItemMetadataPut(
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
      new CACommandTagsPut(new CATags(ITEM_0.tags()));
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandTagsRemove()
    throws Exception
  {
    final var message =
      new CACommandTagsDelete(new CATags(ITEM_0.tags()));
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandTagList()
    throws Exception
  {
    final var message =
      new CACommandTagList();
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandLocationPut0()
    throws Exception
  {
    final var message =
      new CACommandLocationPut(
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
      new CACommandLocationPut(
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
      new CACommandLocationList();
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandItemRepositAdd()
    throws Exception
  {
    final var message =
      new CACommandItemReposit(new CAItemRepositAdd(
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
      new CACommandItemReposit(new CAItemRepositRemove(
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
      new CACommandItemReposit(new CAItemRepositMove(
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
      new CACommandItemLocationsList(CAItemID.random());
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testTransaction()
    throws Exception
  {
    final var message =
      new CATransaction(
        List.of(
          new CACommandItemLocationsList(CAItemID.random()),
          new CACommandLoginUsernamePassword(
            "user", "pass"),
          new CACommandItemList(
            new CAListLocationsAll()),
          new CACommandItemCreate(
            CAItemID.random(), "Item"),
          new CACommandItemGet(
            CAItemID.random()),
          new CACommandItemsRemove(
            Set.of(
              CAItemID.random(),
              CAItemID.random(),
              CAItemID.random()
            )),
          new CACommandItemMetadataPut(
            CAItemID.random(),
            new HashSet<>(ITEM_0.metadata().values())),
          new CACommandItemMetadataRemove(
            CAItemID.random(),
            Set.of("Meta0")),
          new CACommandTagList(),
          new CACommandTagsDelete(
            new CATags(ITEM_0.tags())),
          new CACommandTagsPut(
            new CATags(ITEM_0.tags())),
          new CACommandLocationList(),
          new CACommandLocationPut(
            new CALocation(CALocationID.random(), Optional.empty(), "N", "D")
          ),
          new CACommandItemReposit(new CAItemRepositAdd(
            CAItemID.random(),
            CALocationID.random(),
            0xffff_ffff_ffff_ffffL
          )),
          new CACommandItemReposit(new CAItemRepositRemove(
            CAItemID.random(),
            CALocationID.random(),
            0xffff_ffff_ffff_ffffL
          )),
          new CACommandItemReposit(new CAItemRepositMove(
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
      new CATransactionResponse(
        true,
        List.of(
          new CAResponseItemsRemove(new CAIds(Set.of(CAItemID.random()))),
          new CAResponseError("Failed", 400, Map.of(), List.of())
        )
      );
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testTransactionResponse1()
    throws Exception
  {
    final var message =
      new CATransactionResponse(
        false,
        List.of(
          new CAResponseItemsRemove(new CAIds(Set.of(CAItemID.random()))),
          new CAResponseItemsRemove(new CAIds(Set.of(CAItemID.random()))),
          new CAResponseItemsRemove(new CAIds(Set.of(CAItemID.random())))
        )
      );
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseFilePut()
    throws Exception
  {
    final var message = new CAResponseFilePut(FILE_0);
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseFileRemove()
    throws Exception
  {
    final var message = new CAResponseFileRemove(FILE_0.id());
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseLoginUsernamePassword()
    throws Exception
  {
    final var message =
      new CAResponseLoginUsernamePassword();

    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseItemList0()
    throws Exception
  {
    final var message =
      new CAResponseItemList(new CAItems(Set.of(ITEM_0, ITEM_1, ITEM_2)));
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseItemCreate()
    throws Exception
  {
    final var message =
      new CAResponseItemCreate(ITEM_0);
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseItemGet()
    throws Exception
  {
    final var message =
      new CAResponseItemGet(ITEM_0);
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseItemRemove()
    throws Exception
  {
    final var message =
      new CAResponseItemsRemove(
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
      new CAResponseItemAttachmentRemove(ITEM_0);
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseItemMetadataRemove()
    throws Exception
  {
    final var message =
      new CAResponseItemMetadataRemove(ITEM_0);
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseItemMetadataPut()
    throws Exception
  {
    final var message =
      new CAResponseItemMetadataPut(ITEM_0);
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseTagsPut()
    throws Exception
  {
    final var message =
      new CAResponseTagsPut(new CATags(ITEM_0.tags()));
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseTagsRemove()
    throws Exception
  {
    final var message =
      new CAResponseTagsDelete(new CATags(ITEM_0.tags()));
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseTagList()
    throws Exception
  {
    final var message =
      new CAResponseTagList(new CATags(ITEM_0.tags()));
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseLocationPut()
    throws Exception
  {
    final var message =
      new CAResponseLocationPut(new CALocation(
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
      new CAResponseLocationList(
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
      new CAResponseItemLocationsList(
        new CAItemLocations(locations)
      );
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testEventUpdated()
    throws Exception
  {
    final var message =
      new CAEventType.CAEventUpdated(
        Set.of(
          CAItemID.random(),
          CALocationID.random(),
          CATagID.random(),
          CAUserID.random()
        ),
        Set.of(
          CAItemID.random(),
          CALocationID.random(),
          CATagID.random(),
          CAUserID.random()
        )
      );
    assertEquals(message, this.roundTrip(message));
  }

  private CAMessageType roundTrip(
    final CAMessageType message)
    throws Exception
  {
    try (var output = new ByteArrayOutputStream()) {
      this.serializers.serialize(SOURCE, output, message);

      LOG.debug("serialized: {}", output.toString(UTF_8));

      try (var input = new ByteArrayInputStream(output.toByteArray())) {
        return this.parsers.parse(SOURCE, input);
      } catch (final ParseException e) {
        LOG.error("parse failed: {}", e.getMessage());
        e.statusValues().forEach(parseStatus -> {
          LOG.error(
            "{}: {}:{}: {}",
            parseStatus.errorCode(),
            Integer.valueOf(parseStatus.lexical().line()),
            Integer.valueOf(parseStatus.lexical().column()),
            parseStatus.message()
          );
        });
        throw e;
      }
    }
  }
}
