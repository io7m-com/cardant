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


package com.io7m.cardant.shell.internal;

import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.protocol.inventory.CAICommandRolesGet;
import com.io7m.cardant.protocol.inventory.CAIResponseRolesGet;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import org.jline.builtins.Completers;
import org.jline.reader.Completer;

import java.util.List;
import java.util.Optional;

import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "roles-get"
 */

public final class CAShellCmdRolesGet extends CAShellCmdAbstract
{
  private static final QParameterNamed1<CAUserID> USER_ID =
    new QParameterNamed1<>(
      "--user",
      List.of(),
      new QConstant("The user ID."),
      Optional.empty(),
      CAUserID.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The context
   */

  public CAShellCmdRolesGet(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "roles-get",
        new QConstant("Return the set of roles held by the given user."),
        Optional.empty()
      )
    );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(USER_ID);
  }

  @Override
  public Completer completer()
  {
    return new Completers.OptionCompleter(List.of(), 1);
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    final var response =
      (CAIResponseRolesGet)
        this.client().sendAndWaitOrThrow(
          new CAICommandRolesGet(context.parameterValue(USER_ID)),
          this.commandTimeout()
        );

    this.formatter().formatRoles(response.roles());
    return SUCCESS;
  }
}
