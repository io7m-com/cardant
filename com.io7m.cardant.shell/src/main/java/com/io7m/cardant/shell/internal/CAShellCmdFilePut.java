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
import com.io7m.cardant.client.preferences.api.CAPreferences;
import com.io7m.cardant.model.CAFileID;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed01;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QParameterType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import org.apache.tika.Tika;
import org.jline.reader.Completer;
import org.jline.reader.impl.completer.StringsCompleter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "file-put"
 */

public final class CAShellCmdFilePut extends CAShellCmdAbstract
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAShellCmdFilePut.class);

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

  private static final QParameterNamed01<String> DESCRIPTION =
    new QParameterNamed01<>(
      "--description",
      List.of(),
      new QConstant("The file description."),
      Optional.empty(),
      String.class
    );

  private static final QParameterNamed01<String> CONTENT_TYPE =
    new QParameterNamed01<>(
      "--content-type",
      List.of(),
      new QConstant("The content type (inferred if not specified)."),
      Optional.empty(),
      String.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The shell context
   */

  public CAShellCmdFilePut(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "file-put",
        new QConstant("Upload a file."),
        Optional.empty()
      ));
  }


  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(FILE_ID, FILE, DESCRIPTION, CONTENT_TYPE);
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
    final var description =
      context.parameterValue(DESCRIPTION);
    final var contentTypeOpt =
      context.parameterValue(CONTENT_TYPE);

    final String contentType;
    if (contentTypeOpt.isPresent()) {
      contentType = contentTypeOpt.get();
    } else {
      contentType = inferContentType(file);
    }

    this.preferences().update(oldPreferences -> {
      final var newRecentFiles =
        Stream.concat(
            Stream.of(file),
            oldPreferences.recentFiles().stream())
          .map(Path::toAbsolutePath)
          .distinct()
          .limit(10L)
          .toList();

      return new CAPreferences(
        oldPreferences.debuggingEnabled(),
        oldPreferences.serverBookmarks(),
        newRecentFiles
      );
    });

    this.client().fileUploadOrThrow(
      fileId,
      file,
      contentType,
      description.orElse(""),
      stats -> renderStats(context, stats)
    );
    return SUCCESS;
  }

  private static String inferContentType(
    final Path file)
    throws IOException
  {
    final var detected = new Tika().detect(file);
    LOG.info("Detected file type {}.", detected);
    return detected;
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
