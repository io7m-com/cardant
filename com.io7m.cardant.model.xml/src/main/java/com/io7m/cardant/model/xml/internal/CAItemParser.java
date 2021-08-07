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

package com.io7m.cardant.model.xml.internal;

import com.io7m.blackthorne.api.BTElementHandlerConstructorType;
import com.io7m.blackthorne.api.BTElementHandlerType;
import com.io7m.blackthorne.api.BTElementParsingContextType;
import com.io7m.blackthorne.api.BTQualifiedName;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemAttachment;
import com.io7m.cardant.model.CAItemAttachmentID;
import com.io7m.cardant.model.CAItemAttachments;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CAItemMetadatas;
import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CATags;
import org.xml.sax.Attributes;

import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.UUID;

import static com.io7m.cardant.model.xml.CAInventorySchemas.element1;

/**
 * A parser.
 */

public final class CAItemParser
  implements BTElementHandlerType<Object, CAItem>
{
  private CAItemID itemId;
  private String itemName;
  private long itemCount;
  private SortedMap<String, CAItemMetadata> metadatas;
  private SortedMap<CAItemAttachmentID, CAItemAttachment> attachments;
  private SortedSet<CATag> tags;

  /**
   * Construct a parser.
   *
   * @param context The parse context
   */

  public CAItemParser(
    final BTElementParsingContextType context)
  {

  }

  @Override
  public void onElementStart(
    final BTElementParsingContextType context,
    final Attributes attributes)
  {
    this.itemId =
      new CAItemID(UUID.fromString(attributes.getValue("id")));
    this.itemName =
      attributes.getValue("name");
    this.itemCount =
      Long.parseUnsignedLong(attributes.getValue("count"));
  }

  @Override
  public Map<BTQualifiedName, BTElementHandlerConstructorType<?, ?>>
  onChildHandlersRequested(final BTElementParsingContextType context)
  {
    return Map.ofEntries(
      Map.entry(
        element1("ItemMetadatas"),
        c -> new CAItemMetadatasParser(this.itemId, c)
      ),
      Map.entry(
        element1("ItemAttachments"),
        c -> new CAItemAttachmentsParser(this.itemId, c)
      ),
      Map.entry(
        element1("Tags"),
        CATagsParser::new
      )
    );
  }

  @Override
  public void onChildValueProduced(
    final BTElementParsingContextType context,
    final Object result)
  {
    if (result instanceof CAItemMetadatas received) {
      this.metadatas = received.metadatas();
      return;
    }
    if (result instanceof CAItemAttachments received) {
      this.attachments = received.attachments();
      return;
    }
    if (result instanceof CATags received) {
      this.tags = received.tags();
      return;
    }
    throw new IllegalStateException("Unexpected value: %s".formatted(result));
  }

  @Override
  public CAItem onElementFinished(
    final BTElementParsingContextType context)
  {
    return new CAItem(
      this.itemId,
      this.itemName,
      this.itemCount,
      this.metadatas,
      this.attachments,
      this.tags
    );
  }
}
