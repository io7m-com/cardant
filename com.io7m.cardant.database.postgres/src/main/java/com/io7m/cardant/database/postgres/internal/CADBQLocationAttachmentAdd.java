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
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationAttachmentAddType;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationAttachmentAddType.Parameters;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;
import java.util.Map;

import static com.io7m.cardant.database.api.CADatabaseUnit.UNIT;
import static com.io7m.cardant.database.postgres.internal.CADBQAuditEventAdd.auditEvent;
import static com.io7m.cardant.database.postgres.internal.Tables.LOCATIONS;
import static com.io7m.cardant.database.postgres.internal.Tables.LOCATION_ATTACHMENTS;
import static com.io7m.cardant.strings.CAStringConstants.FILE_ID;
import static com.io7m.cardant.strings.CAStringConstants.LOCATION_ID;
import static com.io7m.cardant.strings.CAStringConstants.RELATION;

/**
 * Add an attachment to a location.
 */

public final class CADBQLocationAttachmentAdd
  extends CADBQAbstract<Parameters, CADatabaseUnit>
  implements LocationAttachmentAddType
{
  private static final Service<Parameters, CADatabaseUnit, LocationAttachmentAddType> SERVICE =
    new Service<>(LocationAttachmentAddType.class, CADBQLocationAttachmentAdd::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQLocationAttachmentAdd(
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
    final Parameters parameters)
    throws CADatabaseException
  {
    final var location = parameters.location();
    final var file = parameters.file();
    final var relation = parameters.relation();

    this.setAttribute(LOCATION_ID, location.displayId());
    this.setAttribute(FILE_ID, file.displayId());
    this.setAttribute(RELATION, relation);

    context.insertInto(LOCATION_ATTACHMENTS)
      .set(LOCATION_ATTACHMENTS.LA_LOCATION_ID, location.id())
      .set(LOCATION_ATTACHMENTS.LA_FILE_ID, file.id())
      .set(LOCATION_ATTACHMENTS.LA_RELATION, relation)
      .onDuplicateKeyUpdate()
      .set(LOCATION_ATTACHMENTS.LA_FILE_ID, file.id())
      .set(LOCATION_ATTACHMENTS.LA_RELATION, relation)
      .execute();

    context.update(LOCATIONS)
      .set(LOCATIONS.LOCATION_UPDATED, this.now())
      .where(LOCATIONS.LOCATION_ID.eq(location.id()))
      .execute();

    final var transaction = this.transaction();
    auditEvent(
      context,
      OffsetDateTime.now(transaction.clock()),
      transaction.userId(),
      "LOCATION_ATTACHMENT_UPDATED",
      Map.entry("Location", location.displayId()),
      Map.entry("File", file.displayId())
    ).execute();

    return UNIT;
  }
}
