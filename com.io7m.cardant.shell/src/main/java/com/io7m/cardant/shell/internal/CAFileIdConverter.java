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

import com.io7m.cardant.model.CAFileID;
import com.io7m.quarrel.core.QValueConverterType;

import java.util.UUID;

/**
 * A value converter for file IDs.
 */

public final class CAFileIdConverter
  implements QValueConverterType<CAFileID>
{
  /**
   * Construct a converter.
   */

  public CAFileIdConverter()
  {

  }

  @Override
  public CAFileID convertFromString(
    final String text)
  {
    return new CAFileID(UUID.fromString(text));
  }

  @Override
  public String convertToString(
    final CAFileID value)
  {
    return value.displayId();
  }

  @Override
  public CAFileID exampleValue()
  {
    return new CAFileID(UUID.randomUUID());
  }

  @Override
  public String syntax()
  {
    return "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
  }

  @Override
  public Class<CAFileID> convertedClass()
  {
    return CAFileID.class;
  }
}
