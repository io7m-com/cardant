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

import com.io7m.blackthorne.api.BTElementHandlerType;
import com.io7m.blackthorne.api.BTElementParsingContextType;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemRepositMove;
import com.io7m.cardant.model.CALocationID;
import org.xml.sax.Attributes;

import java.util.UUID;

/**
 * A parser.
 */

public final class CAItemRepositMoveParser
  implements BTElementHandlerType<Object, CAItemRepositMove>
{
  private CAItemID itemId;
  private CALocationID fromLocation;
  private long itemCount;
  private CALocationID toLocation;

  /**
   * Construct a parser.
   *
   * @param context The parse context
   */

  public CAItemRepositMoveParser(
    final BTElementParsingContextType context)
  {

  }

  @Override
  public void onElementStart(
    final BTElementParsingContextType context,
    final Attributes attributes)
  {
    this.itemId =
      new CAItemID(UUID.fromString(attributes.getValue("item")));
    this.fromLocation =
      new CALocationID(UUID.fromString(attributes.getValue("fromLocation")));
    this.toLocation =
      new CALocationID(UUID.fromString(attributes.getValue("toLocation")));
    this.itemCount =
      Long.parseUnsignedLong(attributes.getValue("count"));
  }

  @Override
  public CAItemRepositMove onElementFinished(
    final BTElementParsingContextType context)
  {
    return new CAItemRepositMove(
      this.itemId,
      this.fromLocation,
      this.toLocation,
      this.itemCount
    );
  }
}
