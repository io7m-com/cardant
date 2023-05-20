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

package com.io7m.cardant.client.transfer.vanilla.internal;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * An artificially slow input stream.
 */

public final class CASlowInputStream extends FilterInputStream
{
  private int count;

  /**
   * An artificially slow input stream.
   *
   * @param input The underlying stream
   */

  public CASlowInputStream(
    final InputStream input)
  {
    super(Objects.requireNonNull(input, "in"));
    this.count = 0;
  }

  @Override
  public int read(
    final byte[] b,
    final int off,
    final int len)
    throws IOException
  {
    ++this.count;
    if (this.count % 10_000_000 == 0) {
      return super.read(b, off, Math.min(512, len));
    }
    return super.read(b, off, 0);
  }

  @Override
  public String toString()
  {
    return String.format(
      "[CASlowInputStream 0x%08x]",
      Integer.valueOf(this.hashCode())
    );
  }
}
