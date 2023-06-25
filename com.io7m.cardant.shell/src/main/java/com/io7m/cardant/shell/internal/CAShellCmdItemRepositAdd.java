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
import com.io7m.cardant.client.api.CAClientSynchronousType;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemRepositAdd;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.protocol.inventory.CAICommandItemReposit;
import com.io7m.cardant.protocol.inventory.CAIResponseItemReposit;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;

import java.util.List;
import java.util.Optional;

import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "item-reposit-add"
 */

public final class CAShellCmdItemRepositAdd
  extends CAShellCmdAbstract<CAICommandItemReposit, CAIResponseItemReposit>
{
  private static final QParameterNamed1<CAItemID> ITEM =
    new QParameterNamed1<>(
      "--item",
      List.of(),
      new QConstant("The item ID."),
      Optional.empty(),
      CAItemID.class
    );

  private static final QParameterNamed1<CALocationID> LOCATION =
    new QParameterNamed1<>(
      "--location",
      List.of(),
      new QConstant("The location ID."),
      Optional.empty(),
      CALocationID.class
    );

  private static final QParameterNamed1<Long> COUNT =
    new QParameterNamed1<>(
      "--count",
      List.of(),
      new QConstant("The number of instances of the item to add."),
      Optional.empty(),
      Long.class
    );

  /**
   * Construct a command.
   *
   * @param inClient The client
   */

  public CAShellCmdItemRepositAdd(
    final CAClientSynchronousType inClient)
  {
    super(
      inClient,
      new QCommandMetadata(
        "item-reposit-add",
        new QConstant("Add instances of items to locations."),
        Optional.empty()
      ),
      CAICommandItemReposit.class,
      CAIResponseItemReposit.class
    );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(ITEM, LOCATION, COUNT);
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    final var client =
      this.client();

    final var itemID =
      context.parameterValue(ITEM);
    final var locationID =
      context.parameterValue(LOCATION);
    final var count =
      context.parameterValue(COUNT);

    final var item =
      ((CAIResponseItemReposit) client.executeOrElseThrow(
        new CAICommandItemReposit(
          new CAItemRepositAdd(itemID, locationID, count.longValue())
        ),
        CAClientException::ofError
      )).data();

    CAItemFormatting.formatItem(context.output(), item);
    return SUCCESS;
  }
}
