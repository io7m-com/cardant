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

import com.io7m.cardant.client.api.CAClientSynchronousType;
import com.io7m.cardant.client.api.CAClientTransferStatistics;
import com.io7m.cardant.model.CAFileID;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QParameterType;
import com.io7m.quarrel.core.QStringType.QConstant;
import org.jline.reader.Completer;
import org.jline.reader.impl.completer.StringsCompleter;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "file-put"
 */

public final class CAShellCmdFilePut implements CAShellCmdType
{
  private static final QParameterNamed1<CAFileID> FILE_ID =
    new QParameterNamed1<>(
      "--id",
      List.of(),
      new QConstant("The file ID."),
      Optional.empty(),
      CAFileID.class
    );

  private static final QParameterNamed1<Path> FILE =
    new QParameterNamed1<>(
      "--file",
      List.of(),
      new QConstant("The file."),
      Optional.empty(),
      Path.class
    );

  private final CAClientSynchronousType client;
  private final QCommandMetadata metadata;

  /**
   * Construct a command.
   *
   * @param inClient The client
   */

  public CAShellCmdFilePut(
    final CAClientSynchronousType inClient)
  {
    this.client =
      Objects.requireNonNull(inClient, "client");

    this.metadata =
      new QCommandMetadata(
        "file-put",
        new QConstant("Upload a file."),
        Optional.empty()
      );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(FILE_ID, FILE);
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    final var fileId =
      context.parameterValue(FILE_ID);
    final var file =
      context.parameterValue(FILE);

    this.client.fileUploadOrThrow(
      fileId,
      file,
      "application/octet-stream",
      stats -> renderStats(context, stats)
    );
    return SUCCESS;
  }

  private static void renderStats(
    final QCommandContextType context,
    final CAClientTransferStatistics stats)
  {
    final var output = context.output();
    output.println(CATransferStatisticsFormatting.format(stats));
    output.flush();
  }

  @Override
  public Completer completer()
  {
    return new StringsCompleter(
      this.onListNamedParameters()
        .stream()
        .map(QParameterType::name)
        .toList()
    );
  }

  @Override
  public QCommandMetadata metadata()
  {
    return this.metadata;
  }

  @Override
  public String toString()
  {
    return "[%s]".formatted(this.getClass().getSimpleName());
  }
}
