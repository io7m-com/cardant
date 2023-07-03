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
import com.io7m.cardant.model.CAFileColumnOrdering;
import com.io7m.cardant.model.CAFileSearchParameters;
import com.io7m.cardant.model.CASizeRange;
import com.io7m.cardant.protocol.inventory.CAICommandFileSearchBegin;
import com.io7m.cardant.protocol.inventory.CAIResponseFileSearch;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed01;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;

import java.util.List;
import java.util.Optional;

import static com.io7m.cardant.model.CAFileColumn.BY_DESCRIPTION;
import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "file-search-begin"
 */

public final class CAShellCmdFileSearchBegin
  extends CAShellCmdAbstractCR<CAICommandFileSearchBegin, CAIResponseFileSearch>
{
  private static final QParameterNamed01<String> DESCRIPTION =
    new QParameterNamed01<>(
      "--description",
      List.of(),
      new QConstant("The file description search query."),
      Optional.empty(),
      String.class
    );

  private static final QParameterNamed01<String> MEDIA_TYPE =
    new QParameterNamed01<>(
      "--media-type",
      List.of(),
      new QConstant("The file media type search query."),
      Optional.empty(),
      String.class
    );

  private static final QParameterNamed1<Long> SIZE_MINIMUM =
    new QParameterNamed1<>(
      "--size-minimum",
      List.of(),
      new QConstant("The minimum file size."),
      Optional.of(Long.valueOf(0L)),
      Long.class
    );

  private static final QParameterNamed1<Long> SIZE_MAXIMUM =
    new QParameterNamed1<>(
      "--size-maximum",
      List.of(),
      new QConstant("The maximum file size."),
      Optional.of(Long.valueOf(Long.MAX_VALUE)),
      Long.class
    );

  /**
   * Construct a command.
   *
   * @param inContext The context
   */

  public CAShellCmdFileSearchBegin(
    final CAShellContextType inContext)
  {
    super(
      inContext,
      new QCommandMetadata(
        "file-search-begin",
        new QConstant("Start searching for files."),
        Optional.empty()
      ),
      CAICommandFileSearchBegin.class,
      CAIResponseFileSearch.class
    );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(DESCRIPTION, MEDIA_TYPE, SIZE_MINIMUM, SIZE_MAXIMUM);
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    final var client =
      this.client();
    
    final var parameters =
      new CAFileSearchParameters(
        context.parameterValue(DESCRIPTION),
        context.parameterValue(MEDIA_TYPE),
        Optional.of(
          new CASizeRange(
            context.parameterValue(SIZE_MINIMUM).longValue(),
            context.parameterValue(SIZE_MAXIMUM).longValue()
          )
        ),
        new CAFileColumnOrdering(BY_DESCRIPTION, true),
        100
      );

    final var files =
      ((CAIResponseFileSearch) client.executeOrElseThrow(
        new CAICommandFileSearchBegin(parameters),
        CAClientException::ofError
      )).data();

    this.formatter().formatFilesPage(files);
    return SUCCESS;
  }
}
