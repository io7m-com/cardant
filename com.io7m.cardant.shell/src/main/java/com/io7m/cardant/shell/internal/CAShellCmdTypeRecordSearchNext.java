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
import com.io7m.cardant.protocol.inventory.CAICommandTypeRecordSearchNext;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeRecordSearch;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.util.List;
import java.util.Optional;

import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "type-record-search-next"
 */

public final class CAShellCmdTypeRecordSearchNext
  extends CAShellCmdAbstractCR<CAICommandTypeRecordSearchNext, CAIResponseTypeRecordSearch>
{
  /**
   * Construct a command.
   *
   * @param inServices The context
   */

  public CAShellCmdTypeRecordSearchNext(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "type-record-search-next",
        new QConstant("Go to the next page of types."),
        Optional.empty()
      ),
      CAICommandTypeRecordSearchNext.class
    );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of();
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    final var client =
      this.client();

    final var items =
      ((CAIResponseTypeRecordSearch) client.executeOrElseThrow(
        new CAICommandTypeRecordSearchNext(),
        CAClientException::ofError
      )).data();

    this.formatter().formatTypeDeclarationPage(items);
    return SUCCESS;
  }
}
