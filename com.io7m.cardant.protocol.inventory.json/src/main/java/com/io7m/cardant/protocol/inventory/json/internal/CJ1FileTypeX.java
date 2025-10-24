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

import com.io7m.cardant.model.CAByteArray;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType;

public enum CJ1FileTypeX
  implements CJ1SerialBijectionType<CJ1FileType, CAFileType>
{
  FILE;

  @Override
  public CAFileType toCore(
    final CJ1FileType file)
  {
    return switch (file) {
      case final CJ1FileWithData ff -> {
        yield new CAFileType.CAFileWithData(
          CAFileID.of(ff.id()),
          ff.description(),
          ff.mediaType(),
          ff.hashAlgorithm(),
          ff.hashValue(),
          new CAByteArray(ff.data())
        );
      }
      case final CJ1FileWithoutData ff -> {
        yield new CAFileType.CAFileWithoutData(
          CAFileID.of(ff.id()),
          ff.description(),
          ff.mediaType(),
          ff.size(),
          ff.hashAlgorithm(),
          ff.hashValue()
        );
      }
    };
  }

  @Override
  public CJ1FileType toCJ1(
    final CAFileType file)
  {
    return switch (file) {
      case final CAFileType.CAFileWithData ff -> {
        yield serializeFileWithData(ff);
      }
      case final CAFileType.CAFileWithoutData ff -> {
        yield serializeFileWithoutData(ff);
      }
    };
  }

  private static CJ1FileWithoutData serializeFileWithoutData(
    final CAFileType.CAFileWithoutData ff)
  {
    return new CJ1FileWithoutData(
      ff.id().id(),
      ff.description(),
      ff.mediaType(),
      ff.size(),
      ff.hashAlgorithm(),
      ff.hashValue()
    );
  }

  private static CJ1FileWithData serializeFileWithData(
    final CAFileType.CAFileWithData ff)
  {
    return new CJ1FileWithData(
      ff.id().id(),
      ff.description(),
      ff.mediaType(),
      ff.hashAlgorithm(),
      ff.hashValue(),
      ff.data().data()
    );
  }
}
