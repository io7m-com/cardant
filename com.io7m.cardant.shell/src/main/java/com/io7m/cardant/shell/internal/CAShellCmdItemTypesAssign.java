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

import com.io7m.cardant.client.api.CAClientException;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CATypeRecordIdentifier;
import com.io7m.cardant.protocol.inventory.CAICommandItemTypesAssign;
import com.io7m.cardant.protocol.inventory.CAIResponseItemTypesAssign;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed0N;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "item-types-assign"
 */

public final class CAShellCmdItemTypesAssign
  extends CAShellCmdAbstractCR<CAICommandItemTypesAssign, CAIResponseItemTypesAssign>
{
  private static final QParameterNamed1<CAItemID> ID =
    new QParameterNamed1<>(
      "--id",
      List.of(),
      new QConstant("The item ID."),
      Optional.empty(),
      CAItemID.class
    );

  private static final QParameterNamed0N<CATypeRecordIdentifier> TYPES =
    new QParameterNamed0N<>(
      "--type",
      List.of(),
      new QConstant("The item types."),
      List.of(),
      CATypeRecordIdentifier.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The context
   */

  public CAShellCmdItemTypesAssign(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "item-types-assign",
        new QConstant("Assign types to an item."),
        Optional.empty()
      ),
      CAICommandItemTypesAssign.class
    );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(ID, TYPES);
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
    final var types =
      context.parameterValues(TYPES);

    final var item =
      ((CAIResponseItemTypesAssign) client.executeOrElseThrow(
        new CAICommandItemTypesAssign(itemID, Set.copyOf(types)),
        CAClientException::ofError
      )).data();

    this.formatter().formatItem(item);
    return SUCCESS;
  }
}
