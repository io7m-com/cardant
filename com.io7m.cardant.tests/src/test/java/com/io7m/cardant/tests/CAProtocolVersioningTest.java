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

import com.io7m.cardant.protocol.versioning.CAVersioningMessageParsers;
import com.io7m.cardant.protocol.versioning.CAVersioningMessageSerializers;
import com.io7m.cardant.protocol.versioning.messages.CAAPI;
import com.io7m.cardant.protocol.versioning.messages.CAVersion;
import com.io7m.cardant.protocol.versioning.messages.CAVersioningAPIVersioning;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class CAProtocolVersioningTest
{
  private static final URI ANY =
    URI.create("urn:whatever");

  private CAVersioningMessageParsers parsers;
  private CAVersioningMessageSerializers serializers;

  @BeforeEach
  public void testSetup()
  {
    this.parsers =
      new CAVersioningMessageParsers();
    this.serializers =
      new CAVersioningMessageSerializers();
  }

  @Test
  public void testRoundTrip()
    throws Exception
  {
    final var message =
      CAVersioningAPIVersioning.of(
        CAAPI.of(
          new CAVersion("a", "/a/v0", 0L),
          new CAVersion("a", "/a/v1", 1L),
          new CAVersion("a", "/a/v2", 2L)
        ),
        CAAPI.of(
          new CAVersion("b", "/b/v0", 0L),
          new CAVersion("b", "/b/v1", 1L),
          new CAVersion("b", "/b/v2", 2L)
        ),
        CAAPI.of(
          new CAVersion("c", "/c/v0", 0L),
          new CAVersion("c", "/c/v1", 1L),
          new CAVersion("c", "/c/v2", 2L)
        )
      );

    final ByteArrayInputStream input;
    try (var output = new ByteArrayOutputStream()) {
      this.serializers.serialize(ANY, output, message);
      input = new ByteArrayInputStream(output.toByteArray());
    }

    final var result = this.parsers.parse(ANY, input);
    assertEquals(message, result);
  }
}
