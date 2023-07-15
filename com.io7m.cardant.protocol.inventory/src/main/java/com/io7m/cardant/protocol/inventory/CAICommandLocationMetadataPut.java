/*
 * Copyright © 2023 Mark Raynsford <code@io7m.com> https://www.io7m.com
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


package com.io7m.cardant.protocol.inventory;

import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CAMetadata;

import java.util.Objects;
import java.util.Set;

/**
 * Add or update metadata values in a location.
 *
 * @param location  The location ID
 * @param metadatas The metadata values
 */

public record CAICommandLocationMetadataPut(
  CALocationID location,
  Set<CAMetadata> metadatas)
  implements CAICommandType<CAIResponseLocationMetadataPut>
{
  /**
   * Add or update metadata values in a location.
   */

  public CAICommandLocationMetadataPut
  {
    Objects.requireNonNull(location, "location");
    Objects.requireNonNull(metadatas, "metadatas");
  }

  @Override
  public Class<CAIResponseLocationMetadataPut> responseClass()
  {
    return CAIResponseLocationMetadataPut.class;
  }
}
