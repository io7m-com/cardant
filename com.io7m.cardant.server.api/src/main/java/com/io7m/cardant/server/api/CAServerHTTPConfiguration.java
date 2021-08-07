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

package com.io7m.cardant.server.api;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Configuration information for the internal web server.
 *
 * @param port             The port on which the web server will listen
 * @param sessionDirectory The directory that will hold session information
 */

public record CAServerHTTPConfiguration(
  int port,
  Path sessionDirectory)
{
  /**
   * Configuration information for the internal web server.
   *
   * @param port             The port on which the web server will listen
   * @param sessionDirectory The directory that will hold session information
   */

  public CAServerHTTPConfiguration
  {
    Objects.requireNonNull(sessionDirectory, "sessionDirectory");

    if (port < 1 || port >= 65536) {
      throw new IllegalArgumentException(
        "Port %d must be in the range [1, 65536)"
          .formatted(Integer.valueOf(port))
      );
    }
  }
}
