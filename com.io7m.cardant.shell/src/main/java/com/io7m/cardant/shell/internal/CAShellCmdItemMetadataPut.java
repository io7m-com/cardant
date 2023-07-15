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
import com.io7m.cardant.model.CAMetadata;
import com.io7m.cardant.protocol.inventory.CAICommandItemMetadataPut;
import com.io7m.cardant.protocol.inventory.CAIResponseItemMetadataPut;
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
 * "item-metadata-put"
 */

public final class CAShellCmdItemMetadataPut
  extends CAShellCmdAbstractCR<CAICommandItemMetadataPut, CAIResponseItemMetadataPut>
{
  private static final QParameterNamed1<CAItemID> ID =
    new QParameterNamed1<>(
      "--id",
      List.of(),
      new QConstant("The item ID."),
      Optional.empty(),
      CAItemID.class
    );

  private static final QParameterNamed0N<CAMetadata> METADATA =
    new QParameterNamed0N<>(
      "--metadata",
      List.of(),
      new QConstant("The metadata key."),
      List.of(),
      CAMetadata.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The context
   */

  public CAShellCmdItemMetadataPut(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "item-metadata-put",
        new QConstant("Add or update metadata on an item."),
        Optional.empty()
      ),
      CAICommandItemMetadataPut.class
    );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(ID, METADATA);
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
    final var metas =
      context.parameterValues(METADATA);

    final var meta =
      Set.copyOf(metas);

    final var item =
      ((CAIResponseItemMetadataPut) client.executeOrElseThrow(
        new CAICommandItemMetadataPut(itemID, meta),
        CAClientException::ofError
      )).data();

    this.formatter().formatItem(item);
    return SUCCESS;
  }
}
