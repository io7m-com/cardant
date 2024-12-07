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
import com.io7m.cardant.model.CAItemSerial;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CAStockInstanceID;
import com.io7m.cardant.model.CAStockRepositSerialIntroduce;
import com.io7m.cardant.protocol.inventory.CAICommandStockReposit;
import com.io7m.cardant.protocol.inventory.CAIResponseStockReposit;
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
 * "stock-reposit-serial-introduce"
 */

public final class CAShellCmdStockRepositSerialIntroduce
  extends CAShellCmdAbstractCR<CAICommandStockReposit, CAIResponseStockReposit>
{
  private static final QParameterNamed1<CAItemID> ITEM =
    new QParameterNamed1<>(
      "--item",
      List.of(),
      new QConstant("The item ID."),
      Optional.empty(),
      CAItemID.class
    );

  private static final QParameterNamed1<CAStockInstanceID> INSTANCE =
    new QParameterNamed1<>(
      "--instance",
      List.of(),
      new QConstant("The stock instance ID."),
      Optional.empty(),
      CAStockInstanceID.class
    );

  private static final QParameterNamed1<CALocationID> LOCATION =
    new QParameterNamed1<>(
      "--location",
      List.of(),
      new QConstant("The location ID."),
      Optional.empty(),
      CALocationID.class
    );

  private static final QParameterNamed1<CAItemSerial> SERIAL =
    new QParameterNamed1<>(
      "--serial",
      List.of(),
      new QConstant("The item serial number."),
      Optional.empty(),
      CAItemSerial.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The context
   */

  public CAShellCmdStockRepositSerialIntroduce(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "stock-reposit-serial-introduce",
        new QConstant("Add an instance of an item to a location."),
        Optional.empty()
      ),
      CAICommandStockReposit.class
    );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(ITEM, INSTANCE, LOCATION, SERIAL);
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
    final var instance =
      context.parameterValue(INSTANCE);
    final var serial =
      context.parameterValue(SERIAL);

    final var item =
      client.sendAndWaitOrThrow(
        new CAICommandStockReposit(
          new CAStockRepositSerialIntroduce(
            instance,
            itemID,
            locationID,
            serial
          )
        ),
        this.commandTimeout()
      ).data();

    this.formatter().formatStock(item);
    return SUCCESS;
  }
}
