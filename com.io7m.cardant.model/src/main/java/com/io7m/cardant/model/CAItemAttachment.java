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

package com.io7m.cardant.model;

import java.util.Objects;
import java.util.Optional;

/**
 * An item attachment.
 *
 * @param id            The ID
 * @param itemId        The item ID
 * @param description   The attachment description
 * @param mediaType     The attachment media type
 * @param relation      The attachment relation
 * @param size          The attachment size in bytes
 * @param hashAlgorithm The attachment hash algorithm
 * @param hashValue     The attachment hash value
 * @param data          The attachment data
 */

public record CAItemAttachment(
  CAItemAttachmentID id,
  CAItemID itemId,
  String description,
  String mediaType,
  String relation,
  long size,
  String hashAlgorithm,
  String hashValue,
  Optional<CAByteArray> data
) implements CAInventoryElementType
{
  /**
   * An item attachment.
   *
   * @param id            The ID
   * @param itemId        The item ID
   * @param description   The attachment description
   * @param mediaType     The attachment media type
   * @param relation      The attachment relation
   * @param size          The attachment size in bytes
   * @param hashAlgorithm The attachment hash algorithm
   * @param hashValue     The attachment hash value
   * @param data          The attachment data
   */

  public CAItemAttachment
  {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(itemId, "itemId");
    Objects.requireNonNull(description, "description");
    Objects.requireNonNull(mediaType, "mediaType");
    Objects.requireNonNull(relation, "relation");
    Objects.requireNonNull(hashAlgorithm, "hashAlgorithm");
    Objects.requireNonNull(hashValue, "hashValue");
    Objects.requireNonNull(data, "data");

    if (data.isPresent()) {
      final var dataArray = data.get();
      if (dataArray.data().length != size) {
        throw new IllegalArgumentException(
          new StringBuilder(64)
            .append("Data length ")
            .append(dataArray.data().length)
            .append(" != size ")
            .append(size)
            .toString()
        );
      }
    }
  }

  /**
   * @return This attachment without any associated data
   */

  public CAItemAttachment withoutData()
  {
    return new CAItemAttachment(
      this.id,
      this.itemId,
      this.description,
      this.mediaType,
      this.relation,
      this.size,
      this.hashAlgorithm,
      this.hashValue,
      Optional.empty()
    );
  }
}
