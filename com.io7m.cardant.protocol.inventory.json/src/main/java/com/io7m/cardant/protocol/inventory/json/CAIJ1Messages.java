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


package com.io7m.cardant.protocol.inventory.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.api.CAProtocolMessagesType;
import com.io7m.cardant.protocol.inventory.CAIMessageType;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1AuditSearchParameters;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandAuditSearchBegin;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandAuditSearchNext;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandAuditSearchPrevious;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandFileDelete;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandFileGet;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandFilePut;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandFileSearchBegin;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandFileSearchNext;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandFileSearchPrevious;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandItemAttachmentAdd;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandItemAttachmentRemove;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandItemCreate;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandItemDelete;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandItemGet;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandItemMetadataPut;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandItemMetadataRemove;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandItemSearchBegin;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandItemSearchNext;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandItemSearchPrevious;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandItemSetName;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandLocationAttachmentAdd;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandLocationAttachmentRemove;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandLocationDelete;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandLocationGet;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandLocationMetadataPut;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandLocationMetadataRemove;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandLocationPut;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandLogin;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1ComparisonExactType;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1ComparisonFuzzyType;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1ComparisonSetType;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CurrencyUnitDeserializer;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1CurrencyUnitSerializer;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1DottedNameDeserializer;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1DottedNameSerializer;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1FileColumn;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1FileColumnOrdering;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1FileSearchParameters;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1FileType;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1IncludeDeleted;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1ItemColumn;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1ItemColumnOrdering;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1ItemSearchParameters;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1Location;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1MessageType;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1MessageTypeX;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1MetadataElementMatchType;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1MetadataType;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1MetadataValueMatchType;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1SizeRange;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1TimeRange;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1TypeRecordFieldIdentifier;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1TypeRecordFieldIdentifierDeserializer;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1TypeRecordFieldIdentifierKeyDeserializer;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1TypeRecordFieldIdentifierKeySerializer;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1TypeRecordFieldIdentifierSerializer;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1TypeRecordIdentifier;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1TypeRecordIdentifierDeserializer;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1TypeRecordIdentifierKeyDeserializer;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1TypeRecordIdentifierSerializer;
import com.io7m.dixmont.core.DmJsonRestrictedDeserializers;
import com.io7m.lanark.core.RDottedName;
import com.io7m.repetoir.core.RPServiceType;
import org.joda.money.CurrencyUnit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.io7m.cardant.protocol.inventory.json.internal.CJ1MessageTypeX.*;

/**
 * The protocol messages for Inventory JSON.
 */

public final class CAIJ1Messages
  implements CAProtocolMessagesType<CAIMessageType>, RPServiceType
{
  private static final Set<String> SERIALIZATION_WHITELIST =
    loadSerializationWhitelist();

  private static Set<String> loadSerializationWhitelist()
  {
    final var source =
      CAIJ1Messages.class.getResource(
        "/com/io7m/cardant/protocol/inventory/json/serialization-whitelist.txt"
      );

    Objects.requireNonNull(source, "source");
    try (final var stream = source.openStream()) {
      try (final var reader = new BufferedReader(new InputStreamReader(stream))) {
        return reader.lines()
          .map(String::trim)
          .filter(x -> !x.isEmpty())
          .collect(Collectors.toUnmodifiableSet());
      }
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static final UUID PROTOCOL_ID =
    UUID.fromString("0135d8f5-2dad-4ca7-85db-614ba435300e");

  /**
   * The content type for the protocol.
   */

  public static final String CONTENT_TYPE =
    "application/vnd.cardant_inventory+json";

  /**
   * The content type for the protocol.
   */

  public static final String CONTENT_TYPE_FOR_SEQUENCE =
    "application/vnd.cardant_inventory_sequence+json";

  private final SimpleDeserializers serializers;
  private final JsonMapper mapper;

  /**
   * The protocol messages for Inventory v1 JSON.
   */

  public CAIJ1Messages()
  {
    this.serializers =
      DmJsonRestrictedDeserializers.builder()
        .allowClassNames(SERIALIZATION_WHITELIST)
        .build();

    this.mapper =
      JsonMapper.builder()
        .build();

    final var simpleModule = new SimpleModule();
    simpleModule.setDeserializers(this.serializers);

    simpleModule.addSerializer(
      CJ1TypeRecordFieldIdentifier.class,
      new CJ1TypeRecordFieldIdentifierSerializer()
    );
    simpleModule.addDeserializer(
      CJ1TypeRecordFieldIdentifier.class,
      new CJ1TypeRecordFieldIdentifierDeserializer()
    );
    simpleModule.addKeyDeserializer(
      CJ1TypeRecordFieldIdentifier.class,
      new CJ1TypeRecordFieldIdentifierKeyDeserializer()
    );
    simpleModule.addKeySerializer(
      CJ1TypeRecordFieldIdentifier.class,
      new CJ1TypeRecordFieldIdentifierKeySerializer()
    );

    simpleModule.addSerializer(
      CJ1TypeRecordIdentifier.class,
      new CJ1TypeRecordIdentifierSerializer()
    );
    simpleModule.addDeserializer(
      CJ1TypeRecordIdentifier.class,
      new CJ1TypeRecordIdentifierDeserializer()
    );
    simpleModule.addKeyDeserializer(
      CJ1TypeRecordIdentifier.class,
      new CJ1TypeRecordIdentifierKeyDeserializer()
    );

    simpleModule.addSerializer(
      CurrencyUnit.class,
      new CJ1CurrencyUnitSerializer()
    );
    simpleModule.addDeserializer(
      CurrencyUnit.class,
      new CJ1CurrencyUnitDeserializer()
    );

    simpleModule.addSerializer(
      RDottedName.class,
      new CJ1DottedNameSerializer()
    );
    simpleModule.addDeserializer(
      RDottedName.class,
      new CJ1DottedNameDeserializer()
    );

    this.mapper.registerModule(simpleModule);
    this.mapper.registerModule(new Jdk8Module());
    this.mapper.registerModule(new JavaTimeModule());
    this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    this.mapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
  }

  /**
   * @return The content type
   */

  public static String contentType()
  {
    return CONTENT_TYPE;
  }

  /**
   * @return The content type for sequences
   */

  public static String contentTypeForSequence()
  {
    return CONTENT_TYPE_FOR_SEQUENCE;
  }

  /**
   * @return The protocol identifier
   */

  public static UUID protocolId()
  {
    return PROTOCOL_ID;
  }

  @Override
  public CAIMessageType parse(
    final byte[] data)
    throws CAProtocolException
  {
    try {
      return MESSAGE.toCore(
        this.mapper.readValue(data, CJ1MessageType.class)
      );
    } catch (final IOException e) {
      throw new CAProtocolException(
        e.getMessage(),
        e,
        CAStandardErrorCodes.errorProtocol(),
        Map.of(),
        Optional.empty()
      );
    }
  }

  @Override
  public byte[] serialize(
    final CAIMessageType message)
    throws CAProtocolException
  {
    try {
      return this.mapper.writeValueAsBytes(MESSAGE.toCJ1(message));
    } catch (final JsonProcessingException e) {
      throw new CAProtocolException(
        e.getMessage(),
        e,
        CAStandardErrorCodes.errorProtocol(),
        Map.of(),
        Optional.empty()
      );
    }
  }

  @Override
  public String description()
  {
    return "Inventory JSON message service.";
  }

  @Override
  public String toString()
  {
    return "[CAIJ1Messages 0x%s]"
      .formatted(Long.toUnsignedString(this.hashCode(), 16));
  }
}
