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

package com.io7m.cardant.protocol.inventory.json.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

/**
 * The type of files.
 */

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "@type"
)
@JsonSubTypes({
  @JsonSubTypes.Type(value = CJ1FileType.CJ1FileWithoutData.class, name = "FileWithoutData"),
  @JsonSubTypes.Type(value = CJ1FileType.CJ1FileWithData.class, name = "FileWithData")
})
public sealed interface CJ1FileType
  extends Comparable<CJ1FileType>, CJ1ValueType
{
  UUID id();

  String description();

  String mediaType();

  long size();

  String hashAlgorithm();

  String hashValue();

  Optional<byte[]> dataOptional();

  CJ1FileWithoutData withoutData();

  @JsonDeserialize
  @JsonSerialize
  @JsonTypeName("FileWithoutData")
  record CJ1FileWithoutData(
    @JsonProperty(value = "id", required = true)
    UUID id,
    @JsonProperty(value = "description", required = true)
    String description,
    @JsonProperty(value = "mediaType", required = true)
    String mediaType,
    @JsonProperty(value = "size", required = true)
    long size,
    @JsonProperty(value = "hashAlgorithm", required = true)
    String hashAlgorithm,
    @JsonProperty(value = "hashValue", required = true)
    String hashValue)
    implements CJ1FileType
  {
    @Override
    public Optional<byte[]> dataOptional()
    {
      return Optional.empty();
    }

    @Override
    public CJ1FileWithoutData withoutData()
    {
      return this;
    }
  }

  @JsonDeserialize
  @JsonSerialize
  @JsonTypeName("FileWithData")
  record CJ1FileWithData(
    @JsonProperty(value = "id", required = true)
    UUID id,
    @JsonProperty(value = "description", required = true)
    String description,
    @JsonProperty(value = "mediaType", required = true)
    String mediaType,
    @JsonProperty(value = "hashAlgorithm", required = true)
    String hashAlgorithm,
    @JsonProperty(value = "hashValue", required = true)
    String hashValue,
    @JsonProperty(value = "data", required = true)
    byte[] data)
    implements CJ1FileType
  {
    @Override
    public long size()
    {
      return Integer.toUnsignedLong(this.data.length);
    }

    @Override
    public Optional<byte[]> dataOptional()
    {
      return Optional.of(this.data);
    }

    @Override
    public CJ1FileWithoutData withoutData()
    {
      return new CJ1FileWithoutData(
        this.id,
        this.description,
        this.mediaType,
        this.size(),
        this.hashAlgorithm,
        this.hashValue);
    }
  }

  @Override
  default int compareTo(
    final CJ1FileType other)
  {
    return Comparator.comparing(CJ1FileType::id).compare(this, other);
  }
}
