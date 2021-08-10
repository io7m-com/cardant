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

package com.io7m.cardant.protocol.versioning.internal;

import com.io7m.blackthorne.api.BTElementHandlerConstructorType;
import com.io7m.blackthorne.api.BTElementHandlerType;
import com.io7m.blackthorne.api.BTElementParsingContextType;
import com.io7m.blackthorne.api.BTQualifiedName;
import com.io7m.cardant.protocol.versioning.messages.CAAPI;
import com.io7m.cardant.protocol.versioning.messages.CAVersioningAPIVersioning;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.io7m.cardant.protocol.versioning.CAVersioningSchemas.element;

/**
 * A parser.
 */

public final class CAVersioningAPIVersioningParser
  implements BTElementHandlerType<CAAPI, CAVersioningAPIVersioning>
{
  private final List<CAAPI> apis;

  /**
   * Construct a parser.
   * @param context The parsing context
   */

  public CAVersioningAPIVersioningParser(
    final BTElementParsingContextType context)
  {
    this.apis = new ArrayList<>();
  }

  @Override
  public Map<BTQualifiedName, BTElementHandlerConstructorType<?, ? extends CAAPI>>
  onChildHandlersRequested(
    final BTElementParsingContextType context)
  {
    return Map.ofEntries(
      Map.entry(
        element("API"),
        CAVersioningAPIParser::new
      )
    );
  }

  @Override
  public void onChildValueProduced(
    final BTElementParsingContextType context,
    final CAAPI received)
  {
    this.apis.add(received);
  }

  @Override
  public CAVersioningAPIVersioning onElementFinished(
    final BTElementParsingContextType context)
  {
    return new CAVersioningAPIVersioning(this.apis);
  }
}
