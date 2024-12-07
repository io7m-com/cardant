/*
 * Copyright © 2024 Mark Raynsford <code@io7m.com> https://www.io7m.com
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


package com.io7m.cardant.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The full path of a location.
 *
 * @param path The path
 */

public record CALocationPath(
  List<CALocationName> path)
{
  /**
   * The full path of a location.
   *
   * @param path The path
   */

  public CALocationPath
  {
    if (path.isEmpty()) {
      throw new IllegalArgumentException("Location path cannot be empty.");
    }
    path = List.copyOf(path);
  }

  @Override
  public String toString()
  {
    return this.path.stream()
      .map(CALocationName::value)
      .collect(Collectors.joining("/"));
  }

  /**
   * @return The location name (the last path component)
   */

  public CALocationName last()
  {
    return this.path.getLast();
  }

  /**
   * Replace the last path element with the given name.
   *
   * @param name The name
   *
   * @return The new path
   */

  public CALocationPath withNewName(
    final CALocationName name)
  {
    Objects.requireNonNull(name, "name");

    final var existing = new ArrayList<>(this.path);
    existing.removeLast();
    existing.add(name);
    return new CALocationPath(existing);
  }

  /**
   * Replace the last path element with the given name.
   *
   * @param name The name
   *
   * @return The new path
   */

  public CALocationPath withNewName(
    final String name)
  {
    return this.withNewName(new CALocationName(name));
  }

  /**
   * Build a location path from the given array.
   *
   * @param array The array
   *
   * @return The path
   */

  public static CALocationPath ofArray(
    final String[] array)
  {
    return new CALocationPath(
      Stream.of(array)
        .map(CALocationName::new)
        .toList()
    );
  }

  /**
   * A singleton path.
   *
   * @param name The name
   *
   * @return The path
   */

  public static CALocationPath singleton(
    final CALocationName name)
  {
    return new CALocationPath(List.of(name));
  }

  /**
   * A singleton path.
   *
   * @param name The name
   *
   * @return The path
   */

  public static CALocationPath singleton(
    final String name)
  {
    return singleton(new CALocationName(name));
  }
}
