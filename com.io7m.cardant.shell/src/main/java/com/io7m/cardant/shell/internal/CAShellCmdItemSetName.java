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

import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.protocol.inventory.CAICommandItemGet;
import com.io7m.cardant.protocol.inventory.CAICommandItemSetName;
import com.io7m.cardant.protocol.inventory.CAIResponseItemSetName;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.util.List;
import java.util.Optional;

import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "item-set-name"
 */

public final class CAShellCmdItemSetName
  extends CAShellCmdAbstractCR<CAICommandItemSetName, CAIResponseItemSetName>
{
  private static final QParameterNamed1<CAItemID> ID =
    new QParameterNamed1<>(
      "--id",
      List.of(),
      new QConstant("The item ID."),
      Optional.empty(),
      CAItemID.class
    );

  private static final QParameterNamed1<String> NAME =
    new QParameterNamed1<>(
      "--name",
      List.of(),
      new QConstant("The item name."),
      Optional.empty(),
      String.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The context
   */

  public CAShellCmdItemSetName(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "item-set-name",
        new QConstant("Set the name of an item."),
        Optional.empty()
      ),
      CAICommandItemSetName.class
    );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(ID, NAME);
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    final var client =
      this.client();

    final var itemID =
      context.parameterValue(ID);
    final var name =
      context.parameterValue(NAME);

    client.sendAndWaitOrThrow(
      new CAICommandItemSetName(itemID, name),
      this.commandTimeout()
    );

    final var item =
      client.sendAndWaitOrThrow(
        new CAICommandItemGet(itemID),
        this.commandTimeout()
      ).data();

    this.formatter().formatItem(item);
    return SUCCESS;
  }
}
