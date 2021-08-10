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

package com.io7m.cardant.protocol.versioning;

import com.io7m.anethum.common.ParseStatus;
import com.io7m.blackthorne.api.BTElementHandlerConstructorType;
import com.io7m.blackthorne.api.BTQualifiedName;
import com.io7m.cardant.protocol.versioning.internal.CAVersioningAPIVersioningParser;
import com.io7m.cardant.protocol.versioning.internal.CAVersioningMessageParser;
import com.io7m.cardant.protocol.versioning.messages.CAVersioningMessageType;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.function.Consumer;

import static com.io7m.cardant.protocol.versioning.CAVersioningSchemas.element;

/**
 * A provider of 1.0 protocol parsers.
 */

public final class CAVersioningMessageParsers
  implements CAVersioningMessageParserFactoryType
{
  /**
   * A provider of 1.0 protocol parsers.
   */

  public CAVersioningMessageParsers()
  {

  }

  /**
   * @return The available message parsers
   */

  public static Map<BTQualifiedName,
    BTElementHandlerConstructorType<?, CAVersioningMessageType>> allParsersSharp()
  {
    return (Map<BTQualifiedName, BTElementHandlerConstructorType<?, CAVersioningMessageType>>) (Object) allParsers();
  }

  /**
   * @return The available message parsers
   */

  public static Map<BTQualifiedName,
    BTElementHandlerConstructorType<?, ? extends CAVersioningMessageType>> allParsers()
  {
    return Map.ofEntries(
      Map.entry(
        element("APIVersioning"),
        CAVersioningAPIVersioningParser::new
      )
    );
  }

  @Override
  public CAVersioningMessageParserType createParserWithContext(
    final Void context,
    final URI source,
    final InputStream stream,
    final Consumer<ParseStatus> statusConsumer)
  {
    return new CAVersioningMessageParser(source, stream);
  }
}
