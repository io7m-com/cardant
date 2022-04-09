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

package com.io7m.cardant.security.vanilla.internal;

import com.io7m.anethum.common.ParseException;
import com.io7m.anethum.common.ParseSeverity;
import com.io7m.anethum.common.ParseStatus;
import com.io7m.cardant.security.vanilla.internal.CSTokenType.CSTokenIdentifier;
import com.io7m.cardant.security.vanilla.internal.CSTokenType.CSTokenKeywordAny;
import com.io7m.cardant.security.vanilla.internal.CSTokenType.CSTokenKeywordImmediately;
import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jlexing.core.LexicalPositionMutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.io7m.cardant.security.vanilla.internal.CSLexer.State.STATE_CRLF;
import static com.io7m.cardant.security.vanilla.internal.CSLexer.State.STATE_INITIAL;
import static com.io7m.cardant.security.vanilla.internal.CSLexer.State.STATE_TEXT;
import static com.io7m.cardant.security.vanilla.internal.CSTokenType.CSTokenComma;
import static com.io7m.cardant.security.vanilla.internal.CSTokenType.CSTokenEOF;
import static com.io7m.cardant.security.vanilla.internal.CSTokenType.CSTokenEquals;
import static com.io7m.cardant.security.vanilla.internal.CSTokenType.CSTokenKeywordAll;
import static com.io7m.cardant.security.vanilla.internal.CSTokenType.CSTokenKeywordItem;
import static com.io7m.cardant.security.vanilla.internal.CSTokenType.CSTokenKeywordLocation;
import static com.io7m.cardant.security.vanilla.internal.CSTokenType.CSTokenKeywordRoles;
import static com.io7m.cardant.security.vanilla.internal.CSTokenType.CSTokenKeywordUser;
import static com.io7m.cardant.security.vanilla.internal.CSTokenType.CSTokenSemicolon;
import static com.io7m.cardant.security.vanilla.internal.CSTokenType.CSTokenStar;

public final class CSLexer
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CSLexer.class);

  private final BufferedReader reader;
  private final StringBuilder text;
  private final LexicalPositionMutable<URI> position;
  private State state;
  private final ArrayDeque<Integer> pushBack;
  private LexicalPosition<URI> tokenStart;

  public CSLexer(
    final BufferedReader inReader,
    final URI source,
    final int line)
  {
    this.reader =
      Objects.requireNonNull(inReader, "reader");
    this.text =
      new StringBuilder();
    this.position =
      LexicalPositionMutable.create(line, 0, Optional.of(source));
    this.state =
      STATE_INITIAL;
    this.pushBack =
      new ArrayDeque<>(2);
  }

  enum State
  {
    STATE_INITIAL,
    STATE_TEXT,
    STATE_CRLF
  }

  private int nextCharacter()
    throws IOException
  {
    if (!this.pushBack.isEmpty()) {
      return this.pushBack.pop()
        .intValue();
    }

    final var ch = this.reader.read();
    if (ch != -1) {
      if (LOG.isTraceEnabled()) {
        LOG.trace("read: {}", Character.toString(ch));
      }
    }
    return ch;
  }

  private void pushCharacter(
    final int ch)
  {
    this.pushBack.push(Integer.valueOf(ch));
  }

  public CSTokenType readToken()
    throws IOException, ParseException
  {
    while (true) {
      switch (this.state) {
        case STATE_INITIAL -> {
          Preconditions.checkPreconditionV(
            this.text.isEmpty(),
            "Text buffer must be empty"
          );

          final var here = this.here();
          this.position.setColumn(this.position.column() + 1);

          final var ch = this.nextCharacter();
          switch (ch) {
            case -1 -> {
              return new CSTokenEOF(here);
            }
            case ';' -> {
              return new CSTokenSemicolon(here);
            }
            case ',' -> {
              return new CSTokenComma(here);
            }
            case '*' -> {
              return new CSTokenStar(here);
            }
            case '=' -> {
              return new CSTokenEquals(here);
            }
            case '\r' -> {
              this.state = STATE_CRLF;
            }
            case '\n' -> {
              this.position.setLine(this.position.line() + 1);
            }

            default -> {
              if (Character.isAlphabetic(ch) || Character.isDigit(ch)) {
                this.tokenStart = here;
                this.text.appendCodePoint(ch);
                this.state = STATE_TEXT;
                break;
              }
              if (Character.isWhitespace(ch)) {
                break;
              }

              throw lexicalException(
                here,
                "Unexpected character: " + Character.toString(ch));
            }
          }
        }

        case STATE_TEXT -> {
          Preconditions.checkPreconditionV(
            !this.text.isEmpty(),
            "Text buffer must not be empty"
          );

          final var here = this.here();
          this.position.setColumn(this.position.column() + 1);

          final var ch = this.nextCharacter();
          switch (ch) {
            case -1 -> {
              this.state = STATE_INITIAL;
              return this.finishTextToken();
            }
            case ';', ',', '*', '=' -> {
              this.pushCharacter(ch);
              return this.finishTextToken();
            }
            default -> {
              if (Character.isWhitespace(ch)) {
                this.pushCharacter(ch);
                return this.finishTextToken();
              }

              if (Character.isAlphabetic(ch) || Character.isDigit(ch)) {
                this.text.appendCodePoint(ch);
                break;
              }

              throw lexicalException(
                here,
                "Unexpected character: " + Character.toString(ch));
            }
          }
        }

        case STATE_CRLF -> {
          Preconditions.checkPreconditionV(
            this.text.isEmpty(),
            "Text buffer must be empty"
          );

          final var ch = this.nextCharacter();
          switch (ch) {
            case '\r' -> {
              this.position.setLine(this.position.line() + 1);
              this.state = STATE_CRLF;
            }
            case '\n' -> {
              this.state = STATE_INITIAL;
            }
            default -> {
              this.pushCharacter(ch);
              this.state = STATE_INITIAL;
            }
          }
        }
      }
    }
  }

  private static ParseException lexicalException(
    final LexicalPosition<URI> here,
    final String message)
  {
    final var error =
      ParseStatus.builder()
        .setSeverity(ParseSeverity.PARSE_ERROR)
        .setMessage(message)
        .setLexical(here)
        .setErrorCode("unexpected-character")
        .build();

    return new ParseException(message, List.of(error));
  }

  private CSTokenType finishTextToken()
  {
    final var result = this.text.toString();
    this.text.setLength(0);
    this.state = STATE_INITIAL;

    return switch (result) {
      case "ALL" -> new CSTokenKeywordAll(this.tokenStart);
      case "ANY" -> new CSTokenKeywordAny(this.tokenStart);
      case "IMMEDIATELY" -> new CSTokenKeywordImmediately(this.tokenStart);
      case "ITEM" -> new CSTokenKeywordItem(this.tokenStart);
      case "LOCATION" -> new CSTokenKeywordLocation(this.tokenStart);
      case "ROLES" -> new CSTokenKeywordRoles(this.tokenStart);
      case "USER" -> new CSTokenKeywordUser(this.tokenStart);
      default -> new CSTokenIdentifier(this.tokenStart, result);
    };
  }

  private LexicalPosition<URI> here()
  {
    return this.position.toImmutable();
  }
}
