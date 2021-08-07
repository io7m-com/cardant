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

import com.io7m.blackthorne.api.BTElementHandlerType;
import com.io7m.blackthorne.api.BTElementParsingContextType;
import com.io7m.cardant.protocol.inventory.v1.CA1CommandLoginUsernamePassword;
import org.xml.sax.Attributes;

/**
 * A parser.
 */

public final class CA1CommandLoginUsernamePasswordParser
  implements BTElementHandlerType<Object, CA1CommandLoginUsernamePassword>
{
  private CA1CommandLoginUsernamePassword result;

  /**
   * Create a parser.
   *
   * @param context The parsing context
   */

  public CA1CommandLoginUsernamePasswordParser(
    final BTElementParsingContextType context)
  {

  }

  @Override
  public void onElementStart(
    final BTElementParsingContextType context,
    final Attributes attributes)
  {
    this.result = new CA1CommandLoginUsernamePassword(
      attributes.getValue("user"),
      attributes.getValue("password")
    );
  }

  @Override
  public CA1CommandLoginUsernamePassword onElementFinished(
    final BTElementParsingContextType context)
  {
    return this.result;
  }
}
