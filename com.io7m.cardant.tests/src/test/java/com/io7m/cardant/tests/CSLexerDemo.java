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

package com.io7m.cardant.tests;

import com.io7m.anethum.common.ParseException;
import com.io7m.cardant.security.vanilla.internal.CSLexer;
import com.io7m.cardant.security.vanilla.internal.CSTokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class CSLexerDemo
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CSLexerDemo.class);

  private CSLexerDemo()
  {

  }

  public static void main(
    final String[] args)
    throws IOException
  {
    try (var reader =
           new BufferedReader(new InputStreamReader(System.in, UTF_8))) {
      final var lexer =
        new CSLexer(reader, URI.create("urn:stdin"), 1);

      while (true) {
        try {
          final var token = lexer.readToken();
          LOG.debug("token: {}", token.serialized());
          if (token instanceof CSTokenType.CSTokenEOF) {
            break;
          }
        } catch (final ParseException e) {
          for (final var error : e.statusValues()) {
            final var lexical = error.lexical();
            switch (error.severity()) {
              case PARSE_ERROR -> {
                LOG.error(
                  "{}:{}: {}",
                  Integer.valueOf(lexical.line()),
                  Integer.valueOf(lexical.column()),
                  error.message()
                );
              }
              case PARSE_WARNING -> {
                LOG.warn(
                  "{}:{}: {}",
                  Integer.valueOf(lexical.line()),
                  Integer.valueOf(lexical.column()),
                  error.message()
                );
              }
              case PARSE_INFO -> {
                LOG.info(
                  "{}:{}: {}",
                  Integer.valueOf(lexical.line()),
                  Integer.valueOf(lexical.column()),
                  error.message()
                );
              }
            }
          }
        }
      }
    }
  }
}
