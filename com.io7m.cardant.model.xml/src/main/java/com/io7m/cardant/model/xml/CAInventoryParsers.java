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

package com.io7m.cardant.model.xml;

import com.io7m.anethum.common.ParseStatus;
import com.io7m.blackthorne.api.BTElementHandlerConstructorType;
import com.io7m.blackthorne.api.BTQualifiedName;
import com.io7m.cardant.model.CAInventoryElementType;
import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CATags;
import com.io7m.cardant.model.xml.internal.CAInventoryParser;
import com.io7m.cardant.model.xml.internal.CAItemsParser;
import com.io7m.cardant.model.xml.internal.CATagParser;
import com.io7m.cardant.model.xml.internal.CATagsParser;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.function.Consumer;

import static com.io7m.cardant.model.xml.CAInventorySchemas.element1;

/**
 * A provider of inventory parsers.
 */

public final class CAInventoryParsers
  implements CAInventoryParserFactoryType
{
  /**
   * The root element parsers.
   */

  private static final Map<BTQualifiedName, BTElementHandlerConstructorType<?, CAInventoryElementType>> ROOT_ELEMENTS =
    makeRootElements();

  /**
   * A provider of inventory parsers.
   */

  public CAInventoryParsers()
  {

  }

  private static Map<BTQualifiedName, BTElementHandlerConstructorType<?, CAInventoryElementType>> makeRootElements()
  {
    return Map.ofEntries(
      Map.entry(
        element1("Items"),
        CAItemsParser::new
      ),
      Map.entry(
        element1("Tags"),
        CATagsParser::new
      ),
      Map.entry(
        element1("Tag"),
        CATagParser::new
      )
    );
  }

  /**
   * @return The root element parsers
   */

  public static Map<BTQualifiedName, BTElementHandlerConstructorType<?, CAInventoryElementType>> rootElementParsers()
  {
    return ROOT_ELEMENTS;
  }

  /**
   * @return The tag parsers
   */

  public static BTElementHandlerConstructorType<?, CATag> tagParser()
  {
    return CATagParser::new;
  }

  /**
   * @return The tags parsers
   */

  public static BTElementHandlerConstructorType<?, CATags> tagsParser()
  {
    return CATagsParser::new;
  }

  @Override
  public CAInventoryParserType createParserWithContext(
    final Void context,
    final URI source,
    final InputStream stream,
    final Consumer<ParseStatus> statusConsumer)
  {
    return new CAInventoryParser(source, stream, statusConsumer);
  }
}
