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
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemAttachment;
import com.io7m.cardant.model.CAItemAttachmentID;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CAItems;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CATagID;
import com.io7m.cardant.model.CATags;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.protocol.inventory.api.CAEventType;
import com.io7m.cardant.protocol.inventory.api.CAMessageParserFactoryType;
import com.io7m.cardant.protocol.inventory.api.CAMessageSerializerFactoryType;
import com.io7m.cardant.protocol.inventory.api.CAMessageServices;
import com.io7m.cardant.protocol.inventory.api.CAMessageType;
import com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseLoginUsernamePassword;
import com.io7m.cardant.protocol.inventory.api.CATransaction;
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
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationExact;
import static com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationWithDescendants;
import static com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationsAll;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemAttachmentPut;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemAttachmentRemove;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemCreate;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemGet;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemList;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemMetadataPut;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemMetadataRemove;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemRemove;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandLoginUsernamePassword;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandTagList;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandTagsDelete;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandTagsPut;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemAttachmentPut;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemAttachmentRemove;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemCreate;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemGet;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemList;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemMetadataPut;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemMetadataRemove;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemRemove;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseTagList;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseTagsDelete;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseTagsPut;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class CAMessageServicesV1Test
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAMessageServicesV1Test.class);

  private static final URI SOURCE = URI.create("urn:loop");
  private CAMessageServices services;
  private CAMessageParserFactoryType parsers;
  private CAMessageSerializerFactoryType serializers;

  private static final CAItem ITEM_0 = makeItem(0);
  private static final CAItem ITEM_1 = makeItem(1);
  private static final CAItem ITEM_2 = makeItem(2);

  private static CAItem makeItem(
    final int index)
  {
    return new CAItem(
      CAItemID.random(),
      "Item " + index,
      200L,
      new TreeMap<>(
        Map.ofEntries(
          Map.entry("M0", new CAItemMetadata("M0", "V0")),
          Map.entry("M1", new CAItemMetadata("M1", "V1")),
          Map.entry("M2", new CAItemMetadata("M2", "V2"))
        )
      ),
      new TreeMap<>(
        Stream.of(
          new CAItemAttachment(
            CAItemAttachmentID.random(),
            "Description",
            "text/plain",
            "text",
            100L,
            "SHA-256",
            "01ba4719c80b6fe911b091a7c05124b64eeece964e09c058ef8f9805daca546b",
            Optional.empty()
          ),
          new CAItemAttachment(
            CAItemAttachmentID.random(),
            "Description",
            "text/plain",
            "text",
            100L,
            "SHA-256",
            "73cb3858a687a8494ca3323053016282f3dad39d42cf62ca4e79dda2aac7d9ac",
            Optional.empty()
          ),
          new CAItemAttachment(
            CAItemAttachmentID.random(),
            "Description",
            "text/plain",
            "text",
            100L,
            "SHA-256",
            "3bb2abb69ebb27fbfe63c7639624c6ec5e331b841a5bc8c3ebc10b9285e90877",
            Optional.empty()
          )
        ).collect(Collectors.toMap(CAItemAttachment::id, Function.identity()))
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
      new CACommandItemRemove(CAItemID.random());
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandItemAttachmentRemove()
    throws Exception
  {
    final var message =
      new CACommandItemAttachmentRemove(
        CAItemID.random(),
        CAItemAttachmentID.random());
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandItemAttachmentPut0()
    throws Exception
  {
    final var message =
      new CACommandItemAttachmentPut(
        CAItemID.random(),
        new CAItemAttachment(
          CAItemAttachmentID.random(),
          "Description",
          "text/plain",
          "text",
          100L,
          "SHA-256",
          "01ba4719c80b6fe911b091a7c05124b64eeece964e09c058ef8f9805daca546b",
          Optional.empty()
        ));
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testCommandItemAttachmentPut1()
    throws Exception
  {
    final var message =
      new CACommandItemAttachmentPut(
        CAItemID.random(),
        new CAItemAttachment(
          CAItemAttachmentID.random(),
          "Description",
          "text/plain",
          "text",
          5L,
          "SHA-256",
          "01ba4719c80b6fe911b091a7c05124b64eeece964e09c058ef8f9805daca546b",
          Optional.of(new CAByteArray("HELLO".getBytes(UTF_8)))
        ));
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
  public void testTransaction()
    throws Exception
  {
    final var message =
      new CATransaction(
        List.of(
          new CACommandLoginUsernamePassword(
            "user", "pass"),
          new CACommandItemList(
            new CAListLocationsAll()),
          new CACommandItemCreate(
            CAItemID.random(), "Item"),
          new CACommandItemGet(
            CAItemID.random()),
          new CACommandItemRemove(
            CAItemID.random()),
          new CACommandItemAttachmentPut(
            CAItemID.random(),
            ITEM_0.attachments().values().stream().findFirst().orElseThrow()),
          new CACommandItemAttachmentRemove(
            CAItemID.random(),
            CAItemAttachmentID.random()),
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
            new CATags(ITEM_0.tags()))
        )
      );
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
      new CAResponseItemRemove(CAItemID.random());
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
  public void testResponseItemAttachmentPut0()
    throws Exception
  {
    final var message =
      new CAResponseItemAttachmentPut(ITEM_0);
    assertEquals(message, this.roundTrip(message));
  }

  @Test
  public void testResponseItemAttachmentPut1()
    throws Exception
  {
    final var message =
      new CAResponseItemAttachmentPut(ITEM_0);
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
  public void testEventUpdated()
    throws Exception
  {
    final var message =
      new CAEventType.CAEventUpdated(
        Set.of(
          CAItemID.random(),
          CAItemAttachmentID.random(),
          CALocationID.random(),
          CATagID.random(),
          CAUserID.random()
        ),
        Set.of(
          CAItemID.random(),
          CAItemAttachmentID.random(),
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
