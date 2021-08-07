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

package com.io7m.cardant.server;

import com.io7m.cardant.database.api.CADatabaseProviderType;
import com.io7m.cardant.server.api.CAServerConfiguration;
import com.io7m.cardant.server.api.CAServerFactoryType;
import com.io7m.cardant.server.api.CAServerType;
import com.io7m.cardant.server.internal.CAServer;

import java.io.IOException;
import java.util.Objects;
import java.util.ServiceLoader;

/**
 * A factory of servers.
 */

public final class CAServers implements CAServerFactoryType
{
  private final CADatabaseProviderType databases;

  /**
   * A factory of servers.
   */

  public CAServers()
  {
    this(
      ServiceLoader.load(CADatabaseProviderType.class)
        .findFirst()
        .orElseThrow()
    );
  }

  /**
   * A factory of servers.
   *
   * @param inDatabases A provider of databases
   */

  public CAServers(
    final CADatabaseProviderType inDatabases)
  {
    this.databases =
      Objects.requireNonNull(inDatabases, "inDatabases");
  }

  @Override
  public CAServerType createServer(
    final CAServerConfiguration configuration)
    throws IOException
  {
    try {
      return CAServer.create(configuration, this.databases);
    } catch (final Exception e) {
      throw new IOException(e);
    }
  }
}
