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
import com.io7m.cardant.model.CAItemAttachment;
import com.io7m.cardant.model.CAItemAttachmentID;
import com.io7m.cardant.model.CAItemAttachments;
import com.io7m.cardant.model.CAItemID;

import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.io7m.cardant.model.xml.CAInventorySchemas.element1;
import static java.util.Map.entry;

/**
 * A parser.
 */

public final class CAItemAttachmentsParser
  implements BTElementHandlerType<CAItemAttachment, CAItemAttachments>
{
  private final SortedMap<CAItemAttachmentID, CAItemAttachment> results;
  private final CAItemID itemId;

  /**
   * Construct a parser.
   *
   * @param inItemId The item ID
   * @param context  The parse context
   */

  public CAItemAttachmentsParser(
    final CAItemID inItemId,
    final BTElementParsingContextType context)
  {
    this.itemId = Objects.requireNonNull(inItemId, "itemId");
    this.results = new TreeMap<>();
  }

  @Override
  public Map<BTQualifiedName, BTElementHandlerConstructorType<?, ? extends CAItemAttachment>>
  onChildHandlersRequested(
    final BTElementParsingContextType context)
  {
    return Map.ofEntries(
      entry(
        element1("ItemAttachment"),
        c -> new CAItemAttachmentParser(this.itemId, c)
      )
    );
  }

  @Override
  public void onChildValueProduced(
    final BTElementParsingContextType context,
    final CAItemAttachment result)
  {
    this.results.put(result.id(), result);
  }

  @Override
  public CAItemAttachments onElementFinished(
    final BTElementParsingContextType context)
  {
    return new CAItemAttachments(this.results);
  }
}
