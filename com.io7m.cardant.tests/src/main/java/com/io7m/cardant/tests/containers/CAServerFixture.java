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


package com.io7m.cardant.tests.containers;

import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.server.api.CAServerConfiguration;
import com.io7m.cardant.server.api.CAServerException;
import com.io7m.cardant.server.api.CAServerFactoryType;
import com.io7m.cardant.server.api.CAServerHTTPServiceConfiguration;
import com.io7m.cardant.server.api.CAServerIdstoreConfiguration;
import com.io7m.cardant.server.api.CAServerLimitsConfiguration;
import com.io7m.cardant.server.api.CAServerMaintenanceConfiguration;
import com.io7m.cardant.server.api.CAServerType;
import com.io7m.cardant.server.basic.CAServers;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.cardant.tls.CATLSDisabled;
import com.io7m.idstore.model.IdName;

import java.io.IOException;
import java.net.URI;
import java.time.Clock;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;

import static com.io7m.cardant.tests.containers.CADatabaseFixture.DATABASES;
import static java.util.Optional.empty;

public record CAServerFixture(
  CAServerType server)
  implements AutoCloseable
{
  private static final CAServerFactoryType SERVERS =
    new CAServers();

  @Override
  public void close()
    throws Exception
  {
    this.server.close();
  }

  public static CAServerFixture create(
    final CAIdstoreFixture idstoreFixture,
    final CADatabaseFixture databaseFixture)
    throws IOException, CAServerException
  {
    final var configuration =
      new CAServerConfiguration(
        Locale.ROOT,
        Clock.systemUTC(),
        CAStrings.create(Locale.ROOT),
        DATABASES,
        databaseFixture.configuration(),
        new CAServerHTTPServiceConfiguration(
          "::",
          CAFixtures.inventoryServicePort(),
          URI.create("http://[::]:" + CAFixtures.inventoryServicePort()),
          Optional.of(Duration.ofHours(1L)),
          CATLSDisabled.TLS_DISABLED
        ),
        new CAServerIdstoreConfiguration(
          URI.create("http://[::]:" + idstoreFixture.userAPIPort()),
          URI.create("http://[::]:" + idstoreFixture.userAPIPort())
        ),
        new CAServerLimitsConfiguration(
          10_000_000L,
          1_000_000L
        ),
        new CAServerMaintenanceConfiguration(
          empty()
        ),
        empty()
      );

    final var fixture =
      new CAServerFixture(SERVERS.createServer(configuration));

    fixture.server.start();
    return fixture;
  }

  public void setUserAsAdmin(
    final CAUserID userId,
    final String userName)
    throws CAServerException
  {
    this.server.setUserAsAdmin(userId, new IdName(userName));
  }
}
