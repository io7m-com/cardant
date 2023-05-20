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

import com.io7m.cardant.client.preferences.api.CAPreferencesServiceType;
import com.io7m.cardant.client.preferences.vanilla.CAPreferencesService;
import com.io7m.cardant.client.transfer.api.CATransferServiceType;
import com.io7m.cardant.client.transfer.vanilla.CATransferService;
import com.io7m.jade.api.ApplicationDirectories;
import com.io7m.jade.api.ApplicationDirectoriesType;
import com.io7m.jade.api.ApplicationDirectoryConfiguration;
import com.io7m.repetoir.core.RPServiceDirectory;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.Executors;

public final class CAMainServices
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAMainServices.class);

  private CAMainServices()
  {

  }

  public static RPServiceDirectoryType create()
    throws Exception
  {
    final ApplicationDirectoriesType directories =
      applicationDirectories();

    final var locale =
      Locale.getDefault();
    final var services =
      new RPServiceDirectory();

    final var mainStrings = new CAMainStrings(locale);
    services.register(CAMainStrings.class, mainStrings);

    final var clock = new CAClockService(Clock.systemUTC());
    services.register(CAClockService.class, clock);

    final var statusService = new CAStatusService();
    services.register(CAStatusServiceType.class, statusService);

    final var clients =
      CAMainClientService.create(services, locale);
    services.register(CAMainClientService.class, clients);

    services.register(
      CAPreferencesServiceType.class,
      openPreferences(directories)
    );
    services.register(
      CAIconsType.class,
      new CAIcons()
    );
    services.register(
      CAFileDialogs.class,
      new CAFileDialogs(mainStrings)
    );
    services.register(
      CAExternalImages.class,
      new CAExternalImages(mainStrings)
    );

    final var mainController = new CAMainController(services);
    services.register(CAMainController.class, mainController);

    final var transferIO =
      Executors.newScheduledThreadPool(4, r -> {
        final var thread = new Thread(r);
        thread.setDaemon(true);
        thread.setName(
          new StringBuilder(64)
            .append("com.io7m.cardant.client.transfer[")
            .append(thread.getId())
            .append("]")
            .toString()
        );
        return thread;
      });

    final var transfers =
      CATransferService.create(
        Clock.systemUTC(),
        transferIO,
        Duration.ofMinutes(1L),
        locale,
        directories.cacheDirectory().resolve("transfers")
      );

    services.register(CATransferServiceType.class, transfers);
    return services;
  }

  private static CAPreferencesServiceType openPreferences(
    final ApplicationDirectoriesType directories)
    throws IOException
  {
    final var configurationDirectory =
      directories.configurationDirectory();
    final var configurationFile =
      configurationDirectory.resolve("preferences.xml");

    LOG.info("preferences: {}", configurationFile);
    return CAPreferencesService.openOrDefault(configurationFile);
  }

  private static ApplicationDirectoriesType applicationDirectories()
  {
    final var configuration =
      ApplicationDirectoryConfiguration.builder()
        .setApplicationName("com.io7m.cardant")
        .setPortablePropertyName("com.io7m.cardant.portable")
        .build();

    return ApplicationDirectories.get(configuration);
  }
}
