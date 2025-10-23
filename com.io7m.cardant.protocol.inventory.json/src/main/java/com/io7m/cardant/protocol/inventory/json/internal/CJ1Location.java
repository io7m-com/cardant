/*
 * Copyright © 2025 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.protocol.inventory.json.internal;


import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@JsonClassDescription("A location.")
public record CJ1Location(
  @JsonPropertyDescription("The location ID.")
  @JsonProperty(value = "id", required = true)
  UUID id,
  @JsonPropertyDescription("The location parent.")
  @JsonProperty(value = "parent")
  Optional<UUID> parent,
  @JsonPropertyDescription("The location path.")
  @JsonProperty(value = "path", required = true)
  List<String> path,
  @JsonPropertyDescription("The location creation time.")
  @JsonProperty(value = "timeCreated", required = true)
  OffsetDateTime timeCreated,
  @JsonPropertyDescription("The location most recent update time.")
  @JsonProperty(value = "timeUpdated", required = true)
  OffsetDateTime timeUpdated,
  @JsonPropertyDescription("The location metadata.")
  @JsonProperty("metadata")
  Map<CJ1TypeRecordFieldIdentifier, CJ1MetadataType> metadata,
  @JsonPropertyDescription("The location attachments.")
  @JsonProperty("attachments")
  List<CJ1AttachmentItem> attachments,
  @JsonPropertyDescription("The location types.")
  @JsonProperty("types")
  Set<CJ1TypeRecordIdentifier> types)
  implements CJ1ValueType
{

}
