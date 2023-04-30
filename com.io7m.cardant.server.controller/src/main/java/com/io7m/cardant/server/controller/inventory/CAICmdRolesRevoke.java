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

package com.io7m.cardant.server.controller.inventory;

import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseQueriesUsersType;
import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.protocol.inventory.CAICommandRolesRevoke;
import com.io7m.cardant.protocol.inventory.CAIResponseRolesRevoke;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.security.CASecurityException;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.medrina.api.MRoleName;
import com.io7m.medrina.api.MSubject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorOperationNotPermitted;
import static com.io7m.cardant.security.CASecurityPolicy.ROLE_INVENTORY_ADMIN;

/**
 * @see CAICommandRolesRevoke
 */

public final class CAICmdRolesRevoke extends CAICmdAbstract<CAICommandRolesRevoke>
{
  /**
   * @see CAICommandRolesRevoke
   */

  public CAICmdRolesRevoke()
  {

  }

  @Override
  protected CAIResponseType executeActual(
    final CAICommandContext context,
    final CAICommandRolesRevoke command)
    throws CASecurityException, CADatabaseException, CACommandExecutionFailure
  {
    final var subject =
      context.session()
        .subject();

    /*
     * Does the current subject have all the roles that are being revoked,
     * or is the current subject an administrator?
     */

    final var rolesTaken =
      command.roles();
    final var rolesHeld =
      subject.roles();

    if (rolesHeld.contains(ROLE_INVENTORY_ADMIN)) {
      return revokeRoles(context, command, rolesTaken);
    }

    if (rolesHeld.containsAll(rolesTaken)) {
      return revokeRoles(context, command, rolesTaken);
    }

    throw context.failFormatted(
      400,
      errorOperationNotPermitted(),
      Map.of("User ID", command.user().toString()),
      "operationNotPermitted"
    );
  }

  private static CAIResponseRolesRevoke revokeRoles(
    final CAICommandContext context,
    final CAICommandRolesRevoke command,
    final Set<MRoleName> rolesTaken)
    throws CADatabaseException, CACommandExecutionFailure
  {
    final var queries =
      context.transaction()
        .queries(CADatabaseQueriesUsersType.class);

    final var targetUser =
      queries.userGet(command.user())
        .orElseThrow(() -> {
          return context.failFormatted(
            400,
            errorNonexistent(),
            Map.of("User ID", command.user().toString()),
            "notFound"
          );
        });

    final var newRoles = new HashSet<>(targetUser.subject().roles());
    newRoles.removeAll(rolesTaken);
    queries.userPut(new CAUser(targetUser.userId(), new MSubject(newRoles)));
    return new CAIResponseRolesRevoke(context.requestId());
  }
}
