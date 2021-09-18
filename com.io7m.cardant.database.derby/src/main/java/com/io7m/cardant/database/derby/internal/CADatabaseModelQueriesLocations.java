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
import com.io7m.cardant.model.CAModelDatabaseQueriesLocationsType;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.io7m.cardant.database.api.CADatabaseErrorCode.ERROR_CYCLIC;

/**
 * Internal database calls for the inventory.
 */

public final class CADatabaseModelQueriesLocations
  extends CADatabaseModelQueriesAbstract
  implements CAModelDatabaseQueriesLocationsType
{
  private static final String LOCATION_PUT_INSERT = """
    INSERT INTO cardant.locations (
      location_id,
      location_parent,
      location_name,
      location_description
    ) VALUES (?, ?, ?, ?)
    """;

  private static final String LOCATION_PUT_UPDATE = """
    UPDATE cardant.locations
      SET location_name = ?,
          location_parent = ?,
          location_description = ?
      WHERE location_id = ?
    """;

  private static final String LOCATION_GET = """
    SELECT
      l.location_id,
      l.location_parent,
      l.location_name,
      l.location_description
    FROM cardant.locations l
      WHERE location_id = ?
    """;

  private static final String LOCATION_LIST = """
    SELECT
      l.location_id,
      l.location_parent,
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
      Optional.ofNullable(result.getBytes("location_parent"))
          .map(CADatabaseBytes::locationIdFromBytes),
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
      statement.setBytes(2, location.parent().map(CADatabaseBytes::locationIdBytes).orElse(null));
      statement.setString(3, location.name());
      statement.setString(4, location.description());
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
      statement.setBytes(2, location.parent().map(CADatabaseBytes::locationIdBytes).orElse(null));
      statement.setString(3, location.description());
      statement.setBytes(4, CADatabaseBytes.locationIdBytes(location.id()));
      statement.executeUpdate();
    }
  }

  private static TreeMap<CALocationID, CALocation> locationListInner(
    final Connection connection)
    throws SQLException
  {
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
  }

  @Override
  public void locationPut(
    final CALocation location)
    throws CADatabaseException
  {
    Objects.requireNonNull(location, "location");

    this.withSQLConnection(connection -> {
      checkAcyclic(connection, location);

      final var existing = locationGetInner(connection, location.id());
      if (existing.isPresent()) {
        locationPutUpdate(connection, location);
      } else {
        locationPutInsert(connection, location);
      }

      this.publishUpdate(location.id());
      return null;
    });
  }

  private static void checkAcyclic(
    final Connection connection,
    final CALocation newLocation)
    throws SQLException, CADatabaseException
  {
    /*
     * If the location did not previously exist, then adding a new location
     * cannot introduce a cycle.
     */

    final var oldLocationOpt =
      locationGetInner(connection, newLocation.id());

    if (oldLocationOpt.isEmpty()) {
      return;
    }

    /*
     * If the parent did not change, then the update cannot introduce
     * a cycle.
     */

    final var oldLocation = oldLocationOpt.get();
    final var newParentOpt = newLocation.parent();
    if (Objects.equals(newParentOpt, oldLocation.parent())) {
      return;
    }

    /*
     * If the new location is simply removing the parent, then the update
     * cannot introduce a cycle.
     */

    if (newParentOpt.isEmpty()) {
      return;
    }

    /*
     * Otherwise, check if the changed parent would introduce a cycle
     * in the location graph...
     */

    final var oldParentOpt =
      oldLocation.parent();
    final var newParent =
      newParentOpt.get();

    final var graph =
      new DirectedAcyclicGraph<CALocationID, CALocationEdge>(CALocationEdge.class);

    final var locations =
      locationListInner(connection);

    try {
      for (final var location : locations.values()) {
        final var locationId = location.id();
        graph.addVertex(locationId);

        final var parentOpt = location.parent();
        if (parentOpt.isPresent()) {
          final var parentId = parentOpt.get();
          final var edge = new CALocationEdge(locationId, parentId);
          graph.addEdge(locationId, parentId, edge);
        }
      }

      oldParentOpt.ifPresent(
        oldParentId -> graph.removeEdge(newLocation.id(), oldParentId));

      final var newEdge = new CALocationEdge(newLocation.id(), newParent);
      graph.addEdge(newLocation.id(), newParent, newEdge);
    } catch (final IllegalArgumentException e) {
      throw new CADatabaseException(ERROR_CYCLIC, e);
    }
  }

  @Override
  public Optional<CALocation> locationGet(
    final CALocationID id)
    throws CADatabaseException
  {
    Objects.requireNonNull(id, "id");
    return this.withSQLConnection(
      connection -> locationGetInner(connection, id)
    );
  }

  @Override
  public SortedMap<CALocationID, CALocation> locationList()
    throws CADatabaseException
  {
    return this.withSQLConnection(
      CADatabaseModelQueriesLocations::locationListInner);
  }

  private record CALocationEdge(
    CALocationID from,
    CALocationID to) { }
}
