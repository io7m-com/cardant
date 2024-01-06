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
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationPutType;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocationSummary;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jooq.DSLContext;
import org.jooq.Query;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.io7m.cardant.database.postgres.internal.CADBQAuditEventAdd.auditEvent;
import static com.io7m.cardant.database.postgres.internal.CADBQLocationMetadataPut.setMetadataValue;
import static com.io7m.cardant.database.postgres.internal.Tables.LOCATIONS;
import static com.io7m.cardant.database.postgres.internal.Tables.LOCATION_METADATA;
import static com.io7m.cardant.database.postgres.internal.Tables.LOCATION_TYPES;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES_RECORDS;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorCyclic;
import static com.io7m.cardant.strings.CAStringConstants.LOCATION_ID;
import static com.io7m.cardant.strings.CAStringConstants.LOCATION_NAME;
import static com.io7m.cardant.strings.CAStringConstants.NEW_LOCATION_ID;
import static com.io7m.cardant.strings.CAStringConstants.NEW_LOCATION_NAME;
import static com.io7m.cardant.strings.CAStringConstants.NEW_LOCATION_PARENT_ID;
import static com.io7m.cardant.strings.CAStringConstants.OLD_LOCATION_ID;
import static com.io7m.cardant.strings.CAStringConstants.OLD_LOCATION_NAME;
import static com.io7m.cardant.strings.CAStringConstants.OLD_LOCATION_PARENT_ID;

/**
 * Create or update a location.
 */

public final class CADBQLocationPut
  extends CADBQAbstract<CALocation, CADatabaseUnit>
  implements LocationPutType
{
  private static final Service<CALocation, CADatabaseUnit, LocationPutType> SERVICE =
    new Service<>(LocationPutType.class, CADBQLocationPut::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQLocationPut(
    final CADatabaseTransaction transaction)
  {
    super(transaction);
  }

  /**
   * @return A query provider
   */

  public static CADBQueryProviderType provider()
  {
    return () -> SERVICE;
  }

  @Override
  protected CADatabaseUnit onExecute(
    final DSLContext context,
    final CALocation location)
    throws CADatabaseException
  {
    this.setAttribute(LOCATION_ID, location.displayId());
    this.setAttribute(LOCATION_NAME, location.name());

    this.checkAcyclic(context, location);

    final var locationId =
      location.id().id();
    final var batches =
      new ArrayList<Query>();

    batches.add(
      context.deleteFrom(LOCATION_METADATA)
        .where(LOCATION_METADATA.LOCATION_META_LOCATION.eq(locationId))
    );
    batches.add(
      context.deleteFrom(LOCATION_TYPES)
        .where(LOCATION_TYPES.LT_LOCATION.eq(locationId))
    );

    batches.add(
      context.insertInto(LOCATIONS)
        .set(
          LOCATIONS.LOCATION_ID,
          locationId)
        .set(
          LOCATIONS.LOCATION_NAME,
          location.name())
        .set(
          LOCATIONS.LOCATION_PARENT,
          location.parent().map(CALocationID::id).orElse(null))
        .onDuplicateKeyUpdate()
        .set(
          LOCATIONS.LOCATION_ID,
          locationId)
        .set(
          LOCATIONS.LOCATION_NAME,
          location.name())
        .set(
          LOCATIONS.LOCATION_PARENT,
          location.parent().map(CALocationID::id).orElse(null))
    );

    for (final var type : location.types()) {
      batches.add(
        context.insertInto(LOCATION_TYPES)
          .set(
            LOCATION_TYPES.LT_LOCATION,
            locationId)
          .set(
            LOCATION_TYPES.LT_TYPE,
            context.select(METADATA_TYPES_RECORDS.MTR_ID)
              .where(METADATA_TYPES_RECORDS.MTR_NAME.eq(type.value()))
          )
      );
    }

    for (final var metaEntry : location.metadata().entrySet()) {
      batches.add(
        setMetadataValue(context, location.id(), metaEntry.getValue())
      );
    }

    final var transaction = this.transaction();
    batches.add(
      auditEvent(
        context,
        OffsetDateTime.now(transaction.clock()),
        transaction.userId(),
        "LOCATION_UPDATED",
        Map.entry("Location", locationId.toString())
      )
    );

    context.batch(batches).execute();
    return CADatabaseUnit.UNIT;
  }

  private void checkAcyclic(
    final DSLContext context,
    final CALocation newLocation)
    throws CADatabaseException
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
      new DirectedAcyclicGraph<CALocationID, CALocationEdge>(
        CALocationEdge.class
      );

    final var locations =
      CADBQLocationList.list(context);

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
      this.setAttribute(NEW_LOCATION_ID, newLocation.displayId());
      this.setAttribute(NEW_LOCATION_NAME, newLocation.name());
      this.setAttribute(NEW_LOCATION_PARENT_ID, newParent.displayId());
      this.setAttribute(OLD_LOCATION_ID, oldLocation.id().displayId());
      this.setAttribute(OLD_LOCATION_NAME, oldLocation.name());
      oldParentOpt.ifPresent(oldParent -> {
        this.setAttribute(OLD_LOCATION_PARENT_ID, oldParent.displayId());
      });

      throw new CADatabaseException(
        e.getMessage(),
        e,
        errorCyclic(),
        this.attributes(),
        Optional.empty()
      );
    }
  }

  static Optional<CALocationSummary> locationGetInner(
    final CALocationID id,
    final DSLContext context)
  {
    return context.select(
        LOCATIONS.LOCATION_PARENT,
        LOCATIONS.LOCATION_NAME
      ).from(LOCATIONS)
      .where(LOCATIONS.LOCATION_ID.eq(id.id()))
      .fetchOptional()
      .map(r -> {
        return new CALocationSummary(
          id,
          Optional.ofNullable(r.get(LOCATIONS.LOCATION_PARENT))
            .map(CALocationID::new),
          r.get(LOCATIONS.LOCATION_NAME)
        );
      });
  }

  private record CALocationEdge(
    CALocationID from,
    CALocationID to)
  {

  }
}
