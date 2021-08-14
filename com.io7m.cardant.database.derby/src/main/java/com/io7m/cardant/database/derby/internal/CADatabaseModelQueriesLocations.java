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

package com.io7m.cardant.database.derby.internal;

import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CAModelCADatabaseQueriesLocationsType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Internal database calls for the inventory.
 */

public final class CADatabaseModelQueriesLocations
  extends CADatabaseModelQueriesAbstract
  implements CAModelCADatabaseQueriesLocationsType
{
  private static final String LOCATION_PUT_INSERT = """
    INSERT INTO cardant.locations (
      location_id,
      location_name,
      location_description
    ) VALUES (?, ?, ?)
    """;

  private static final String LOCATION_PUT_UPDATE = """
    UPDATE cardant.locations
      SET location_name = ?,
          location_description = ?
      WHERE location_id = ?
    """;

  private static final String LOCATION_GET = """
    SELECT
      l.location_id,
      l.location_name,
      l.location_description
    FROM cardant.locations l
      WHERE location_id = ?
    """;

  private static final String LOCATION_LIST = """
    SELECT
      l.location_id,
      l.location_name,
      l.location_description
    FROM cardant.locations l
    """;

  CADatabaseModelQueriesLocations(
    final CADatabaseDerbyTransaction inTransaction)
  {
    super(inTransaction);
  }

  private static Optional<CALocation> locationGetInner(
    final Connection connection,
    final CALocationID id)
    throws SQLException
  {
    try (var statement = connection.prepareStatement(LOCATION_GET)) {
      statement.setBytes(1, CADatabaseBytes.locationIdBytes(id));
      try (var result = statement.executeQuery()) {
        if (!result.next()) {
          return Optional.empty();
        }
        return Optional.of(locationParse(result));
      }
    }
  }

  private static CALocation locationParse(
    final ResultSet result)
    throws SQLException
  {
    return new CALocation(
      CADatabaseBytes.locationIdFromBytes(result.getBytes("location_id")),
      result.getString("location_name"),
      result.getString("location_description")
    );
  }

  private static void locationPutInsert(
    final Connection connection,
    final CALocation location)
    throws SQLException
  {
    try (var statement = connection.prepareStatement(LOCATION_PUT_INSERT)) {
      statement.setBytes(1, CADatabaseBytes.locationIdBytes(location.id()));
      statement.setString(2, location.name());
      statement.setString(3, location.description());
      statement.executeUpdate();
    }
  }

  private static void locationPutUpdate(
    final Connection connection,
    final CALocation location)
    throws SQLException
  {
    try (var statement = connection.prepareStatement(LOCATION_PUT_UPDATE)) {
      statement.setString(1, location.name());
      statement.setString(2, location.description());
      statement.setBytes(3, CADatabaseBytes.locationIdBytes(location.id()));
      statement.executeUpdate();
    }
  }

  @Override
  public void locationPut(final CALocation location)
    throws CADatabaseException
  {
    Objects.requireNonNull(location, "location");

    this.withSQLConnection(connection -> {
      final var existing = locationGetInner(connection, location.id());
      if (existing.isPresent()) {
        locationPutUpdate(connection, location);
      } else {
        locationPutInsert(connection, location);
      }
      return null;
    });
  }

  @Override
  public Optional<CALocation> locationGet(final CALocationID id)
    throws CADatabaseException
  {
    Objects.requireNonNull(id, "id");
    return this.withSQLConnection(connection -> locationGetInner(
      connection,
      id));
  }

  @Override
  public SortedMap<CALocationID, CALocation> locationList()
    throws CADatabaseException
  {
    return this.withSQLConnection(connection -> {
      try (var statement = connection.prepareStatement(LOCATION_LIST)) {
        try (var result = statement.executeQuery()) {
          final var locations = new TreeMap<CALocationID, CALocation>();
          while (result.next()) {
            final var location = locationParse(result);
            locations.put(location.id(), location);
          }
          return locations;
        }
      }
    });
  }

}
