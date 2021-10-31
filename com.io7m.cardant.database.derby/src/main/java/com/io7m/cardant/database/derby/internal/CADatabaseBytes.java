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

package com.io7m.cardant.database.derby.internal;

import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CATagID;

import java.nio.ByteBuffer;
import java.util.UUID;

import static java.nio.ByteOrder.BIG_ENDIAN;

/**
 * Functions for converting between values and raw bytes.
 */

public final class CADatabaseBytes
{
  private CADatabaseBytes()
  {

  }

  static byte[] uuidBytes(
    final UUID id)
  {
    final var buffer = ByteBuffer.allocate(16).order(BIG_ENDIAN);
    buffer.putLong(0, id.getMostSignificantBits());
    buffer.putLong(8, id.getLeastSignificantBits());
    return buffer.array();
  }

  static UUID uuidFromBytes(
    final byte[] bytes)
  {
    final var buffer = ByteBuffer.wrap(bytes).order(BIG_ENDIAN);
    final var msb = buffer.getLong(0);
    final var lsb = buffer.getLong(8);
    return new UUID(msb, lsb);
  }

  static CALocationID locationIdFromBytes(
    final byte[] bytes)
  {
    return new CALocationID(uuidFromBytes(bytes));
  }

  static byte[] locationIdBytes(
    final CALocationID id)
  {
    return uuidBytes(id.id());
  }

  static CAItemID itemIdFromBytes(
    final byte[] bytes)
  {
    return new CAItemID(uuidFromBytes(bytes));
  }

  static byte[] itemIdBytes(
    final CAItemID id)
  {
    return uuidBytes(id.id());
  }

  static byte[] tagIdBytes(
    final CATagID id)
  {
    return uuidBytes(id.id());
  }

  static CATagID tagIdFromBytes(
    final byte[] bytes)
  {
    return new CATagID(uuidFromBytes(bytes));
  }

  static CAFileID fileIdFromBytes(
    final byte[] bytes)
  {
    return new CAFileID(uuidFromBytes(bytes));
  }

  static byte[] fileIdBytes(
    final CAFileID id)
  {
    return uuidBytes(id.id());
  }
}
