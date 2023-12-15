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
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType;
import com.io7m.cardant.protocol.inventory.CAICommandFileSearchBegin;
import com.io7m.cardant.protocol.inventory.CAIResponseFileSearch;
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

import static com.io7m.cardant.model.CAFileColumn.BY_DESCRIPTION;
import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "file-search-begin"
 */

public final class CAShellCmdFileSearchBegin
  extends CAShellCmdAbstractCR<CAICommandFileSearchBegin, CAIResponseFileSearch>
{
  private static final QParameterNamed01<String> DESCRIPTION_EQUALS =
    new QParameterNamed01<>(
      "--description-equal-to",
      List.of(),
      new QConstant("Filter files by description."),
      Optional.empty(),
      String.class
    );

  private static final QParameterNamed01<String> DESCRIPTION_NEQUALS =
    new QParameterNamed01<>(
      "--description-not-equal-to",
      List.of(),
      new QConstant("Filter files by description."),
      Optional.empty(),
      String.class
    );

  private static final QParameterNamed01<String> DESCRIPTION_SIMILAR =
    new QParameterNamed01<>(
      "--description-similar-to",
      List.of(),
      new QConstant("Filter files by description."),
      Optional.empty(),
      String.class
    );

  private static final QParameterNamed01<String> DESCRIPTION_NOT_SIMILAR =
    new QParameterNamed01<>(
      "--description-not-similar-to",
      List.of(),
      new QConstant("Filter files by description."),
      Optional.empty(),
      String.class
    );

  private static final QParameterNamed01<String> MEDIA_EQUALS =
    new QParameterNamed01<>(
      "--media-equal-to",
      List.of(),
      new QConstant("Filter files by media type."),
      Optional.empty(),
      String.class
    );

  private static final QParameterNamed01<String> MEDIA_NEQUALS =
    new QParameterNamed01<>(
      "--media-not-equal-to",
      List.of(),
      new QConstant("Filter files by media type."),
      Optional.empty(),
      String.class
    );

  private static final QParameterNamed01<String> MEDIA_SIMILAR =
    new QParameterNamed01<>(
      "--media-similar-to",
      List.of(),
      new QConstant("Filter files by media type."),
      Optional.empty(),
      String.class
    );

  private static final QParameterNamed01<String> MEDIA_NOT_SIMILAR =
    new QParameterNamed01<>(
      "--media-not-similar-to",
      List.of(),
      new QConstant("Filter files by media type."),
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
      DESCRIPTION_EQUALS,
      DESCRIPTION_NEQUALS,
      DESCRIPTION_SIMILAR,
      DESCRIPTION_NOT_SIMILAR,
      MEDIA_EQUALS,
      MEDIA_NEQUALS,
      MEDIA_SIMILAR,
      MEDIA_NOT_SIMILAR,
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

    final var descriptionEquals =
      context.parameterValue(DESCRIPTION_EQUALS)
        .map(CAComparisonFuzzyType.IsEqualTo::new)
        .map(x -> (CAComparisonFuzzyType<String>) x);
    final var descriptionNequals =
      context.parameterValue(DESCRIPTION_NEQUALS)
        .map(CAComparisonFuzzyType.IsNotEqualTo::new)
        .map(x -> (CAComparisonFuzzyType<String>) x);
    final var descriptionSimilar =
      context.parameterValue(DESCRIPTION_SIMILAR)
        .map(CAComparisonFuzzyType.IsSimilarTo::new)
        .map(x -> (CAComparisonFuzzyType<String>) x);
    final var descriptionNotSimilar =
      context.parameterValue(DESCRIPTION_NOT_SIMILAR)
        .map(CAComparisonFuzzyType.IsNotSimilarTo::new)
        .map(x -> (CAComparisonFuzzyType<String>) x);
    final var descriptionMatch =
      descriptionEquals
        .or(() -> descriptionNequals)
        .or(() -> descriptionSimilar)
        .or(() -> descriptionNotSimilar)
        .orElseGet(CAComparisonFuzzyType.Anything::new);

    final var mediaEquals =
      context.parameterValue(MEDIA_EQUALS)
        .map(CAComparisonFuzzyType.IsEqualTo::new)
        .map(x -> (CAComparisonFuzzyType<String>) x);
    final var mediaNequals =
      context.parameterValue(MEDIA_NEQUALS)
        .map(CAComparisonFuzzyType.IsNotEqualTo::new)
        .map(x -> (CAComparisonFuzzyType<String>) x);
    final var mediaSimilar =
      context.parameterValue(MEDIA_SIMILAR)
        .map(CAComparisonFuzzyType.IsSimilarTo::new)
        .map(x -> (CAComparisonFuzzyType<String>) x);
    final var mediaNotSimilar =
      context.parameterValue(MEDIA_NOT_SIMILAR)
        .map(CAComparisonFuzzyType.IsNotSimilarTo::new)
        .map(x -> (CAComparisonFuzzyType<String>) x);
    final var mediaMatch =
      mediaEquals
        .or(() -> mediaNequals)
        .or(() -> mediaSimilar)
        .or(() -> mediaNotSimilar)
        .orElseGet(CAComparisonFuzzyType.Anything::new);

    final var parameters =
      new CAFileSearchParameters(
        descriptionMatch,
        mediaMatch,
        new CASizeRange(
          context.parameterValue(SIZE_MINIMUM).longValue(),
          context.parameterValue(SIZE_MAXIMUM).longValue()
        ),
        new CAFileColumnOrdering(BY_DESCRIPTION, true),
        context.parameterValue(LIMIT).longValue()
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
