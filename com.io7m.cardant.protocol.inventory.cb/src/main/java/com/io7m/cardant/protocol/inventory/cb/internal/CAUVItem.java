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


package com.io7m.cardant.protocol.inventory.cb.internal;

import com.io7m.cardant.model.CAAttachment;
import com.io7m.cardant.model.CAAttachmentKey;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.model.CATypeRecordFieldIdentifier;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1Attachment;
import com.io7m.cardant.protocol.inventory.cb.CAI1AttachmentKey;
import com.io7m.cardant.protocol.inventory.cb.CAI1Item;
import com.io7m.cardant.protocol.inventory.cb.CAI1Metadata;
import com.io7m.cardant.protocol.inventory.cb.CAI1TypeRecordFieldIdentifier;
import com.io7m.cedarbridge.runtime.api.CBList;
import com.io7m.cedarbridge.runtime.api.CBMap;
import com.io7m.cedarbridge.runtime.api.CBString;
import com.io7m.cedarbridge.runtime.api.CBUUID;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVAttachment.ATTACHMENT;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVAttachmentKey.ATTACHMENT_KEY;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVMetadata.METADATA;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVTypeRecordFieldIdentifier.TYPE_RECORD_FIELD_IDENTIFIER;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVTypeRecordIdentifier.TYPE_RECORD_IDENTIFIER;

/**
 * A validator.
 */

public enum CAUVItem
  implements CAProtocolMessageValidatorType<CAItem, CAI1Item>
{
  /**
   * A validator.
   */

  ITEM;

  @Override
  public CAI1Item convertToWire(
    final CAItem item)
  {
    final var attachments =
      new HashMap<CAI1AttachmentKey, CAI1Attachment>();

    for (final var entry : item.attachments().entrySet()) {
      final var key =
        entry.getKey();
      final var val =
        entry.getValue();
      final var rKey =
        ATTACHMENT_KEY.convertToWire(key);
      final var rVal =
        ATTACHMENT.convertToWire(val);
      attachments.put(rKey, rVal);
    }

    final var metadata =
      new HashMap<CAI1TypeRecordFieldIdentifier, CAI1Metadata>();

    for (final var entry : item.metadata().entrySet()) {
      final var key =
        entry.getKey();
      final var val =
        entry.getValue();
      final var rKey =
        TYPE_RECORD_FIELD_IDENTIFIER.convertToWire(key);
      final var rVal =
        METADATA.convertToWire(val);
      metadata.put(rKey, rVal);
    }

    return new CAI1Item(
      new CBUUID(item.id().id()),
      new CBString(item.name()),
      new CBMap<>(metadata),
      new CBMap<>(attachments),
      new CBList<>(
        item.types()
          .stream()
          .map(TYPE_RECORD_IDENTIFIER::convertToWire)
          .toList()
      )
    );
  }

  @Override
  public CAItem convertFromWire(
    final CAI1Item item)
  {
    final var attachments =
      new HashMap<CAAttachmentKey, CAAttachment>();

    for (final var entry : item.fieldAttachments().values().entrySet()) {
      final var key =
        entry.getKey();
      final var val =
        entry.getValue();
      final var rKey =
        ATTACHMENT_KEY.convertFromWire(key);
      final var rVal =
        ATTACHMENT.convertFromWire(val);
      attachments.put(rKey, rVal);
    }

    final var metadata =
      new HashMap<CATypeRecordFieldIdentifier, CAMetadataType>();

    for (final var entry : item.fieldMetadata().values().entrySet()) {
      final var key =
        entry.getKey();
      final var val =
        entry.getValue();
      final var rKey =
        TYPE_RECORD_FIELD_IDENTIFIER.convertFromWire(key);
      final var rVal =
        METADATA.convertFromWire(val);
      metadata.put(rKey, rVal);
    }

    return new CAItem(
      new CAItemID(item.fieldId().value()),
      item.fieldName().value(),
      new TreeMap<>(metadata),
      new TreeMap<>(attachments),
      new TreeSet<>(
        item.fieldTypes()
          .values()
          .stream()
          .map(TYPE_RECORD_IDENTIFIER::convertFromWire)
          .toList()
      )
    );
  }
}
