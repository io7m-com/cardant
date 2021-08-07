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

package com.io7m.cardant.cmdline.internal;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.io7m.anethum.common.ParseException;
import com.io7m.cardant.server.api.CAServerConfiguration;
import com.io7m.cardant.server.api.CAServerConfigurationParserFactoryType;
import com.io7m.cardant.server.api.CAServerFactoryType;
import com.io7m.claypot.core.CLPCommandContextType;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.ServiceLoader;

/**
 * The "server" command.
 */

@Parameters(commandDescription = "Run the server.")
public final class CACommandServer extends CAAbstractCommand
{
  /**
   * The server configuration file.
   */

  @Parameter(
    names = "--configuration",
    description = "The configuration file",
    required = true)

  // CHECKSTYLE:OFF
  public Path configurationFile;
  // CHECKSTYLE:ON

  /**
   * Construct a command.
   *
   * @param context The command context
   */

  public CACommandServer(
    final CLPCommandContextType context)
  {
    super(
      Locale.getDefault(),
      context
    );
  }

  @Override
  public String extendedHelp()
  {
    return this.caStrings().format("cmdServerHelp");
  }

  @Override
  protected Status executeActual()
  {
    final var servers =
      ServiceLoader.load(CAServerFactoryType.class)
        .findFirst()
        .orElseThrow();

    final var configurations =
      ServiceLoader.load(CAServerConfigurationParserFactoryType.class)
        .findFirst()
        .orElseThrow();

    final CAServerConfiguration configuration;
    try {
      configuration = configurations.parseFile(this.configurationFile);
    } catch (final IOException | ParseException e) {
      this.logger().error("configuration: {}", e.getMessage());
      return Status.FAILURE;
    }

    try {
      try (var ignored = servers.createServer(configuration)) {
        while (true) {
          try {
            Thread.sleep(1_000L);
          } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
          }
        }
      }
    } catch (final IOException e) {
      this.logger().error("server: {}", e.getMessage());
      return Status.FAILURE;
    }
  }

  @Override
  public String name()
  {
    return "server";
  }
}
