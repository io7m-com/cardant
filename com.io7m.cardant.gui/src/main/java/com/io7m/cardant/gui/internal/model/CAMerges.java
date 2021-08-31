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

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class CAMerges
{
  private CAMerges()
  {

  }

  public static <K, VS, VI> void merge(
    final Map<K, VS> target,
    final Map<K, VI> source,
    final Function<VI, VS> creator,
    final BiConsumer<VS, VI> updater)
  {
    Objects.requireNonNull(target, "target");
    Objects.requireNonNull(source, "source");
    Objects.requireNonNull(creator, "creator");
    Objects.requireNonNull(updater, "updater");

    /*
     * The additions to the map are all keys present in the source map
     * that aren't present in the target map.
     */

    final var additions = new HashSet<>(source.keySet());
    additions.removeAll(target.keySet());

    /*
     * The removals from the map are all keys present in the target map
     * that aren't present in the source map.
     */

    final var removals = new HashSet<>(target.keySet());
    removals.removeAll(source.keySet());

    /*
     * The updates to the map are all keys that are present in both the source
     * and target map.
     */

    final var updates = new HashSet<>(target.keySet());
    updates.retainAll(source.keySet());

    for (final var addition : additions) {
      target.put(addition, creator.apply(source.get(addition)));
    }

    for (final var update : updates) {
      updater.accept(target.get(update), source.get(update));
    }

    for (final var removal : removals) {
      target.remove(removal);
    }
  }
}
