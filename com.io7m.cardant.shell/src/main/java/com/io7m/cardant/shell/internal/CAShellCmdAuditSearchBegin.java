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

import com.io7m.cardant.model.CAAuditSearchParameters;
import com.io7m.cardant.model.CATimeRange;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.model.comparisons.CAComparisonExactType;
import com.io7m.cardant.protocol.inventory.CAICommandAuditSearchBegin;
import com.io7m.cardant.protocol.inventory.CAIResponseAuditSearch;
import com.io7m.idstore.model.IdTimeRange;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed01;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "audit-search-begin"
 */

public final class CAShellCmdAuditSearchBegin
  extends CAShellCmdAbstractCR<CAICommandAuditSearchBegin, CAIResponseAuditSearch>
{
  private static final QParameterNamed1<OffsetDateTime> TIME_FROM =
    new QParameterNamed1<>(
      "--time-from",
      List.of(),
      new QConstant("Return audit events later than this date."),
      Optional.of(IdTimeRange.largest().timeLower()),
      OffsetDateTime.class
    );

  private static final QParameterNamed1<OffsetDateTime> TIME_TO =
    new QParameterNamed1<>(
      "--time-to",
      List.of(),
      new QConstant("Return audit events earlier than this date."),
      Optional.of(IdTimeRange.largest().timeUpper()),
      OffsetDateTime.class
    );

  private static final QParameterNamed01<CAUserID> USER =
    new QParameterNamed01<>(
      "--user",
      List.of(),
      new QConstant("Filter events by user."),
      Optional.empty(),
      CAUserID.class
    );

  private static final QParameterNamed01<String> TYPE_EQUALS =
    new QParameterNamed01<>(
      "--type-equal-to",
      List.of(),
      new QConstant("Filter events by type."),
      Optional.empty(),
      String.class
    );

  private static final QParameterNamed01<String> TYPE_NEQUALS =
    new QParameterNamed01<>(
      "--type-not-equal-to",
      List.of(),
      new QConstant("Filter events by type."),
      Optional.empty(),
      String.class
    );

  private static final QParameterNamed1<Integer> LIMIT =
    new QParameterNamed1<>(
      "--limit",
      List.of(),
      new QConstant("The maximum number of results per page."),
      Optional.of(Integer.valueOf(10)),
      Integer.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The service directory
   */

  public CAShellCmdAuditSearchBegin(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "audit-search-begin",
        new QConstant("Begin searching for audits."),
        Optional.empty()
      ),
      CAICommandAuditSearchBegin.class
    );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(
      LIMIT,
      USER,
      TIME_FROM,
      TIME_TO,
      TYPE_EQUALS,
      TYPE_NEQUALS
    );
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    final var client =
      this.client();

    final var user =
      context.parameterValue(USER);
    final var typeEquals =
      context.parameterValue(TYPE_EQUALS);
    final var typeNequals =
      context.parameterValue(TYPE_NEQUALS);

    final var type =
      typeEquals.map(CAComparisonExactType.IsEqualTo::new)
        .map(x -> (CAComparisonExactType<String>) x)
        .or(() -> typeNequals.map(CAComparisonExactType.IsNotEqualTo::new))
        .orElseGet(CAComparisonExactType.Anything::new);

    final var parameters =
      new CAAuditSearchParameters(
        user,
        type,
        new CATimeRange(
          context.parameterValue(TIME_FROM),
          context.parameterValue(TIME_TO)
        ),
        context.parameterValue(LIMIT).longValue()
      );

    final var page =
      client.sendAndWaitOrThrow(
        new CAICommandAuditSearchBegin(parameters),
        this.commandTimeout()
      ).results();

    this.formatter().formatAuditPage(page);
    return SUCCESS;
  }
}
