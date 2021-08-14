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
import com.io7m.cardant.model.CAItemAttachment;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemMetadatas;
import com.io7m.cardant.model.CAItemRepositAdd;
import com.io7m.cardant.model.CAItemRepositMove;
import com.io7m.cardant.model.CAItemRepositRemove;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CATags;
import com.io7m.cardant.model.xml.internal.CAInventoryParser;
import com.io7m.cardant.model.xml.internal.CAItemAttachmentParser;
import com.io7m.cardant.model.xml.internal.CAItemMetadatasParser;
import com.io7m.cardant.model.xml.internal.CAItemParser;
import com.io7m.cardant.model.xml.internal.CAItemRepositAddParser;
import com.io7m.cardant.model.xml.internal.CAItemRepositMoveParser;
import com.io7m.cardant.model.xml.internal.CAItemRepositRemoveParser;
import com.io7m.cardant.model.xml.internal.CAItemsParser;
import com.io7m.cardant.model.xml.internal.CALocationParser;
import com.io7m.cardant.model.xml.internal.CALocationsParser;
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
        element1("Item"),
        CAItemParser::new
      ),
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
      ),
      Map.entry(
        element1("Locations"),
        CALocationsParser::new
      ),
      Map.entry(
        element1("Location"),
        CALocationParser::new
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

  /**
   * @param itemID The item that will own the attachment
   *
   * @return An item attachment parser
   */

  public static BTElementHandlerConstructorType<?, CAItemAttachment>
  itemAttachmentParser(
    final CAItemID itemID)
  {
    return context -> new CAItemAttachmentParser(itemID, context);
  }

  /**
   * @param itemId The item that will own the metadatas
   *
   * @return An item metadatas parser
   */

  public static BTElementHandlerConstructorType<?, CAItemMetadatas>
  itemMetadatasParser(
    final CAItemID itemId)
  {
    return context -> new CAItemMetadatasParser(itemId, context);
  }

  /**
   * @return The location parsers
   */

  public static BTElementHandlerConstructorType<?, CALocation> locationParser()
  {
    return CALocationParser::new;
  }

  /**
   * @return An item reposit parser
   */

  public static BTElementHandlerConstructorType<?, CAItemRepositAdd>
  itemRepositAddParser()
  {
    return CAItemRepositAddParser::new;
  }

  /**
   * @return An item reposit parser
   */

  public static BTElementHandlerConstructorType<?, CAItemRepositRemove>
  itemRepositRemoveParser()
  {
    return CAItemRepositRemoveParser::new;
  }

  /**
   * @return An item reposit parser
   */

  public static BTElementHandlerConstructorType<?, CAItemRepositMove>
  itemRepositMoveParser()
  {
    return CAItemRepositMoveParser::new;
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
