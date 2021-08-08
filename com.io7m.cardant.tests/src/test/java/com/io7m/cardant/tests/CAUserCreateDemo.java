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

package com.io7m.cardant.tests;

import com.io7m.cardant.database.api.CADatabaseOpenEvent;
import com.io7m.cardant.database.api.CADatabaseParameters;
import com.io7m.cardant.database.derby.CADatabasesDerby;
import com.io7m.cardant.model.CAModelCADatabaseQueriesType;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.model.CAUsers;
import com.io7m.cardant.server.api.CAServerConfiguration;
import com.io7m.cardant.server.api.CAServerConfigurationParserFactoryType;
import com.io7m.cardant.server.api.CAServerDatabaseLocalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ServiceLoader;
import java.util.function.Consumer;

public final class CAUserCreateDemo
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAUserCreateDemo.class);

  private CAUserCreateDemo()
  {

  }

  public static void main(
    final String[] args)
    throws Exception
  {
    final var configurationFile =
      Paths.get(args[0]);

    final var configurations =
      ServiceLoader.load(CAServerConfigurationParserFactoryType.class)
        .findFirst()
        .orElseThrow();

    final CAServerConfiguration configuration =
      configurations.parseFileWithContext(
        FileSystems.getDefault(), configurationFile);

    final var databaseLocalConfiguration =
      (CAServerDatabaseLocalConfiguration) configuration.database();

    final var databaseParameters =
      new CADatabaseParameters(
        databaseLocalConfiguration.file().toString(),
        false
      );

    final Consumer<CADatabaseOpenEvent> events = databaseEvent -> {

    };

    try (var database = new CADatabasesDerby()
      .open(databaseParameters, events)) {
      try (var connection = database.openConnection()) {
        try (var transaction = connection.beginTransaction()) {
          final var queries =
            transaction.queries(CAModelCADatabaseQueriesType.class);
          queries.userPut(CAUsers.createUser(
            SecureRandom.getInstanceStrong(),
            CAUserID.random(),
            "someone",
            "12345678"
          ));
          transaction.commit();
        }
      }
    }
  }
}
