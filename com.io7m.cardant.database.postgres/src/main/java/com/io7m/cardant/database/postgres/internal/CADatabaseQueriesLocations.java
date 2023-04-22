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

package com.io7m.cardant.database.postgres.internal;

import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.io7m.cardant.database.postgres.internal.CADatabaseExceptions.handleDatabaseException;
import static com.io7m.cardant.database.postgres.internal.Tables.LOCATIONS;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorCyclic;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSql;

/**
 * Location related queries.
 */

public final class CADatabaseQueriesLocations
  extends CABaseQueries
  implements CADatabaseQueriesLocationsType
{
  CADatabaseQueriesLocations(
    final CADatabaseTransaction inTransaction)
  {
    super(inTransaction);
  }

  @Override
  public void locationPut(
    final CALocation location)
    throws CADatabaseException
  {
    Objects.requireNonNull(location, "location");

    final var errorAttributes = new TreeMap<String, String>();
    errorAttributes.put("Location ID", location.displayId());
    errorAttributes.put("Location Name", location.name());

    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesLocations.locationPut");

    try {
      checkAcyclic(context, location);

      final var id = location.id().id();
      var locRec = context.fetchOne(LOCATIONS, LOCATIONS.LOCATION_ID.eq(id));
      if (locRec == null) {
        locRec = context.newRecord(LOCATIONS);
        locRec.setLocationId(id);
      }
      locRec.setLocationParent(
        location.parent()
          .map(CALocationID::id)
          .orElse(null)
      );
      locRec.setLocationName(location.name());
      locRec.setLocationDescription(location.description());
      locRec.store();
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(
        transaction,
        e,
        errorAttributes
      );
    } catch (final SQLException e) {
      throw new CADatabaseException(
        errorSql(),
        e.getMessage(),
        e,
        errorAttributes
      );
    } finally {
      querySpan.end();
    }
  }

  private static void checkAcyclic(
    final DSLContext context,
    final CALocation newLocation)
    throws SQLException, CADatabaseException
  {
    /*
     * If the location did not previously exist, then adding a new location
     * cannot introduce a cycle.
     */

    final var oldLocationOpt =
      locationGetInner(newLocation.id(), context);

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
      locationListInner(context);

    try {
      for (final var location : locations.values()) {
        final var locationId = location.id();
        graph.addVertex(locationId);
      }

      for (final var location : locations.values()) {
        final var locationId = location.id();
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
      final var errorAttributes = new TreeMap<String, String>();
      errorAttributes.put("New Location ID", newLocation.displayId());
      errorAttributes.put("New Location Name", newLocation.name());
      errorAttributes.put("New Location Parent ID", newParent.displayId());
      errorAttributes.put("Old Location ID", oldLocation.displayId());
      errorAttributes.put("Old Location Name", oldLocation.name());
      oldParentOpt.ifPresent(oldParent -> {
        errorAttributes.put("Old Location Parent ID", oldParent.displayId());
      });

      throw new CADatabaseException(
        errorCyclic(),
        e.getMessage(),
        e,
        errorAttributes
      );
    }
  }

  @Override
  public Optional<CALocation> locationGet(
    final CALocationID id)
    throws CADatabaseException
  {
    Objects.requireNonNull(id, "id");

    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesLocations.locationGet");

    try {
      return locationGetInner(id, context);
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(transaction, e);
    } finally {
      querySpan.end();
    }
  }

  private static Optional<CALocation> locationGetInner(
    final CALocationID id,
    final DSLContext context)
  {
    final var locRec =
      context.fetchOne(LOCATIONS, LOCATIONS.LOCATION_ID.eq(id.id()));
    if (locRec == null) {
      return Optional.empty();
    }

    return Optional.of(
      new CALocation(
        id,
        Optional.ofNullable(locRec.getLocationParent()).map(CALocationID::new),
        locRec.getLocationName(),
        locRec.getLocationDescription()
      )
    );
  }

  @Override
  public SortedMap<CALocationID, CALocation> locationList()
    throws CADatabaseException
  {
    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesLocations.locationList");

    try {
      return locationListInner(context);
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(transaction, e);
    } finally {
      querySpan.end();
    }
  }

  private static TreeMap<CALocationID, CALocation> locationListInner(
    final DSLContext context)
  {
    final var results = new TreeMap<CALocationID, CALocation>();
    context.selectFrom(LOCATIONS)
      .stream()
      .forEach(r -> {
        final var loc = new CALocation(
          new CALocationID(r.getLocationId()),
          Optional.ofNullable(r.getLocationParent()).map(CALocationID::new),
          r.getLocationName(),
          r.getLocationDescription()
        );
        results.put(loc.id(), loc);
      });
    return results;
  }

  private record CALocationEdge(
    CALocationID from,
    CALocationID to)
  {

  }
}
