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

package com.io7m.cardant.tests.database;

import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers(disabledWithoutDocker = true)
@ExtendWith(CADatabaseExtension.class)
public final class CADatabaseLocationsTest
{
  /**
   * Creating locations works.
   *
   * @param transaction The transaction
   *
   * @throws Exception On errors
   */

  @Test
  public void testLocationCreate(
    final CADatabaseTransactionType transaction)
    throws Exception
  {
    final var q =
      transaction.queries(CADatabaseQueriesLocationsType.class);

    final var loc0 =
      new CALocation(
        CALocationID.random(),
        Optional.empty(),
        "Loc0",
        "Location 0"
      );
    final var loc1 =
      new CALocation(
        CALocationID.random(),
        Optional.empty(),
        "Loc1",
        "Location 1"
      );
    final var loc2 =
      new CALocation(
        CALocationID.random(),
        Optional.empty(),
        "Loc2",
        "Location 2"
      );

    q.locationPut(loc0);
    q.locationPut(loc1);
    q.locationPut(loc2);

    q.locationPut(loc0);
    q.locationPut(loc1);
    q.locationPut(loc2);

    final var r = new TreeMap<CALocationID, CALocation>();
    r.put(loc0.id(), loc0);
    r.put(loc1.id(), loc1);
    r.put(loc2.id(), loc2);

    assertEquals(r, q.locationList());

    assertEquals(loc0, q.locationGet(loc0.id()).orElseThrow());
    assertEquals(loc1, q.locationGet(loc1.id()).orElseThrow());
    assertEquals(loc2, q.locationGet(loc2.id()).orElseThrow());
  }

  /**
   * Removing a location parent works.
   *
   * @param transaction The transaction
   *
   * @throws Exception On errors
   */

  @Test
  public void testLocationRemoveParent(
    final CADatabaseTransactionType transaction)
    throws Exception
  {
    final var q =
      transaction.queries(CADatabaseQueriesLocationsType.class);

    final var loc0 =
      new CALocation(
        CALocationID.random(),
        Optional.empty(),
        "Loc0",
        "Location 0"
      );
    final var loc1with =
      new CALocation(
        CALocationID.random(),
        Optional.of(loc0.id()),
        "Loc1",
        "Location 1"
      );
    final var loc1without =
      new CALocation(
        loc1with.id(),
        Optional.empty(),
        "Loc1",
        "Location 1"
      );


    q.locationPut(loc0);
    q.locationPut(loc1with);
    q.locationPut(loc1without);

    final var r = new TreeMap<CALocationID, CALocation>();
    r.put(loc0.id(), loc0);
    r.put(loc1without.id(), loc1without);

    assertEquals(r, q.locationList());
  }

  /**
   * Creating a location cycle fails.
   *
   * @param transaction The transaction
   *
   * @throws Exception On errors
   */

  @Test
  public void testLocationCyclic0(
    final CADatabaseTransactionType transaction)
    throws Exception
  {
    final var q =
      transaction.queries(CADatabaseQueriesLocationsType.class);

    final var loc0 =
      new CALocation(
        CALocationID.random(),
        Optional.empty(),
        "Loc0",
        "Location 0"
      );
    final var loc1 =
      new CALocation(
        CALocationID.random(),
        Optional.of(loc0.id()),
        "Loc1",
        "Location 1"
      );
    final var loc2 =
      new CALocation(
        CALocationID.random(),
        Optional.of(loc1.id()),
        "Loc2",
        "Location 2"
      );
    final var loc0cyc =
      new CALocation(
        loc0.id(),
        Optional.of(loc2.id()),
        "Loc0",
        "Location 0"
      );

    q.locationPut(loc0);
    q.locationPut(loc1);
    q.locationPut(loc2);

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        q.locationPut(loc0cyc);
      });
    assertEquals(CAStandardErrorCodes.errorCyclic(), ex.errorCode());
  }
}
