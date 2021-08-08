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

package com.io7m.cardant.protocol.inventory.v1.internal;

import com.io7m.anethum.common.ParseException;
import com.io7m.anethum.common.ParseSeverity;
import com.io7m.anethum.common.ParseStatus;
import com.io7m.blackthorne.api.BTException;
import com.io7m.blackthorne.api.BTParseError;
import com.io7m.blackthorne.api.BTParseErrorType;
import com.io7m.blackthorne.jxe.BlackthorneJXE;
import com.io7m.cardant.protocol.inventory.v1.CA1InventoryMessageParserType;
import com.io7m.cardant.protocol.inventory.v1.CA1InventoryMessageParsers;
import com.io7m.cardant.protocol.inventory.v1.CA1InventorySchemas;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1InventoryMessageType;
import com.io7m.jxe.core.JXEXInclude;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.io7m.anethum.common.ParseSeverity.PARSE_ERROR;
import static com.io7m.anethum.common.ParseSeverity.PARSE_WARNING;

/**
 * A parser.
 */

public final class CA1InventoryMessageParser
  implements CA1InventoryMessageParserType
{
  private final URI source;
  private final InputStream stream;

  /**
   * Create a parser.
   *
   * @param inSource The parsing source
   * @param inStream The source stream
   */

  public CA1InventoryMessageParser(
    final URI inSource,
    final InputStream inStream)
  {
    this.source =
      Objects.requireNonNull(inSource, "source");
    this.stream =
      Objects.requireNonNull(inStream, "stream");
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
  public CA1InventoryMessageType execute()
    throws ParseException
  {
    try {
      return BlackthorneJXE.parse(
        this.source,
        this.stream,
        CA1InventoryMessageParsers.allParsersSharp(),
        JXEXInclude.XINCLUDE_DISABLED,
        CA1InventorySchemas.schemas()
      );
    } catch (final BTException e) {
      throw new ParseException(
        e.getMessage(),
        e.errors()
          .stream()
          .map(CA1InventoryMessageParser::mapError)
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
