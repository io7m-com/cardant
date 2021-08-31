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

package com.io7m.cardant.gui.internal.model;

import javafx.beans.value.ObservableValue;

import java.util.UUID;

import static java.util.Locale.ROOT;

public final class CAStringSearch
{
  private CAStringSearch()
  {

  }

  public static boolean containsIgnoreCase(
    final String a,
    final String b)
  {
    return a.toUpperCase(ROOT)
      .contains(b.toUpperCase(ROOT));
  }

  public static boolean containsIgnoreCase(
    final ObservableValue<String> a,
    final String b)
  {
    return containsIgnoreCase(a.getValue(), b);
  }

  public static boolean containsIgnoreCase(
    final UUID a,
    final String b)
  {
    return containsIgnoreCase(a.toString().toUpperCase(ROOT), b);
  }
}
