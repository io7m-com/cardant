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

package com.io7m.cardant.tests;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

final class SortedMapEntryCollector<K, V>
  implements Collector<Map.Entry<K, V>, SortedMap<K, V>, SortedMap<K, V>>
{
  @Override
  public Supplier<SortedMap<K, V>> supplier()
  {
    return TreeMap::new;
  }

  @Override
  public BiConsumer<SortedMap<K, V>, Map.Entry<K, V>> accumulator()
  {
    return (map, entry) -> map.put(entry.getKey(), entry.getValue());
  }

  @Override
  public BinaryOperator<SortedMap<K, V>> combiner()
  {
    return (mapA, mapB) -> {
      final var map = new TreeMap<K, V>();
      map.putAll(mapA);
      map.putAll(mapB);
      return map;
    };
  }

  @Override
  public Function<SortedMap<K, V>, SortedMap<K, V>> finisher()
  {
    return Function.identity();
  }

  @Override
  public Set<Characteristics> characteristics()
  {
    return Set.of();
  }
}
