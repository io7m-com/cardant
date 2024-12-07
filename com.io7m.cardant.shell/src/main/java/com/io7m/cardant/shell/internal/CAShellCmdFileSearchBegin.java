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

import com.io7m.cardant.model.CADescriptionMatch;
import com.io7m.cardant.model.CAFileColumnOrdering;
import com.io7m.cardant.model.CAFileSearchParameters;
import com.io7m.cardant.model.CAMediaTypeMatch;
import com.io7m.cardant.model.CASizeRange;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType;
import com.io7m.cardant.protocol.inventory.CAICommandFileSearchBegin;
import com.io7m.cardant.protocol.inventory.CAIResponseFileSearch;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;

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
  private static final QParameterNamed1<CADescriptionMatch> DESCRIPTION_MATCH =
    new QParameterNamed1<>(
      "--description-match",
      List.of(),
      new QConstant(
        "Only include files that have descriptions matching the given expression."),
      Optional.of(new CADescriptionMatch(new CAComparisonFuzzyType.Anything<>())),
      CADescriptionMatch.class
    );

  private static final QParameterNamed1<CAMediaTypeMatch> MEDIATYPE_MATCH =
    new QParameterNamed1<>(
      "--mediatype-match",
      List.of(),
      new QConstant(
        "Only include files that have media types matching the given expression."),
      Optional.of(new CAMediaTypeMatch(new CAComparisonFuzzyType.Anything<>())),
      CAMediaTypeMatch.class
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

  private static final QParameterNamed1<Long> LIMIT =
    new QParameterNamed1<>(
      "--limit",
      List.of(),
      new QConstant("The maximum number of results per page."),
      Optional.of(Long.valueOf(100L)),
      Long.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The context
   */

  public CAShellCmdFileSearchBegin(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "file-search-begin",
        new QConstant("Start searching for files."),
        Optional.empty()
      ),
      CAICommandFileSearchBegin.class
    );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(
      DESCRIPTION_MATCH,
      MEDIATYPE_MATCH,
      LIMIT,
      SIZE_MINIMUM,
      SIZE_MAXIMUM
    );
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    final var client =
      this.client();
    final var descriptionMatch =
      context.parameterValue(DESCRIPTION_MATCH);
    final var mediaMatch =
      context.parameterValue(MEDIATYPE_MATCH);

    final var parameters =
      new CAFileSearchParameters(
        descriptionMatch.expression(),
        mediaMatch.expression(),
        new CASizeRange(
          context.parameterValue(SIZE_MINIMUM).longValue(),
          context.parameterValue(SIZE_MAXIMUM).longValue()
        ),
        new CAFileColumnOrdering(BY_DESCRIPTION, true),
        context.parameterValue(LIMIT).longValue()
      );

    final var files =
      client.sendAndWaitOrThrow(
        new CAICommandFileSearchBegin(parameters),
        this.commandTimeout()
      ).data();

    this.formatter().formatFilesPage(files);
    return SUCCESS;
  }
}
