/*
 * Copyright © 2021 Mark Raynsford <code@io7m.com> http://io7m.com
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

package com.io7m.cardant.database.derby;

import com.io7m.cardant.database.api.CADatabaseEvent;
import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseParameters;
import com.io7m.cardant.database.api.CADatabaseProviderType;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.database.derby.internal.CADatabaseDerby;
import com.io7m.cardant.database.derby.internal.CADatabaseMessages;
import com.io7m.cardant.database.derby.internal.xml.CADatabaseSchemaDecl;
import com.io7m.cardant.database.derby.internal.xml.CADatabaseSchemaSetDecl;
import com.io7m.cardant.database.derby.internal.xml.CADatabaseXML;
import org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Consumer;

import static com.io7m.cardant.database.api.CADatabaseErrorCode.ERROR_GENERAL;
import static com.io7m.cardant.database.derby.internal.CADerbyConstants.LANG_SCHEMA_DOES_NOT_EXIST;
import static com.io7m.cardant.database.derby.internal.CADerbyConstants.LANG_TABLE_NOT_FOUND;
import static java.math.BigInteger.valueOf;

/**
 * A provider of Derby databases.
 */

public final class CADatabasesDerby implements CADatabaseProviderType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CADatabasesDerby.class);

  private final CADatabaseMessages messages;

  /**
   * A provider of Derby databases.
   */

  public CADatabasesDerby()
  {
    this(Locale.getDefault());
  }

  /**
   * A provider of Derby databases.
   *
   * @param locale The locale
   */

  public CADatabasesDerby(
    final Locale locale)
  {
    try {
      this.messages =
        new CADatabaseMessages(Objects.requireNonNull(locale, "locale"));
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static void upgradeDatabase(
    final Connection connection,
    final Optional<BigInteger> currentVersionOpt,
    final Consumer<CADatabaseEvent> safeEvents)
    throws IOException, SQLException
  {
    final var url =
      CADatabasesDerby.class.getResource(
        "/com/io7m/cardant/database/derby/internal/database.xml");

    final CADatabaseSchemaSetDecl set;
    try (var stream = url.openStream()) {
      set = CADatabaseXML.parse(url.toURI(), stream);
    } catch (final URISyntaxException e) {
      throw new IOException(e);
    }

    final NavigableMap<BigInteger, CADatabaseSchemaDecl> revisions;
    if (currentVersionOpt.isPresent()) {
      final var currentVersion = currentVersionOpt.get();
      LOG.debug("current database version is {}", currentVersion);
      revisions = set.schemasInOrder().tailMap(currentVersion, false);
    } else {
      LOG.debug("current database version is unset; full upgrade required");
      revisions = set.schemasInOrder();
    }

    executeRevisions(connection, revisions, safeEvents);
  }

  private static void executeRevisions(
    final Connection connection,
    final NavigableMap<BigInteger, CADatabaseSchemaDecl> revisions,
    final Consumer<CADatabaseEvent> events)
    throws SQLException
  {
    try {
      LOG.debug(
        "{} schema revisions available",
        Integer.valueOf(revisions.size())
      );

      if (revisions.isEmpty()) {
        return;
      }

      int statementCount = 0;
      for (final var entry : revisions.entrySet()) {
        statementCount += entry.getValue().statements().size();
      }

      int statementCurrent = 0;
      for (final var entry : revisions.entrySet()) {
        final var statementTexts = entry.getValue().statements();
        final var revisionId = entry.getKey();

        for (final var statementText : statementTexts) {
          try (var statement = connection.prepareStatement(statementText)) {
            executing(events, statementText, statementCurrent, statementCount);
            statement.execute();
            ++statementCurrent;
          }
        }

        final String statementText;
        if (Objects.equals(revisionId, BigInteger.ZERO)) {
          statementText = "insert into cardant.schema_version (version_number) values (?)";
        } else {
          statementText = "update cardant.schema_version set version_number = ?";
        }

        executing(events, statementText, statementCurrent, statementCount);
        try (var statement = connection.prepareStatement(statementText)) {
          statement.setLong(1, revisionId.longValue());
          statement.execute();
        }
      }

      events.accept(
        CADatabaseEvent.builder()
          .setMessage("Committing database changes")
          .setProgress(OptionalDouble.of(1.0))
          .build()
      );

      LOG.debug("commit");
      connection.commit();
    } catch (final Exception e) {
      connection.rollback();
      throw e;
    }
  }

  private static void executing(
    final Consumer<CADatabaseEvent> events,
    final String statement,
    final int statementCurrent,
    final int statementCount)
  {
    final var progress = (double) statementCurrent / (double) statementCount;

    events.accept(
      CADatabaseEvent.builder()
        .setMessage("Executing: " + statement)
        .setProgress(OptionalDouble.of(progress))
        .build()
    );
  }

  @Override
  public CADatabaseType open(
    final CADatabaseParameters parameters,
    final Consumer<CADatabaseEvent> events)
    throws CADatabaseException
  {
    Objects.requireNonNull(parameters, "parameters");
    Objects.requireNonNull(events, "events");

    final var path = parameters.path();
    LOG.info("open: {}", path);

    final Consumer<CADatabaseEvent> safeEvents = databaseEvent -> {
      try {
        events.accept(databaseEvent);
      } catch (final Exception e) {
        LOG.error("event consumer raised exception: ", e);
      }
    };

    try {
      final var dataSource = new EmbeddedConnectionPoolDataSource();
      dataSource.setDatabaseName(path);
      dataSource.setCreateDatabase("true");
      dataSource.setConnectionAttributes("create=true");

      final var connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      upgradeDatabase(
        connection,
        this.getSchemaVersion(connection),
        safeEvents);

      return new CADatabaseDerby(this.messages, dataSource);
    } catch (final Exception e) {
      throw new CADatabaseException(
        ERROR_GENERAL,
        this.messages.format("errorOpenDatabase", e.getLocalizedMessage()),
        e
      );
    }
  }

  private Optional<BigInteger> getSchemaVersion(
    final Connection connection)
    throws CADatabaseException
  {
    Objects.requireNonNull(connection, "connection");

    try {
      final var statementText = "SELECT version_number FROM cardant.schema_version";
      LOG.debug("execute: {}", statementText);

      try (var statement = connection.prepareStatement(statementText)) {
        try (var result = statement.executeQuery()) {
          if (!result.next()) {
            throw new SQLException(
              this.messages.format(
                "errorSchemaVersionTableRow",
                "cardant.schema_version")
            );
          }
          return Optional.of(valueOf(result.getLong(1)));
        }
      }
    } catch (final SQLException e) {
      final var state = e.getSQLState();
      if (state == null) {
        throw new CADatabaseException(
          ERROR_GENERAL,
          e.getLocalizedMessage(),
          e);
      }
      switch (state) {
        case LANG_SCHEMA_DOES_NOT_EXIST:
        case LANG_TABLE_NOT_FOUND: {
          return Optional.empty();
        }
        default: {
          throw new CADatabaseException(
            ERROR_GENERAL,
            e.getLocalizedMessage(),
            e);
        }
      }
    }
  }
}
