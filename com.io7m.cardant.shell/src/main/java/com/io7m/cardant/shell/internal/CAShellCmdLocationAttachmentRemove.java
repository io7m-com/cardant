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

import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.protocol.inventory.CAICommandLocationAttachmentAdd;
import com.io7m.cardant.protocol.inventory.CAICommandLocationAttachmentRemove;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationAttachmentAdd;
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
 * "location-attachment-add"
 */

public final class CAShellCmdLocationAttachmentRemove
  extends CAShellCmdAbstractCR<CAICommandLocationAttachmentAdd, CAIResponseLocationAttachmentAdd>
{
  private static final QParameterNamed1<CALocationID> ID =
    new QParameterNamed1<>(
      "--id",
      List.of(),
      new QConstant("The location ID."),
      Optional.empty(),
      CALocationID.class
    );

  private static final QParameterNamed1<CAFileID> FILE =
    new QParameterNamed1<>(
      "--file-id",
      List.of(),
      new QConstant("The file ID."),
      Optional.empty(),
      CAFileID.class
    );

  private static final QParameterNamed1<String> RELATION =
    new QParameterNamed1<>(
      "--relation",
      List.of(),
      new QConstant("The attachment relation."),
      Optional.empty(),
      String.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The context
   */

  public CAShellCmdLocationAttachmentRemove(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "location-attachment-remove",
        new QConstant("Remove an attachment from an location."),
        Optional.empty()
      ),
      CAICommandLocationAttachmentAdd.class
    );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(ID, FILE, RELATION);
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    final var client =
      this.client();

    final var locationID =
      context.parameterValue(ID);
    final var fileID =
      context.parameterValue(FILE);
    final var relation =
      context.parameterValue(RELATION);

    final var location =
      client.sendAndWaitOrThrow(
        new CAICommandLocationAttachmentRemove(locationID, fileID, relation),
        this.commandTimeout()
      ).data();

    this.formatter().formatLocation(location);
    return SUCCESS;
  }
}
