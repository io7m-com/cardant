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

package com.io7m.cardant.protocol.inventory.v1.internal;

import com.io7m.blackthorne.api.BTElementHandlerConstructorType;
import com.io7m.blackthorne.api.BTElementHandlerType;
import com.io7m.blackthorne.api.BTElementParsingContextType;
import com.io7m.blackthorne.api.BTQualifiedName;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1InventoryEventUpdated;

import java.util.Map;

import static com.io7m.cardant.protocol.inventory.v1.CA1InventorySchemas.element1;

/**
 * A parser.
 */

public final class CA1EventUpdatedParser
  implements BTElementHandlerType<CA1EventUpdatedSubsetType, CA1InventoryEventUpdated>
{
  private CA1EventUpdatedRemoved removed;
  private CA1EventUpdatedUpdated updated;

  /**
   * Create a parser.
   *
   * @param context The parsing context
   */

  public CA1EventUpdatedParser(
    final BTElementParsingContextType context)
  {

  }

  @Override
  public void onChildValueProduced(
    final BTElementParsingContextType context,
    final CA1EventUpdatedSubsetType result)
    throws Exception
  {
    if (result instanceof CA1EventUpdatedRemoved receiveRemoved) {
      this.removed = receiveRemoved;
      return;
    }
    if (result instanceof CA1EventUpdatedUpdated receiveUpdated) {
      this.updated = receiveUpdated;
      return;
    }
    throw new IllegalStateException("Unexpected value: " + result);
  }

  @Override
  public Map<BTQualifiedName, BTElementHandlerConstructorType<?, ? extends CA1EventUpdatedSubsetType>>
  onChildHandlersRequested(
    final BTElementParsingContextType context)
  {
    return Map.ofEntries(
      Map.entry(
        element1("Updated"),
        CA1EventUpdatedUpdatedParser::new
      ),
      Map.entry(
        element1("Removed"),
        CA1EventUpdatedRemovedParser::new
      )
    );
  }

  @Override
  public CA1InventoryEventUpdated onElementFinished(
    final BTElementParsingContextType context)
  {
    return new CA1InventoryEventUpdated(
      this.updated.values(),
      this.removed.values()
    );
  }
}
