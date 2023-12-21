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


package com.io7m.cardant.server.service.configuration.v1;

import com.io7m.blackthorne.core.BTElementHandlerConstructorType;
import com.io7m.blackthorne.core.BTElementHandlerType;
import com.io7m.blackthorne.core.BTElementParsingContextType;
import com.io7m.blackthorne.core.BTQualifiedName;
import com.io7m.cardant.server.api.CAServerConfigurationFile;
import com.io7m.cardant.server.api.CAServerDatabaseConfiguration;
import com.io7m.cardant.server.api.CAServerHTTPServiceConfiguration;
import com.io7m.cardant.server.api.CAServerIdstoreConfiguration;
import com.io7m.cardant.server.api.CAServerLimitsConfiguration;
import com.io7m.cardant.server.api.CAServerMaintenanceConfiguration;
import com.io7m.cardant.server.api.CAServerOpenTelemetryConfiguration;

import java.util.Map;
import java.util.Optional;

import static com.io7m.cardant.server.service.configuration.v1.CAC1Names.qName;
import static java.util.Map.entry;

/**
 * The root configuration parser.
 */

public final class CAC1Configuration
  implements BTElementHandlerType<Object, CAServerConfigurationFile>
{
  private CAServerDatabaseConfiguration database;
  private Optional<CAServerOpenTelemetryConfiguration> telemetry;
  private CAServerIdstoreConfiguration idstore;
  private CAServerLimitsConfiguration limits;
  private CAServerHTTPServiceConfiguration inventory;
  private CAServerMaintenanceConfiguration maintenance;

  /**
   * The root configuration parser.
   *
   * @param context The context
   */

  public CAC1Configuration(
    final BTElementParsingContextType context)
  {
    this.telemetry = Optional.empty();
  }

  @Override
  public Map<BTQualifiedName, BTElementHandlerConstructorType<?, ?>>
  onChildHandlersRequested(
    final BTElementParsingContextType context)
  {
    return Map.ofEntries(
      entry(qName("Database"), CAC1Database::new),
      entry(qName("InventoryService"), CAC1InventoryService::new),
      entry(qName("Idstore"), CAC1Idstore::new),
      entry(qName("Limits"), CAC1Limits::new),
      entry(qName("Maintenance"), CAC1Maintenance::new),
      entry(qName("OpenTelemetry"), CAC1Telemetry::new)
    );
  }

  @Override
  public void onChildValueProduced(
    final BTElementParsingContextType context,
    final Object result)
  {
    switch (result) {
      case final CAServerDatabaseConfiguration c -> {
        this.database = c;
      }
      case final CAServerOpenTelemetryConfiguration c -> {
        this.telemetry = Optional.of(c);
      }
      case final CAServerLimitsConfiguration c -> {
        this.limits = c;
      }
      case final CAServerIdstoreConfiguration c -> {
        this.idstore = c;
      }
      case final CAServerHTTPServiceConfiguration c -> {
        this.inventory = c;
      }
      case final CAServerMaintenanceConfiguration c -> {
        this.maintenance = c;
      }
      default -> {
        throw new IllegalArgumentException(
          "Unrecognized element: %s".formatted(result)
        );
      }
    }
  }

  @Override
  public CAServerConfigurationFile onElementFinished(
    final BTElementParsingContextType context)
  {
    return new CAServerConfigurationFile(
      this.inventory,
      this.database,
      this.idstore,
      this.limits,
      this.maintenance,
      this.telemetry
    );
  }
}
