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
import com.io7m.cardant.model.CAItems;

import java.util.HashSet;
import java.util.Map;

import static com.io7m.cardant.model.xml.CAInventorySchemas.element1;
import static java.util.Map.entry;

/**
 * A parser.
 */

public final class CAItemsParser
  implements BTElementHandlerType<CAItem, CAItems>
{
  private final HashSet<CAItem> results;

  /**
   * Construct a parser.
   *
   * @param context The parse context
   */

  public CAItemsParser(
    final BTElementParsingContextType context)
  {
    this.results = new HashSet<>();
  }

  @Override
  public Map<BTQualifiedName, BTElementHandlerConstructorType<?, ? extends CAItem>>
  onChildHandlersRequested(final BTElementParsingContextType context)
  {
    return Map.ofEntries(
      entry(element1("Item"), CAItemParser::new)
    );
  }

  @Override
  public void onChildValueProduced(
    final BTElementParsingContextType context,
    final CAItem result)
  {
    this.results.add(result);
  }

  @Override
  public CAItems onElementFinished(
    final BTElementParsingContextType context)
  {
    return new CAItems(this.results);
  }
}
