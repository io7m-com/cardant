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

package com.io7m.cardant.server.internal.rest;

import com.io7m.cardant.protocol.versioning.CAVersioningMessageSerializerFactoryType;
import com.io7m.cardant.protocol.versioning.messages.CAVersioningAPIVersioning;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

import static com.io7m.cardant.server.internal.rest.CAMediaTypes.applicationCardantVersioningXML;

/**
 * An API versioning advertiser.
 */

public final class CAVersioningServlet extends HttpServlet
{
  private final CAVersioningMessageSerializerFactoryType serializers;
  private final CAVersioningAPIVersioning versioning;
  private URI clientURI;

  /**
   * An API versioning advertiser.
   *
   * @param inSerializers The message serializers
   * @param inVersioning  The available API versions
   */

  public CAVersioningServlet(
    final CAVersioningMessageSerializerFactoryType inSerializers,
    final CAVersioningAPIVersioning inVersioning)
  {
    this.serializers =
      Objects.requireNonNull(inSerializers, "serializers");
    this.versioning =
      Objects.requireNonNull(inVersioning, "versioning");
  }

  private static String clientOf(
    final HttpServletRequest servletRequest)
  {
    return new StringBuilder(64)
      .append('[')
      .append(servletRequest.getRemoteAddr())
      .append(':')
      .append(servletRequest.getRemotePort())
      .append(']')
      .toString();
  }

  private static URI makeClientURI(
    final HttpServletRequest servletRequest)
  {
    return URI.create(
      new StringBuilder(64)
        .append("client:")
        .append(servletRequest.getRemoteAddr())
        .append(":")
        .append(servletRequest.getRemotePort())
        .toString()
    );
  }

  @Override
  protected void service(
    final HttpServletRequest request,
    final HttpServletResponse servletResponse)
    throws IOException
  {
    MDC.put("client", clientOf(request));
    this.clientURI = makeClientURI(request);

    try {
      servletResponse.setContentType(applicationCardantVersioningXML());

      try (var outputStream = servletResponse.getOutputStream()) {
        this.serializers.serialize(
          this.clientURI,
          outputStream,
          this.versioning
        );
      }
    } catch (final Exception e) {
      throw new IOException(e);
    } finally {
      MDC.remove("client");
    }
  }
}
