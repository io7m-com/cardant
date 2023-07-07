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
import com.io7m.cardant.database.api.CADatabaseQueriesUsersType.GetType;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.model.CAUser;
import com.io7m.idstore.model.IdName;
import com.io7m.idstore.model.IdValidityException;
import com.io7m.medrina.api.MRoleName;
import com.io7m.medrina.api.MSubject;
import io.opentelemetry.api.trace.Span;
import org.jooq.DSLContext;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.io7m.cardant.database.postgres.internal.Tables.USERS;

/**
 * Retrieve a user.
 */

public final class CADBQUserGet
  extends CADBQAbstract<UUID, Optional<CAUser>>
  implements GetType
{
  private static final Service<UUID, Optional<CAUser>, GetType> SERVICE =
    new Service<>(GetType.class, CADBQUserGet::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQUserGet(
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
  protected Optional<CAUser> onExecute(
    final DSLContext context,
    final UUID id)
    throws CADatabaseException
  {
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
              .map(MRoleName::of)
              .collect(Collectors.toUnmodifiableSet())
          )
        )
      );
    } catch (final IdValidityException e) {
      Span.current().recordException(e);
      throw new CADatabaseException(
        e.getMessage(),
        e,
        CAStandardErrorCodes.errorProtocol(),
        this.attributes(),
        Optional.empty()
      );
    }
  }
}
