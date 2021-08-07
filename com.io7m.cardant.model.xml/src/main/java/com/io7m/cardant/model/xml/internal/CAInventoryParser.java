/*
 * Copyright © 2021 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.model.xml.internal;

import com.io7m.anethum.common.ParseException;
import com.io7m.anethum.common.ParseSeverity;
import com.io7m.anethum.common.ParseStatus;
import com.io7m.blackthorne.api.BTException;
import com.io7m.blackthorne.api.BTParseError;
import com.io7m.blackthorne.api.BTParseErrorType;
import com.io7m.blackthorne.jxe.BlackthorneJXE;
import com.io7m.cardant.model.CAInventoryElementType;
import com.io7m.cardant.model.xml.CAInventoryParserType;
import com.io7m.cardant.model.xml.CAInventoryParsers;
import com.io7m.cardant.model.xml.CAInventorySchemas;
import com.io7m.jxe.core.JXEXInclude;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.io7m.anethum.common.ParseSeverity.PARSE_ERROR;
import static com.io7m.anethum.common.ParseSeverity.PARSE_WARNING;

/**
 * An inventory parser.
 */

public final class CAInventoryParser implements CAInventoryParserType
{
  private final URI source;
  private final InputStream stream;
  private final Consumer<ParseStatus> statusConsumer;

  /**
   * An inventory parser.
   *
   * @param inSource         The source
   * @param inStream         The input stream
   * @param inStatusConsumer A status consumer
   */

  public CAInventoryParser(
    final URI inSource,
    final InputStream inStream,
    final Consumer<ParseStatus> inStatusConsumer)
  {
    this.source =
      Objects.requireNonNull(inSource, "source");
    this.stream =
      Objects.requireNonNull(inStream, "stream");
    this.statusConsumer =
      Objects.requireNonNull(inStatusConsumer, "statusConsumer");
  }

  private static ParseStatus mapError(
    final BTParseError error)
  {
    return ParseStatus.builder()
      .setErrorCode("parseError")
      .setLexical(error.lexical())
      .setMessage(error.message())
      .setSeverity(mapSeverity(error.severity()))
      .build();
  }

  private static ParseSeverity mapSeverity(
    final BTParseErrorType.Severity severity)
  {
    return switch (severity) {
      case WARNING -> PARSE_WARNING;
      case ERROR -> PARSE_ERROR;
    };
  }

  @Override
  public CAInventoryElementType execute()
    throws ParseException
  {
    try {
      return BlackthorneJXE.parse(
        this.source,
        this.stream,
        CAInventoryParsers.rootElementParsers(),
        JXEXInclude.XINCLUDE_DISABLED,
        CAInventorySchemas.schemas()
      );
    } catch (final BTException e) {
      throw new ParseException(
        e.getMessage(),
        e.errors()
          .stream()
          .map(CAInventoryParser::mapError)
          .peek(this.statusConsumer::accept)
          .collect(Collectors.toList())
      );
    }
  }

  @Override
  public void close()
    throws IOException
  {
    this.stream.close();
  }
}
