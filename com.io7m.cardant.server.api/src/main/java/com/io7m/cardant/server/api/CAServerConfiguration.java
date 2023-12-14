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

package com.io7m.cardant.server.api;

import com.io7m.cardant.database.api.CADatabaseConfiguration;
import com.io7m.cardant.database.api.CADatabaseFactoryType;
import com.io7m.cardant.strings.CAStrings;

import java.time.Clock;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * The configuration for a server.
 *
 * @param clock                 The clock
 * @param strings               The string resources
 * @param databaseConfiguration The database configuration for the server
 * @param databases             The factory of databases that will be used for
 *                              the server
 * @param locale                The locale
 * @param limitsConfiguration   The limits configuration
 * @param idstoreConfiguration  The idstore configuration
 * @param openTelemetry         The OpenTelemetry configuration
 * @param inventoryApiAddress   The inventory API address
 */

public record CAServerConfiguration(
  Locale locale,
  Clock clock,
  CAStrings strings,
  CADatabaseFactoryType databases,
  CADatabaseConfiguration databaseConfiguration,
  CAServerHTTPServiceConfiguration inventoryApiAddress,
  CAServerIdstoreConfiguration idstoreConfiguration,
  CAServerLimitsConfiguration limitsConfiguration,
  Optional<CAServerOpenTelemetryConfiguration> openTelemetry)
{
  /**
   * The configuration for a server.
   *
   * @param clock                 The clock
   * @param strings               The string resources
   * @param databaseConfiguration The database configuration for the server
   * @param databases             The factory of databases that will be used for
   *                              the server
   * @param locale                The locale
   * @param limitsConfiguration   The limits configuration
   * @param idstoreConfiguration  The idstore configuration
   * @param openTelemetry         The OpenTelemetry configuration
   * @param inventoryApiAddress   The inventory API address
   */

  public CAServerConfiguration
  {
    Objects.requireNonNull(inventoryApiAddress, "inventoryApiAddress");
    Objects.requireNonNull(clock, "clock");
    Objects.requireNonNull(strings, "strings");
    Objects.requireNonNull(databaseConfiguration, "databaseConfiguration");
    Objects.requireNonNull(databases, "databases");
    Objects.requireNonNull(idstoreConfiguration, "idstoreConfiguration");
    Objects.requireNonNull(limitsConfiguration, "limitsConfiguration");
    Objects.requireNonNull(locale, "locale");
    Objects.requireNonNull(openTelemetry, "openTelemetry");
  }
}
