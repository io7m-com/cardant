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

import com.io7m.cardant.client.api.CAClientTransferStatistics;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.protocol.inventory.CAICommandFileGet;
import com.io7m.cardant.protocol.inventory.CAIResponseFileGet;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed01;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QParameterType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import org.jline.reader.Completer;
import org.jline.reader.impl.completer.StringsCompleter;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static com.io7m.quarrel.core.QCommandStatus.FAILURE;
import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "file-get"
 */

public final class CAShellCmdFileGet
  extends CAShellCmdAbstract
{
  private static final QParameterNamed1<CAFileID> FILE_ID =
    new QParameterNamed1<>(
      "--id",
      List.of(),
      new QConstant("The file ID."),
      Optional.empty(),
      CAFileID.class
    );

  private static final QParameterNamed01<Path> FILE =
    new QParameterNamed01<>(
      "--download-to",
      List.of(),
      new QConstant("If specified, the file will be downloaded to this path."),
      Optional.empty(),
      Path.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The shell context
   */

  public CAShellCmdFileGet(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "file-get",
        new QConstant("Download a file."),
        Optional.empty()
      ));
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
    final var fileOpt =
      context.parameterValue(FILE);

    final var fileDefinition =
      (CAIResponseFileGet)
        this.client().executeOrElseThrow(new CAICommandFileGet(fileId));

    final var fileInfo = fileDefinition.data();
    this.formatter().formatFile(fileInfo);

    if (fileOpt.isEmpty()) {
      return SUCCESS;
    }

    final var file = fileOpt.get();
    final var outFileName = file.getFileName();
    if (outFileName == null) {
      return FAILURE;
    }

    final var newFileName = outFileName + ".tmp";
    this.client().fileDownloadOrThrow(
      fileId,
      file,
      file.resolveSibling(newFileName),
      fileInfo.size(),
      fileInfo.hashAlgorithm(),
      fileInfo.hashValue(),
      statistics -> {
        renderStats(context, statistics);
      }
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
}
