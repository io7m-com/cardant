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

package com.io7m.cardant.model;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

/**
 * The type of files.
 */

public sealed interface CAFileType extends Comparable<CAFileType>
{
  /**
   * @return The ID
   */

  CAFileID id();

  /**
   * @return The description
   */

  String description();

  /**
   * @return The media type
   */

  String mediaType();

  /**
   * @return The size in bytes
   */

  long size();

  /**
   * @return The hash algorithm
   */

  String hashAlgorithm();

  /**
   * @return The hash value
   */

  String hashValue();

  /**
   * @return The data, if any
   */

  Optional<CAByteArray> dataOptional();

  /**
   * @return The file without data
   */

  CAFileWithoutData withoutData();

  /**
   * A file without data.
   *
   * @param id            The ID
   * @param description   The description
   * @param mediaType     The media type
   * @param size          The size in bytes
   * @param hashAlgorithm The hash algorithm
   * @param hashValue     The hash value
   */

  record CAFileWithoutData(
    CAFileID id,
    String description,
    String mediaType,
    long size,
    String hashAlgorithm,
    String hashValue)
    implements CAFileType
  {
    /**
     * A file without data.
     */

    public CAFileWithoutData
    {
      Objects.requireNonNull(id, "id");
      Objects.requireNonNull(description, "description");
      Objects.requireNonNull(mediaType, "mediaType");
      Objects.requireNonNull(hashAlgorithm, "hashAlgorithm");
      Objects.requireNonNull(hashValue, "hashValue");
    }

    @Override
    public Optional<CAByteArray> dataOptional()
    {
      return Optional.empty();
    }

    @Override
    public CAFileWithoutData withoutData()
    {
      return this;
    }
  }

  /**
   * A file with data.
   *
   * @param id            The ID
   * @param description   The description
   * @param mediaType     The media type
   * @param size          The size in bytes
   * @param hashAlgorithm The hash algorithm
   * @param hashValue     The hash value
   * @param data          The data
   */

  record CAFileWithData(
    CAFileID id,
    String description,
    String mediaType,
    long size,
    String hashAlgorithm,
    String hashValue,
    CAByteArray data)
    implements CAFileType
  {
    /**
     * A file with data.
     */

    public CAFileWithData
    {
      Objects.requireNonNull(id, "id");
      Objects.requireNonNull(description, "description");
      Objects.requireNonNull(mediaType, "mediaType");
      Objects.requireNonNull(hashAlgorithm, "hashAlgorithm");
      Objects.requireNonNull(hashValue, "hashValue");
      Objects.requireNonNull(data, "data");

      final var dataArray = data.data();
      if (dataArray.length != size) {
        throw new IllegalArgumentException(
          new StringBuilder(64)
            .append("Data length ")
            .append(dataArray.length)
            .append(" != size ")
            .append(size)
            .toString()
        );
      }
    }

    @Override
    public Optional<CAByteArray> dataOptional()
    {
      return Optional.of(this.data);
    }

    @Override
    public CAFileWithoutData withoutData()
    {
      return new CAFileWithoutData(
        this.id,
        this.description,
        this.mediaType,
        this.size,
        this.hashAlgorithm,
        this.hashValue);
    }
  }

  @Override
  default int compareTo(
    final CAFileType other)
  {
    return Comparator.comparing(CAFileType::id).compare(this, other);
  }
}
