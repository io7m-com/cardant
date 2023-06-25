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
import com.io7m.cardant.database.api.CADatabaseQueriesMaintenanceType;
import com.io7m.cardant.security.CASecurityPolicy;
import com.io7m.jdeferthrow.core.ExceptionTracker;
import com.io7m.lanark.core.RDottedName;
import com.io7m.medrina.api.MRoleName;
import org.jooq.exception.DataAccessException;

import static com.io7m.cardant.database.postgres.internal.CADatabaseExceptions.handleDatabaseException;
import static com.io7m.cardant.database.postgres.internal.tables.Users.USERS;
import static java.util.Collections.emptySortedMap;

/**
 * Maintenance related queries.
 */

public final class CADatabaseQueriesMaintenance
  extends CABaseQueries
  implements CADatabaseQueriesMaintenanceType
{
  CADatabaseQueriesMaintenance(
    final CADatabaseTransaction inTransaction)
  {
    super(inTransaction);
  }

  /**
   * Run maintenance tasks.
   *
   * @throws CADatabaseException On errors
   */

  @Override
  public void runMaintenance()
    throws CADatabaseException
  {
    final var exceptions =
      new ExceptionTracker<CADatabaseException>();

    try {
      this.runUpdateAdminRoles();
    } catch (final CADatabaseException e) {
      exceptions.addException(e);
    }

    exceptions.throwIfNecessary();
  }

  private void runUpdateAdminRoles()
    throws CADatabaseException
  {
    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesMaintenance.runUpdateAdminRoles");

    try {
      final var roleArray =
        new String[CASecurityPolicy.ROLES_ALL.size()];

      final var allRoles =
        CASecurityPolicy.ROLES_ALL.stream()
          .sorted()
          .map(MRoleName::value)
          .map(RDottedName::value)
          .toList()
          .toArray(roleArray);

      final var hasAdmin =
        USERS.ROLES.contains(new String[]{
          CASecurityPolicy.ROLE_INVENTORY_ADMIN.value().value(),
        });

      context.update(USERS)
        .set(USERS.ROLES, allRoles)
        .where(hasAdmin)
        .execute();

    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(transaction, e, emptySortedMap());
    } finally {
      querySpan.end();
    }
  }
}
