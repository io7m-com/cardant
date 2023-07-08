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


package com.io7m.cardant.shell.internal;

import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.lanark.core.RDottedName;
import com.io7m.quarrel.core.QValueConverterType;

/**
 * A value converter for metadata.
 */

public final class CAItemMetadataConverter
  implements QValueConverterType<CAItemMetadata>
{
  /**
   * Construct a converter.
   */

  public CAItemMetadataConverter()
  {

  }

  @Override
  public CAItemMetadata convertFromString(
    final String text)
  {
    final var colon = text.indexOf(':');
    if (colon == -1) {
      throw new IllegalArgumentException("Expected <dotted-name> : text.");
    }

    final var name =
      text.substring(0, colon);
    final var value =
      text.substring(colon);

    return new CAItemMetadata(
      new RDottedName(name),
       value
    );
  }

  @Override
  public String convertToString(
    final CAItemMetadata value)
  {
    return value.value();
  }

  @Override
  public CAItemMetadata exampleValue()
  {
    return new CAItemMetadata(
      new RDottedName("com.io7m.metadata"),
      "Example value."
    );
  }

  @Override
  public String syntax()
  {
    return "<dotted-name> : <text>";
  }

  @Override
  public Class<CAItemMetadata> convertedClass()
  {
    return CAItemMetadata.class;
  }
}
