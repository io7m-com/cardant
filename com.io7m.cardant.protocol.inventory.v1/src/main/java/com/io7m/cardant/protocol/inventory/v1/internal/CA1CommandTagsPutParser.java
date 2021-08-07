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
import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CATags;
import com.io7m.cardant.model.xml.CAInventoryParsers;
import com.io7m.cardant.model.xml.CAInventorySchemas;
import com.io7m.cardant.protocol.inventory.v1.CA1CommandTagsPut;

import java.util.Map;
import java.util.TreeSet;

/**
 * A parser.
 */

public final class CA1CommandTagsPutParser
  implements BTElementHandlerType<Object, CA1CommandTagsPut>
{
  private final TreeSet<CATag> tags;

  /**
   * Create a parser.
   *
   * @param context The parsing context
   */

  public CA1CommandTagsPutParser(
    final BTElementParsingContextType context)
  {
    this.tags = new TreeSet<>();
  }

  @Override
  public Map<BTQualifiedName, BTElementHandlerConstructorType<?, ?>> onChildHandlersRequested(
    final BTElementParsingContextType context)
  {
    return Map.ofEntries(
      Map.entry(
        CAInventorySchemas.element1("Tag"),
        CAInventoryParsers.tagParser()
      ),
      Map.entry(
        CAInventorySchemas.element1("Tags"),
        CAInventoryParsers.tagsParser()
      )
    );
  }

  @Override
  public void onChildValueProduced(
    final BTElementParsingContextType context,
    final Object value)
  {
    if (value instanceof CATag received) {
      this.tags.add(received);
      return;
    }
    if (value instanceof CATags received) {
      this.tags.addAll(received.tags());
      return;
    }
    throw new IllegalStateException("Unexpected value: %s".formatted(value));
  }

  @Override
  public CA1CommandTagsPut onElementFinished(
    final BTElementParsingContextType context)
  {
    return new CA1CommandTagsPut(new CATags(this.tags));
  }
}
