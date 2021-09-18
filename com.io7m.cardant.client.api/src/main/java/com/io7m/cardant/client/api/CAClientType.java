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

package com.io7m.cardant.client.api;

import com.io7m.cardant.model.CAIds;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemAttachmentID;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CAItems;
import com.io7m.cardant.model.CAListLocationBehaviourType;

import java.io.Closeable;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

/**
 * The type of client instances.
 */

public interface CAClientType extends Closeable
{
  /**
   * @return {@code true} if the client is connected
   */

  boolean isConnected();

  /**
   * @return A stream of events
   */

  Flow.Publisher<CAClientEventType> events();

  /**
   * List items on the server.
   *
   * @param locationBehaviour The location behaviour
   *
   * @return An item list
   */

  CompletableFuture<CAClientCommandResultType<CAItems>> itemsList(
    CAListLocationBehaviourType locationBehaviour);

  /**
   * Get an item on the server.
   *
   * @param id The item id
   *
   * @return An item
   */

  CompletableFuture<CAClientCommandResultType<CAItem>> itemGet(
    CAItemID id);

  /**
   * Create an item on the server.
   *
   * @param id   The item id
   * @param name The item name
   *
   * @return An item
   */

  CompletableFuture<CAClientCommandResultType<CAItem>> itemCreate(
    CAItemID id,
    String name);

  /**
   * Delete items on the server.
   *
   * @param items The items
   *
   * @return The deleted items
   */

  CompletableFuture<CAClientCommandResultType<CAIds>> itemsDelete(
    Set<CAItemID> items);

  /**
   * Delete metadata from an item on the server.
   *
   * @param id       The item id
   * @param metadata The metadata names
   *
   * @return An item
   */

  CompletableFuture<CAClientCommandResultType<CAItem>> itemMetadataDelete(
    CAItemID id,
    Set<String> metadata);

  /**
   * Update metadata in an item on the server.
   *
   * @param id            The item id
   * @param itemMetadatas The metadata values
   *
   * @return An item
   */

  CompletableFuture<CAClientCommandResultType<CAItem>> itemMetadataUpdate(
    CAItemID id,
    Set<CAItemMetadata> itemMetadatas);

  /**
   * Delete an attachment from an item on the server.
   *
   * @param id             The item id
   * @param itemAttachment The attachment
   *
   * @return An item
   */

  CompletableFuture<CAClientCommandResultType<CAItem>> itemAttachmentDelete(
    CAItemID id,
    CAItemAttachmentID itemAttachment);
}
