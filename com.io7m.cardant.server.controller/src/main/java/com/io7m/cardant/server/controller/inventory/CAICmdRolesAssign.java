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
import com.io7m.cardant.protocol.inventory.CAICommandRolesAssign;
import com.io7m.cardant.protocol.inventory.CAIResponseRolesAssign;
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
import static com.io7m.cardant.strings.CAStringConstants.ERROR_NONEXISTENT;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_OPERATION_NOT_PERMITTED;
import static com.io7m.cardant.strings.CAStringConstants.USER_ID;

/**
 * @see CAICommandRolesAssign
 */

public final class CAICmdRolesAssign extends CAICmdAbstract<CAICommandRolesAssign>
{
  /**
   * @see CAICommandRolesAssign
   */

  public CAICmdRolesAssign()
  {

  }

  @Override
  protected CAIResponseType executeActual(
    final CAICommandContext context,
    final CAICommandRolesAssign command)
    throws CASecurityException, CADatabaseException, CACommandExecutionFailure
  {
    final var subject =
      context.session()
        .subject();

    /*
     * Does the current subject have all the roles that are being given away,
     * or is the current subject an administrator?
     */

    final var rolesGiven =
      command.roles();
    final var rolesHeld =
      subject.roles();

    if (rolesHeld.contains(ROLE_INVENTORY_ADMIN)) {
      return assignRoles(context, command, rolesGiven);
    }

    if (rolesHeld.containsAll(rolesGiven)) {
      return assignRoles(context, command, rolesGiven);
    }

    throw context.failFormatted(
      400,
      errorOperationNotPermitted(),
      Map.of(USER_ID, command.user().toString()),
      ERROR_OPERATION_NOT_PERMITTED
    );
  }

  private static CAIResponseRolesAssign assignRoles(
    final CAICommandContext context,
    final CAICommandRolesAssign command,
    final Set<MRoleName> rolesGiven)
    throws CADatabaseException, CACommandExecutionFailure
  {
    final var transaction =
      context.transaction();
    final var put =
      transaction.queries(CADatabaseQueriesUsersType.PutType.class);
    final var get =
      transaction.queries(CADatabaseQueriesUsersType.GetType.class);

    final var targetUser =
      get.execute(command.user())
        .orElseThrow(() -> {
          return context.failFormatted(
            400,
            errorNonexistent(),
            Map.of(USER_ID, command.user().toString()),
            ERROR_NONEXISTENT
          );
        });

    final var newRoles = new HashSet<>(targetUser.subject().roles());
    newRoles.addAll(rolesGiven);
    put.execute(
      new CAUser(
        targetUser.userId(),
        targetUser.name(),
        new MSubject(newRoles)
      )
    );
    return new CAIResponseRolesAssign(context.requestId());
  }
}
