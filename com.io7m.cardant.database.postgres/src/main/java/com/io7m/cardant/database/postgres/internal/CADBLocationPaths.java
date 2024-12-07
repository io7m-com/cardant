/*
 * Copyright © 2024 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

import com.io7m.cardant.model.CALocationID;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Select;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.util.UUID;

/**
 * Functions to fetch location paths from the database.
 */

public final class CADBLocationPaths
{
  private CADBLocationPaths()
  {

  }

  /**
   * The location path field resulting from the location path function.
   */

  public static final Field<String[]> LOCATION_PATH_NAME =
    DSL.field(
      DSL.name("r_location_path"),
      SQLDataType.VARCHAR.array()
    );

  /**
   * Find the path for the given location.
   *
   * @param context    The context
   * @param locationID The location
   *
   * @return The resulting path
   */

  public static Select<Record1<String[]>> locationPath(
    final DSLContext context,
    final CALocationID locationID)
  {
    return context.select(LOCATION_PATH_NAME)
      .from(
        DSL.sql(
          "location_path(?)",
          DSL.inline(locationID.id().toString())
        )
      );
  }

  /**
   * Find the path for the given location.
   *
   * @param context    The context
   * @param locationID The location
   *
   * @return The resulting path
   */

  public static Select<Record1<String[]>> locationPathFromColumn(
    final DSLContext context,
    final Field<UUID> locationID)
  {
    return context.select(LOCATION_PATH_NAME)
      .from(DSL.sql("location_path(?)", locationID));
  }

  /**
   * Find the path for the given location.
   *
   * @param context    The context
   * @param locationID The location
   *
   * @return The resulting path
   */

  public static Field<String[]> locationPathNamed(
    final DSLContext context,
    final CALocationID locationID)
  {
    return locationPath(context, locationID)
      .asField(LOCATION_PATH_NAME.getName());
  }

  /**
   * Find the path for the given location.
   *
   * @param context    The context
   * @param locationID The location
   *
   * @return The resulting path
   */

  public static Field<String[]> locationPathFromColumnNamed(
    final DSLContext context,
    final Field<UUID> locationID)
  {
    return locationPathFromColumn(context, locationID)
      .asField(LOCATION_PATH_NAME.getName());
  }
}
