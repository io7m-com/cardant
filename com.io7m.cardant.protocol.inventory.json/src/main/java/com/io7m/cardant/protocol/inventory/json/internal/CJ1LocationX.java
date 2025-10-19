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

import com.io7m.cardant.model.CAAttachment;
import com.io7m.cardant.model.CAAttachmentKey;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.model.CATypeRecordFieldIdentifier;
import com.io7m.cardant.model.CATypeRecordIdentifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static com.io7m.cardant.protocol.inventory.json.internal.CJ1AttachmentKeyX.ATTACHMENT_KEY;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1AttachmentX.ATTACHMENT;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1LocationPathX.LOCATION_PATH;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1MetadataX.METADATA;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1TypeRecordFieldIdentifierX.TYPE_RECORD_FIELD_IDENTIFIER;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1TypeRecordIdentifierX.TYPE_RECORD_IDENTIFIER;

public enum CJ1LocationX
  implements CJ1SerialBijectionType<CJ1Location, CALocation>
{
  LOCATION;

  @Override
  public CALocation toCore(
    final CJ1Location m)
  {
    return new CALocation(
      CALocationID.of(m.id()),
      m.parent().map(CALocationID::of),
      LOCATION_PATH.toCore(m.path()),
      m.timeCreated(),
      m.timeUpdated(),
      toCoreMetadata(m.metadata()),
      toCoreAttachments(m.attachments()),
      toCoreTypes(m.types())
    );
  }

  private static SortedSet<CATypeRecordIdentifier> toCoreTypes(
    final Set<CJ1TypeRecordIdentifier> types)
  {
    return types.stream()
      .map(TYPE_RECORD_IDENTIFIER::toCore)
      .collect(Collectors.toCollection(TreeSet::new));
  }

  private static SortedMap<CAAttachmentKey, CAAttachment> toCoreAttachments(
    final List<CJ1AttachmentItem> attachments)
  {
    final var r = new TreeMap<CAAttachmentKey, CAAttachment>();
    for (final var e : attachments) {
      r.put(
        ATTACHMENT_KEY.toCore(e.key()),
        ATTACHMENT.toCore(e.attachment())
      );
    }
    return r;
  }

  private static SortedMap<CATypeRecordFieldIdentifier, CAMetadataType> toCoreMetadata(
    final Map<CJ1TypeRecordFieldIdentifier, CJ1MetadataType> metadata)
  {
    final var r = new TreeMap<CATypeRecordFieldIdentifier, CAMetadataType>();
    for (final var e : metadata.entrySet()) {
      r.put(
        TYPE_RECORD_FIELD_IDENTIFIER.toCore(e.getKey()),
        METADATA.toCore(e.getValue())
      );
    }
    return r;
  }

  @Override
  public CJ1Location toCJ1(
    final CALocation m)
  {
    return new CJ1Location(
      m.id().id(),
      m.parent().map(CALocationID::id),
      LOCATION_PATH.toCJ1(m.path()),
      m.timeCreated(),
      m.timeUpdated(),
      toCJ1Metadata(m.metadata()),
      toCJ1Attachments(m.attachments()),
      toCJ1Types(m.types())
    );
  }

  private static Set<CJ1TypeRecordIdentifier> toCJ1Types(
    final SortedSet<CATypeRecordIdentifier> types)
  {
    return types.stream()
      .map(TYPE_RECORD_IDENTIFIER::toCJ1)
      .collect(Collectors.toSet());
  }

  private static List<CJ1AttachmentItem> toCJ1Attachments(
    final SortedMap<CAAttachmentKey, CAAttachment> attachments)
  {
    final var r = new ArrayList<CJ1AttachmentItem>(attachments.size());
    for (final var e : attachments.entrySet()) {
      r.add(
        new CJ1AttachmentItem(
          ATTACHMENT_KEY.toCJ1(e.getKey()),
          ATTACHMENT.toCJ1(e.getValue())
        )
      );
    }
    return r;
  }

  private static Map<CJ1TypeRecordFieldIdentifier, CJ1MetadataType> toCJ1Metadata(
    final SortedMap<CATypeRecordFieldIdentifier, CAMetadataType> metadata)
  {
    final var r =
      new HashMap<CJ1TypeRecordFieldIdentifier, CJ1MetadataType>(metadata.size());
    for (final var e : metadata.entrySet()) {
      r.put(
        TYPE_RECORD_FIELD_IDENTIFIER.toCJ1(e.getKey()),
        METADATA.toCJ1(e.getValue())
      );
    }
    return r;
  }
}
