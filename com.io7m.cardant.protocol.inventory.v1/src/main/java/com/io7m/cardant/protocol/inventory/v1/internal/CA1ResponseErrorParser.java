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
import com.io7m.cardant.protocol.inventory.v1.messages.CA1ResponseError;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1ResponseErrorDetail;
import org.xml.sax.Attributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.io7m.cardant.protocol.inventory.v1.CA1InventorySchemas.element1;

/**
 * A parser.
 */

public final class CA1ResponseErrorParser
  implements BTElementHandlerType<CA1ResponseErrorDetail, CA1ResponseError>
{
  private final ArrayList<CA1ResponseErrorDetail> details;
  private CA1ResponseError result;

  /**
   * Create a parser.
   *
   * @param context The parsing context
   */

  public CA1ResponseErrorParser(
    final BTElementParsingContextType context)
  {
    this.details = new ArrayList<>();
  }

  @Override
  public Map<BTQualifiedName, BTElementHandlerConstructorType<?, ? extends CA1ResponseErrorDetail>>
  onChildHandlersRequested(
    final BTElementParsingContextType context)
  {
    return Map.ofEntries(
      Map.entry(
        element1("ResponseErrorDetail"),
        CA1ResponseErrorDetailParser::new
      )
    );
  }

  @Override
  public void onChildValueProduced(
    final BTElementParsingContextType context,
    final CA1ResponseErrorDetail received)
  {
    this.details.add(received);
  }

  @Override
  public void onElementStart(
    final BTElementParsingContextType context,
    final Attributes attributes)
  {
    this.result = new CA1ResponseError(
      Integer.parseInt(attributes.getValue("status")),
      attributes.getValue("message"),
      List.of()
    );
  }

  @Override
  public CA1ResponseError onElementFinished(
    final BTElementParsingContextType context)
  {
    return new CA1ResponseError(
      this.result.status(),
      this.result.message(),
      this.details
    );
  }
}
