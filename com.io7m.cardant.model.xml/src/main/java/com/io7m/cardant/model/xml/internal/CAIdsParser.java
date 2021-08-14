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
import com.io7m.cardant.model.CAIdType;
import com.io7m.cardant.model.CAIds;
import com.io7m.cardant.model.xml.CAInventoryParsers;

import java.util.HashSet;
import java.util.Map;

/**
 * A parser.
 */

public final class CAIdsParser
  implements BTElementHandlerType<CAIdType, CAIds>
{
  private final HashSet<CAIdType> ids;

  /**
   * Construct a parser.
   *
   * @param context The parse context
   */

  public CAIdsParser(
    final BTElementParsingContextType context)
  {
    this.ids = new HashSet<>();
  }

  @Override
  public Map<BTQualifiedName, BTElementHandlerConstructorType<?, ? extends CAIdType>>
  onChildHandlersRequested(
    final BTElementParsingContextType context)
  {
    return CAInventoryParsers.idParsers();
  }

  @Override
  public void onChildValueProduced(
    final BTElementParsingContextType context,
    final CAIdType result)
    throws Exception
  {
    this.ids.add(result);
  }

  @Override
  public CAIds onElementFinished(
    final BTElementParsingContextType context)
  {
    return new CAIds(this.ids);
  }
}
