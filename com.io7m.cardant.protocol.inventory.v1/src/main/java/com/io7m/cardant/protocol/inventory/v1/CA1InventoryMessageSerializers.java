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

import com.io7m.cardant.model.xml.CAInventorySerializerFactoryType;
import com.io7m.cardant.model.xml.CAInventorySerializers;
import com.io7m.cardant.protocol.inventory.v1.internal.CA1InventoryMessageSerializer;

import java.io.OutputStream;
import java.net.URI;
import java.util.Objects;

/**
 * A provider of 1.0 protocol serializers.
 */

public final class CA1InventoryMessageSerializers
  implements CA1InventoryMessageSerializerFactoryType
{
  private final CAInventorySerializerFactoryType serializers;

  /**
   * A provider of 1.0 protocol serializers.
   *
   * @param inSerializers The inventory serializers
   */

  public CA1InventoryMessageSerializers(
    final CAInventorySerializerFactoryType inSerializers)
  {
    this.serializers =
      Objects.requireNonNull(inSerializers, "serializers");
  }

  /**
   * A provider of 1.0 protocol serializers.
   */

  public CA1InventoryMessageSerializers()
  {
    this(new CAInventorySerializers());
  }

  @Override
  public CA1InventoryMessageSerializerType createSerializerWithContext(
    final Void context,
    final URI target,
    final OutputStream stream)
  {
    return new CA1InventoryMessageSerializer(this.serializers, target, stream);
  }
}
