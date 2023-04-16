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

package com.io7m.cardant.server.inventory.v1;

import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.inventory.CAIMessageType;
import com.io7m.cardant.protocol.inventory.cb.CAI1Messages;
import com.io7m.repetoir.core.RPServiceType;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Objects;

/**
 * Convenient functions to send messages.
 */

public final class CAI1Sends implements RPServiceType
{
  private final CAI1Messages messages;

  /**
   * Convenient functions to send messages.
   *
   * @param inMessages A message codec
   */

  public CAI1Sends(
    final CAI1Messages inMessages)
  {
    this.messages = Objects.requireNonNull(inMessages, "messages");
  }

  /**
   * Send a message.
   *
   * @param response   The servlet response
   * @param statusCode The HTTP status code
   * @param message    The message
   *
   * @throws IOException On errors
   */

  public void send(
    final HttpServletResponse response,
    final int statusCode,
    final CAIMessageType message)
    throws IOException
  {
    response.setStatus(statusCode);
    response.setContentType(CAI1Messages.contentType());

    try {
      final var data = this.messages.serialize(message);
      response.setContentLength(data.length);
      try (var output = response.getOutputStream()) {
        output.write(data);
      }
    } catch (final CAProtocolException e) {
      throw new IOException(e);
    }
  }

  @Override
  public String description()
  {
    return "Inventory v1 sending service.";
  }

  @Override
  public String toString()
  {
    return "[CAI1Sends 0x%s]"
      .formatted(Long.toUnsignedString(this.hashCode(), 16));
  }
}
