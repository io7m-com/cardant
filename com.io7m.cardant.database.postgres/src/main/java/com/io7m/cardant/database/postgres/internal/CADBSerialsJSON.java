/*
 * Copyright © 2024 Mark Raynsford <code@io7m.com> https://www.io7m.com
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


package com.io7m.cardant.database.postgres.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.io7m.cardant.model.CAItemSerial;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.lanark.core.RDottedName;
import org.jooq.JSON;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Functions to serialize/deserialize JSON serial numbers.
 */

public final class CADBSerialsJSON
{
  private static final ObjectMapper OBJECT_MAPPER =
    new ObjectMapper();

  private CADBSerialsJSON()
  {

  }

  /**
   * Convert serial numbers to a JSON array.
   *
   * @param serials The serial numbers
   *
   * @return The JSON
   */

  public static JSON serialsToJSON(
    final List<CAItemSerial> serials)
  {
    final var array = OBJECT_MAPPER.createArrayNode();
    for (final var serial : serials) {
      final var object = OBJECT_MAPPER.createObjectNode();
      object.put("Type", serial.type().value());
      object.put("Value", serial.value());
      array.add(object);
    }

    try {
      return JSON.json(OBJECT_MAPPER.writeValueAsString(array));
    } catch (final JsonProcessingException e) {
      throw new UnreachableCodeException(e);
    }
  }

  /**
   * Convert serial numbers from possibly-nested JSON arrays.
   *
   * @param json The json
   *
   * @return The parsed serial numbers
   *
   * @throws IOException On parse errors
   */

  public static List<CAItemSerial> serialsFromJSON(
    final JSON json)
    throws IOException
  {
    final var output = new ArrayList<CAItemSerial>();
    parseSerialsNode(output, OBJECT_MAPPER.readTree(json.data()));
    return List.copyOf(output);
  }

  private static void parseSerialsNode(
    final ArrayList<CAItemSerial> output,
    final JsonNode jsonNode)
    throws IOException
  {
    switch (jsonNode) {
      case final ObjectNode o -> {
        output.add(new CAItemSerial(
          new RDottedName(o.get("Type").textValue()),
          o.get("Value").textValue()
        ));
      }
      case final ArrayNode a -> {
        for (final var x : a) {
          parseSerialsNode(output, x);
        }
      }
      default -> {
        throw new IOException(
          "Unparseable serial number object: %s".formatted(jsonNode)
        );
      }
    }
  }
}
