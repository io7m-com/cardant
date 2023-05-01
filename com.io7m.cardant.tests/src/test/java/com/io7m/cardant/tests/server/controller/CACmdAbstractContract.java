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

package com.io7m.cardant.tests.server.controller;

import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.server.controller.CAServerStrings;
import com.io7m.cardant.server.controller.inventory.CAICommandContext;
import com.io7m.cardant.server.service.clock.CAServerClock;
import com.io7m.cardant.server.service.sessions.CASession;
import com.io7m.cardant.server.service.sessions.CASessionSecretIdentifier;
import com.io7m.cardant.server.service.telemetry.api.CAServerTelemetryNoOp;
import com.io7m.cardant.server.service.telemetry.api.CAServerTelemetryServiceType;
import com.io7m.cardant.tests.CAFakeClock;
import com.io7m.idstore.model.IdName;
import com.io7m.medrina.api.MRoleName;
import com.io7m.medrina.api.MSubject;
import com.io7m.repetoir.core.RPServiceDirectory;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public abstract class CACmdAbstractContract
{
  private RPServiceDirectory services;
  private CADatabaseTransactionType transaction;
  private CAFakeClock clock;
  private CAServerClock serverClock;
  private CAServerStrings strings;
  private OffsetDateTime timeStart;
  private CAUser user;
  private CAICommandContext context;

  protected final OffsetDateTime timeStart()
  {
    return this.timeStart;
  }

  @BeforeEach
  protected final void commandSetup()
    throws Exception
  {
    this.services =
      new RPServiceDirectory();
    this.transaction =
      Mockito.mock(CADatabaseTransactionType.class);

    this.clock =
      new CAFakeClock();
    this.serverClock =
      new CAServerClock(this.clock);
    this.timeStart =
      this.serverClock.now();
    this.strings =
      new CAServerStrings(Locale.ROOT);

    this.user =
      new CAUser(
        UUID.randomUUID(),
        new IdName("x"),
        new MSubject(Set.of())
      );

    this.services.register(CAServerClock.class, this.serverClock);
    this.services.register(CAServerStrings.class, this.strings);
    this.services.register(
      CAServerTelemetryServiceType.class,
      CAServerTelemetryNoOp.noop());
  }

  protected final void setRoles(
    final MRoleName... roles)
  {
    this.user = new CAUser(
      this.user.userId(),
      new IdName("x"),
      new MSubject(Set.of(roles))
    );
  }

  @AfterEach
  protected final void commandTearDown()
    throws Exception
  {
    this.services.close();
  }

  protected final RPServiceDirectoryType services()
  {
    return this.services;
  }

  protected final CADatabaseTransactionType transaction()
  {
    return this.transaction;
  }

  protected final CAICommandContext createContext()
  {
    final var session =
      new CASession(CASessionSecretIdentifier.generate(), this.user);

    return new CAICommandContext(
      this.services,
      UUID.randomUUID(),
      this.transaction,
      session,
      "127.0.0.1",
      "Tests"
    );
  }
}
