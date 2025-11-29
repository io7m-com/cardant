/*
 * Copyright © 2025 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.protocol.inventory.json.internal;

import com.io7m.lanark.core.RDottedName;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.KeyDeserializer;

public final class CJ1TypePackageIdentifierKeyDeserializer
  extends KeyDeserializer
{
  public CJ1TypePackageIdentifierKeyDeserializer()
  {

  }

  @Override
  public Object deserializeKey(
    final String text,
    final DeserializationContext deserializationContext)
  {
    final var segments = text.split("\\w+");
    if (segments.length != 2) {
      throw new IllegalArgumentException("Unparseable package identifier.");
    }
    return new CJ1TypePackageIdentifier(
      new RDottedName(segments[0]),
      segments[1]
    );
  }
}
