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

package com.io7m.cardant.gui.internal;

import com.io7m.cardant.client.api.CAClientFactoryType;
import com.io7m.cardant.client.preferences.api.CAPreferencesServiceType;
import com.io7m.cardant.client.preferences.vanilla.CAPreferencesService;
import com.io7m.cardant.client.vanilla.CAClients;
import com.io7m.cardant.services.api.CAServiceDirectory;
import com.io7m.cardant.services.api.CAServiceDirectoryType;
import com.io7m.jade.api.ApplicationDirectories;
import com.io7m.jade.api.ApplicationDirectoryConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;

public final class CAMainServices
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAMainServices.class);

  private CAMainServices()
  {

  }

  public static CAServiceDirectoryType create()
    throws IOException
  {
    final var services = new CAServiceDirectory();

    final var mainStrings = new CAMainStrings(Locale.getDefault());
    services.register(CAMainStrings.class, mainStrings);
    services.register(CAPreferencesServiceType.class, openPreferences());
    services.register(CAIconsType.class, new CAIcons());

    final var eventBus = new CAMainEventBus();
    services.register(CAMainEventBusType.class, eventBus);

    final var clients = new CAClients();
    services.register(CAClientFactoryType.class, clients);

    services.register(
      CAMainClientController.class,
      new CAMainClientController(mainStrings, clients, eventBus)
    );

    return services;
  }

  private static CAPreferencesServiceType openPreferences()
    throws IOException
  {
    final var configuration =
      ApplicationDirectoryConfiguration.builder()
        .setApplicationName("com.io7m.cardant")
        .setPortablePropertyName("com.io7m.cardant.portable")
        .build();

    final var directories =
      ApplicationDirectories.get(configuration);
    final var configurationDirectory =
      directories.configurationDirectory();
    final var configurationFile =
      configurationDirectory.resolve("preferences.xml");

    LOG.info("preferences: {}", configurationFile);
    return CAPreferencesService.openOrDefault(configurationFile);
  }
}
