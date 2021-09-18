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
import com.io7m.cardant.model.CAItemLocations;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CAItems;
import com.io7m.cardant.model.CAListLocationBehaviourType;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocations;

import java.io.Closeable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

public interface CAClientType extends Closeable
{
  boolean isConnected();

  Flow.Publisher<CAClientEventType> events();

  CompletableFuture<CAClientCommandResultType<CAItems>> itemsList(
    CAListLocationBehaviourType locationBehaviour);

  CompletableFuture<CAClientCommandResultType<CAItem>> itemGet(
    CAItemID id);

  CompletableFuture<CAClientCommandResultType<CAItem>> itemCreate(
    CAItemID id,
    String name);

  CompletableFuture<CAClientCommandResultType<CAIds>> itemsDelete(
    Set<CAItemID> items);

  CompletableFuture<CAClientCommandResultType<CAItem>> itemMetadataDelete(
    CAItemID id,
    Set<String> metadata);

  CompletableFuture<CAClientCommandResultType<CAItem>> itemMetadataUpdate(
    CAItemID id,
    Set<CAItemMetadata> itemMetadatas);

  CompletableFuture<CAClientCommandResultType<CAItem>> itemAttachmentDelete(
    CAItemID id,
    CAItemAttachmentID itemAttachment);
}
