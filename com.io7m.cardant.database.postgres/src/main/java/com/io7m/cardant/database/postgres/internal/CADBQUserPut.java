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
import com.io7m.cardant.database.api.CADatabaseQueriesUsersType.PutType;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.security.CASecurityPolicy;
import com.io7m.medrina.api.MRoleName;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static com.io7m.cardant.database.api.CADatabaseUnit.UNIT;
import static com.io7m.cardant.database.postgres.internal.CADBQAuditEventAdd.auditEvent;
import static com.io7m.cardant.database.postgres.internal.Tables.USERS;

/**
 * Update a user.
 */

public final class CADBQUserPut
  extends CADBQAbstract<CAUser, CADatabaseUnit>
  implements PutType
{
  private static final Service<CAUser, CADatabaseUnit, PutType> SERVICE =
    new Service<>(PutType.class, CADBQUserPut::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQUserPut(
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

  private static String[] roleSetToStringArray(
    final Set<MRoleName> targetRoles)
  {
    final var roles = new String[targetRoles.size()];
    var index = 0;
    for (final var role : targetRoles) {
      roles[index] = role.value().value();
      ++index;
    }
    Arrays.sort(roles);
    return roles;
  }

  @Override
  protected CADatabaseUnit onExecute(
    final DSLContext context,
    final CAUser user)
    throws CADatabaseException
  {
    final var sourceRoles =
      user.subject()
        .roles();

    final Set<MRoleName> targetRoles;
    if (sourceRoles.contains(CASecurityPolicy.ROLE_INVENTORY_ADMIN)) {
      targetRoles = CASecurityPolicy.ROLES_ALL;
    } else {
      targetRoles = sourceRoles;
    }

    final String[] roleArray =
      roleSetToStringArray(targetRoles);

    context.insertInto(USERS)
      .set(USERS.ID, user.userId().id())
      .set(USERS.NAME, user.name().value())
      .set(USERS.ROLES, roleArray)
      .onDuplicateKeyUpdate()
      .set(USERS.NAME, user.name().value())
      .set(USERS.ROLES, roleArray)
      .where(USERS.ID.eq(user.userId().id()))
      .execute();

    auditEvent(
      context,
      OffsetDateTime.now(this.transaction().clock()),
      user.userId(),
      "USER_ROLES_CHANGED",
      Map.entry("Roles", String.join(",", roleArray))
    );
    return UNIT;
  }
}
