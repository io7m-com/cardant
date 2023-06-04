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

package com.io7m.cardant.server.service.health;

import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import com.io7m.repetoir.core.RPServiceType;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.io7m.cardant.database.api.CADatabaseRole.CARDANT;

/**
 * The server health service.
 */

public final class CAServerHealth implements RPServiceType, AutoCloseable
{
  private final CADatabaseType database;
  private final ScheduledExecutorService executor;
  private volatile String status;

  private CAServerHealth(
    final CADatabaseType inDatabase,
    final ScheduledExecutorService inExecutor)
  {
    this.database =
      Objects.requireNonNull(inDatabase, "database");
    this.executor =
      Objects.requireNonNull(inExecutor, "executor");

    this.status = statusOKText();

    this.executor.scheduleAtFixedRate(
      this::updateHealthStatus, 0L, 30L, TimeUnit.SECONDS);
  }

  /**
   * @return The string used to indicate a healthy server
   */

  public static String statusOKText()
  {
    return "OK";
  }

  private void updateHealthStatus()
  {
    try (var ignored = this.database.openConnection(CARDANT)) {
      this.status = statusOKText();
    } catch (final CADatabaseException e) {
      this.status = "UNHEALTHY DATABASE (%s)".formatted(e.getMessage());
    }
  }

  /**
   * The server health service.
   *
   * @param services The services
   *
   * @return The health service
   */

  public static CAServerHealth create(
    final RPServiceDirectoryType services)
  {
    final var database =
      services.requireService(CADatabaseType.class);

    final var executor =
      Executors.newSingleThreadScheduledExecutor(r -> {
        final var thread = new Thread(r);
        thread.setDaemon(true);
        thread.setName(
          "com.io7m.idstore.server.service.health[%d]"
            .formatted(Long.valueOf(thread.getId()))
        );
        return thread;
      });

    return new CAServerHealth(database, executor);
  }

  /**
   * @return The current health status
   */

  public String status()
  {
    return this.status;
  }

  @Override
  public String toString()
  {
    return "[CAServerHealth 0x%s]"
      .formatted(Long.toUnsignedString(this.hashCode(), 16));
  }

  @Override
  public String description()
  {
    return "Health service";
  }

  @Override
  public void close()
  {
    this.executor.shutdown();
  }
}
