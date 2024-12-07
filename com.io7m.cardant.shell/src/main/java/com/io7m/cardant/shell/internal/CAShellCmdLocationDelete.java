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

import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.protocol.inventory.CAICommandLocationDelete;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationDelete;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed01;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.util.List;
import java.util.Optional;

import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "location-delete"
 */

public final class CAShellCmdLocationDelete
  extends CAShellCmdAbstractCR<CAICommandLocationDelete, CAIResponseLocationDelete>
{
  private static final QParameterNamed01<CALocationID> ID =
    new QParameterNamed01<>(
      "--id",
      List.of(),
      new QConstant("The location ID."),
      Optional.empty(),
      CALocationID.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The context
   */

  public CAShellCmdLocationDelete(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "location-delete",
        new QConstant("Delete a location."),
        Optional.empty()
      ),
      CAICommandLocationDelete.class
    );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(ID);
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    final var client =
      this.client();

    final var itemID =
      context.parameterValue(ID)
        .orElseGet(CALocationID::random);

    client.sendAndWaitOrThrow(
      new CAICommandLocationDelete(itemID),
      this.commandTimeout()
    );
    return SUCCESS;
  }
}
