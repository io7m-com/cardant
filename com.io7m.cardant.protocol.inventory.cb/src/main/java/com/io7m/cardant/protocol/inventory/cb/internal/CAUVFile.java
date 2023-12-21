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


package com.io7m.cardant.protocol.inventory.cb.internal;

import com.io7m.cardant.model.CAByteArray;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1File;
import com.io7m.cedarbridge.runtime.api.CBByteArray;
import com.io7m.cedarbridge.runtime.api.CBUUID;

import java.nio.ByteBuffer;

import static com.io7m.cedarbridge.runtime.api.CBCore.string;
import static com.io7m.cedarbridge.runtime.api.CBCore.unsigned64;

/**
 * A validator.
 */

public enum CAUVFile
  implements CAProtocolMessageValidatorType<CAFileType, CAI1File>
{
  /**
   * A validator.
   */

  FILE;

  @Override
  public CAI1File convertToWire(
    final CAFileType message)
  {
    return switch (message) {
      case final CAFileType.CAFileWithData f -> {
        yield new CAI1File.CAI1FileWithData(
          new CBUUID(f.id().id()),
          string(f.description()),
          string(f.mediaType()),
          unsigned64(f.size()),
          string(f.hashAlgorithm()),
          string(f.hashValue()),
          new CBByteArray(ByteBuffer.wrap(f.data().data()))
        );
      }
      case final CAFileType.CAFileWithoutData f -> {
        yield new CAI1File.CAI1FileWithoutData(
          new CBUUID(f.id().id()),
          string(f.description()),
          string(f.mediaType()),
          unsigned64(f.size()),
          string(f.hashAlgorithm()),
          string(f.hashValue())
        );
      }
    };
  }

  @Override
  public CAFileType convertFromWire(
    final CAI1File message)
  {
    return switch (message) {
      case final CAI1File.CAI1FileWithData with -> {
        yield new CAFileType.CAFileWithData(
          new CAFileID(with.fieldId().value()),
          with.fieldDescription().value(),
          with.fieldMediaType().value(),
          with.fieldHashAlgorithm().value(),
          with.fieldHashValue().value(),
          byteArray(with.fieldData())
        );
      }
      case final CAI1File.CAI1FileWithoutData with -> {
        yield new CAFileType.CAFileWithoutData(
          new CAFileID(with.fieldId().value()),
          with.fieldDescription().value(),
          with.fieldMediaType().value(),
          with.fieldSize().value(),
          with.fieldHashAlgorithm().value(),
          with.fieldHashValue().value()
        );
      }
    };
  }

  private static CAByteArray byteArray(
    final CBByteArray b)
  {
    final var buf = b.value();
    final var d = new byte[buf.capacity()];
    buf.get(d);
    return new CAByteArray(d);
  }
}
