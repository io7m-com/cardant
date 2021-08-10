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

package com.io7m.cardant.protocol.inventory.v1;

import com.io7m.anethum.common.ParseStatus;
import com.io7m.blackthorne.api.BTElementHandlerConstructorType;
import com.io7m.blackthorne.api.BTQualifiedName;
import com.io7m.cardant.protocol.inventory.v1.internal.CA1CommandItemAttachmentPutParser;
import com.io7m.cardant.protocol.inventory.v1.internal.CA1CommandItemAttachmentRemoveParser;
import com.io7m.cardant.protocol.inventory.v1.internal.CA1CommandItemCreateParser;
import com.io7m.cardant.protocol.inventory.v1.internal.CA1CommandItemListParser;
import com.io7m.cardant.protocol.inventory.v1.internal.CA1CommandItemMetadataPutParser;
import com.io7m.cardant.protocol.inventory.v1.internal.CA1CommandItemMetadataRemoveParser;
import com.io7m.cardant.protocol.inventory.v1.internal.CA1CommandItemRemoveParser;
import com.io7m.cardant.protocol.inventory.v1.internal.CA1CommandItemUpdateParser;
import com.io7m.cardant.protocol.inventory.v1.internal.CA1CommandLoginUsernamePasswordParser;
import com.io7m.cardant.protocol.inventory.v1.internal.CA1CommandTagListParser;
import com.io7m.cardant.protocol.inventory.v1.internal.CA1CommandTagsDeleteParser;
import com.io7m.cardant.protocol.inventory.v1.internal.CA1CommandTagsPutParser;
import com.io7m.cardant.protocol.inventory.v1.internal.CA1InventoryMessageParser;
import com.io7m.cardant.protocol.inventory.v1.internal.CA1ResponseErrorParser;
import com.io7m.cardant.protocol.inventory.v1.internal.CA1ResponseOKParser;
import com.io7m.cardant.protocol.inventory.v1.internal.CA1TransactionParser;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1InventoryCommandType;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1InventoryMessageType;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1InventoryResponseType;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.io7m.cardant.protocol.inventory.v1.CA1InventorySchemas.element1;

/**
 * A provider of 1.0 protocol parsers.
 */

public final class CA1InventoryMessageParsers
  implements CA1InventoryMessageParserFactoryType
{
  /**
   * A provider of 1.0 protocol parsers.
   */

  public CA1InventoryMessageParsers()
  {

  }

  /**
   * @return The set of parsers for all messages (with a sharper type)
   */

  public static Map<BTQualifiedName,
    BTElementHandlerConstructorType<?, CA1InventoryMessageType>> allParsersSharp()
  {
    return (Map<BTQualifiedName, BTElementHandlerConstructorType<?, CA1InventoryMessageType>>) (Object) allParsers();
  }

  /**
   * @return The set of parsers for all messages
   */

  public static Map<BTQualifiedName,
    BTElementHandlerConstructorType<?, ? extends CA1InventoryMessageType>> allParsers()
  {
    final Map<BTQualifiedName,
      BTElementHandlerConstructorType<?, ? extends CA1InventoryMessageType>> result = new HashMap<>();
    result.putAll(responseParsers());
    result.putAll(commandParsers());

    result.put(
      element1("Transaction"),
      CA1TransactionParser::new
    );
    return result;
  }

  /**
   * @return The set of parsers for responses
   */

  public static Map<BTQualifiedName,
    BTElementHandlerConstructorType<?, ? extends CA1InventoryResponseType>> responseParsers()
  {
    return Map.ofEntries(
      Map.entry(
        element1("ResponseError"),
        CA1ResponseErrorParser::new
      ),
      Map.entry(
        element1("ResponseOK"),
        CA1ResponseOKParser::new
      )
    );
  }

  /**
   * @return The set of parsers for commands
   */

  public static Map<BTQualifiedName,
    BTElementHandlerConstructorType<?, ? extends CA1InventoryCommandType>> commandParsers()
  {
    return Map.ofEntries(
      Map.entry(
        element1("CommandLoginUsernamePassword"),
        CA1CommandLoginUsernamePasswordParser::new
      ),
      Map.entry(
        element1("CommandTagList"),
        CA1CommandTagListParser::new
      ),
      Map.entry(
        element1("CommandTagsPut"),
        CA1CommandTagsPutParser::new
      ),
      Map.entry(
        element1("CommandTagsDelete"),
        CA1CommandTagsDeleteParser::new
      ),
      Map.entry(
        element1("CommandItemCreate"),
        CA1CommandItemCreateParser::new
      ),
      Map.entry(
        element1("CommandItemUpdate"),
        CA1CommandItemUpdateParser::new
      ),
      Map.entry(
        element1("CommandItemRemove"),
        CA1CommandItemRemoveParser::new
      ),
      Map.entry(
        element1("CommandItemAttachmentPut"),
        CA1CommandItemAttachmentPutParser::new
      ),
      Map.entry(
        element1("CommandItemAttachmentRemove"),
        CA1CommandItemAttachmentRemoveParser::new
      ),
      Map.entry(
        element1("CommandItemMetadataPut"),
        CA1CommandItemMetadataPutParser::new
      ),
      Map.entry(
        element1("CommandItemMetadataRemove"),
        CA1CommandItemMetadataRemoveParser::new
      ),
      Map.entry(
        element1("CommandItemList"),
        CA1CommandItemListParser::new
      )
    );
  }

  @Override
  public CA1InventoryMessageParserType createParserWithContext(
    final Void context,
    final URI source,
    final InputStream stream,
    final Consumer<ParseStatus> statusConsumer)
  {
    return new CA1InventoryMessageParser(source, stream);
  }
}
