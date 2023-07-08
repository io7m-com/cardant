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
import com.io7m.cardant.model.CATypeDeclarationSearchParameters;
import com.io7m.cardant.protocol.inventory.CAICommandTypeDeclarationSearchBegin;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeDeclarationSearch;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed01;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.util.List;
import java.util.Optional;

import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "type-search-begin"
 */

public final class CAShellCmdTypeDeclarationSearchBegin
  extends CAShellCmdAbstractCR<
  CAICommandTypeDeclarationSearchBegin, CAIResponseTypeDeclarationSearch>
{
  private static final QParameterNamed01<String> SEARCH =
    new QParameterNamed01<>(
      "--query",
      List.of(),
      new QConstant("The type search query."),
      Optional.empty(),
      String.class
    );

  private static final QParameterNamed1<Integer> LIMIT =
    new QParameterNamed1<>(
      "--limit",
      List.of(),
      new QConstant("The maximum number of results per page."),
      Optional.of(Integer.valueOf(100)),
      Integer.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The shell context
   */

  public CAShellCmdTypeDeclarationSearchBegin(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "type-search-begin",
        new QConstant("Start searching for types"),
        Optional.empty()
      ),
      CAICommandTypeDeclarationSearchBegin.class
    );
  }


  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(SEARCH, LIMIT);
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    final var client =
      this.client();

    final var search =
      context.parameterValue(SEARCH);
    final var limit =
      context.parameterValue(LIMIT);

    final var type =
      ((CAIResponseTypeDeclarationSearch) client.executeOrElseThrow(
        new CAICommandTypeDeclarationSearchBegin(
          new CATypeDeclarationSearchParameters(search, limit.intValue())
        ),
        CAClientException::ofError
      )).data();

    this.formatter().formatTypeDeclarationPage(type);
    return SUCCESS;
  }
}
