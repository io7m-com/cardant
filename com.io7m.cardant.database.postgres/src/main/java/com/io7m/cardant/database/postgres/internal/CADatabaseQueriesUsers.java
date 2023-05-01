/*
 * Copyright © 2022 Mark Raynsford <code@io7m.com> https://www.io7m.com
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
import com.io7m.cardant.database.api.CADatabaseQueriesUsersType;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.security.CASecurityPolicy;
import com.io7m.idstore.model.IdName;
import com.io7m.idstore.model.IdValidityException;
import com.io7m.medrina.api.MRoleName;
import com.io7m.medrina.api.MSubject;
import org.jooq.exception.DataAccessException;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.io7m.cardant.database.postgres.internal.CADatabaseExceptions.handleDatabaseException;
import static com.io7m.cardant.database.postgres.internal.Tables.AUDIT;
import static com.io7m.cardant.database.postgres.internal.Tables.USERS;

final class CADatabaseQueriesUsers
  extends CABaseQueries
  implements CADatabaseQueriesUsersType
{
  CADatabaseQueriesUsers(
    final CADatabaseTransaction inTransaction)
  {
    super(inTransaction);
  }

  @Override
  public void userPut(
    final CAUser user)
    throws CADatabaseException
  {
    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan("CADatabaseUsersQueries.userPut");

    try {
      var userRec = context.fetchOne(USERS, USERS.ID.eq(user.userId()));
      if (userRec == null) {
        userRec = context.newRecord(USERS);
        userRec.set(USERS.ID, user.userId());
        userRec.set(USERS.NAME, user.name().value());

        final var auditRec = context.newRecord(AUDIT);
        auditRec.setUserId(transaction.userId());
        auditRec.setTime(OffsetDateTime.now(transaction.clock()));
        auditRec.setType("USER_FIRST_LOGIN");
        auditRec.setMessage(user.userId().toString());
        auditRec.store();
      }

      final var sourceRoles =
        user.subject()
          .roles();

      final Set<MRoleName> targetRoles;
      if (sourceRoles.contains(CASecurityPolicy.ROLE_INVENTORY_ADMIN)) {
        targetRoles = CASecurityPolicy.ROLES_ALL;
      } else {
        targetRoles = sourceRoles;
      }

      final String[] roles =
        roleSetToStringArray(targetRoles);

      userRec.set(USERS.NAME, user.name().value());
      userRec.setRoles(roles);
      userRec.store();

      final var auditRec = context.newRecord(AUDIT);
      auditRec.setUserId(transaction.userId());
      auditRec.setTime(OffsetDateTime.now(transaction.clock()));
      auditRec.setType("USER_ROLES_CHANGED");
      auditRec.setMessage(String.join(",", roles));
      auditRec.store();

    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(transaction, e);
    } finally {
      querySpan.end();
    }
  }

  private static String[] roleSetToStringArray(
    final Set<MRoleName> targetRoles)
  {
    final var roles = new String[targetRoles.size()];
    var index = 0;
    for (final var role : targetRoles) {
      roles[index] = role.value();
      ++index;
    }
    Arrays.sort(roles);
    return roles;
  }

  @Override
  public Optional<CAUser> userGet(
    final UUID id)
    throws CADatabaseException
  {
    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan("CADatabaseUsersQueries.userGet");

    try {
      final var userRec = context.fetchOne(USERS, USERS.ID.eq(id));
      if (userRec == null) {
        return Optional.empty();
      }

      return Optional.of(
        new CAUser(
          id,
          new IdName(userRec.get(USERS.NAME)),
          new MSubject(
            Stream.of(userRec.getRoles())
              .map(MRoleName::new)
              .collect(Collectors.toUnmodifiableSet())
          )
        )
      );
    } catch (final IdValidityException e) {
      querySpan.recordException(e);
      throw new CADatabaseException(
        e.getMessage(),
        e,
        CAStandardErrorCodes.errorProtocol(),
        Map.of(),
        Optional.empty()
      );
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(transaction, e);
    } finally {
      querySpan.end();
    }
  }
}
