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
import com.io7m.cardant.security.api.CSAttributeName;
import com.io7m.cardant.security.api.CSAttributeValue;
import com.io7m.cardant.security.api.CSLabel;
import com.io7m.cardant.security.api.CSLabelParserType;
import com.io7m.jlexing.core.LexicalPositionMutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class CSLabelParser implements CSLabelParserType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CSLabelParser.class);

  private final InputStream stream;
  private final Consumer<ParseStatus> statusConsumer;
  private final InputStreamReader streamReader;
  private final BufferedReader reader;
  private final LexicalPositionMutable<URI> position;
  private final ArrayList<ParseStatus> errors;
  private final HashMap<CSAttributeName, CSAttributeValue> attributes;
  private final StringBuilder text;
  private State state;
  private String nameCurrent;
  private String valueCurrent;
  private boolean failed;

  public CSLabelParser(
    final URI inSource,
    final InputStream inStream,
    final Consumer<ParseStatus> inStatusConsumer)
  {
    Objects.requireNonNull(inSource, "source");

    this.stream =
      Objects.requireNonNull(inStream, "stream");
    this.statusConsumer =
      Objects.requireNonNull(inStatusConsumer, "statusConsumer");
    this.errors =
      new ArrayList<>();
    this.state =
      State.STATE_INITIAL;
    this.position =
      LexicalPositionMutable.create(1, 0, Optional.of(inSource));
    this.streamReader =
      new InputStreamReader(this.stream, UTF_8);
    this.reader =
      new BufferedReader(this.streamReader);
    this.attributes =
      new HashMap<>();
    this.text =
      new StringBuilder(128);
  }

  private static boolean isNameCharacter(
    final int ch)
  {
    if (ch >= 'a' && ch <= 'z') {
      return true;
    }
    if (ch >= '0' && ch <= '9') {
      return true;
    }
    if (ch == '-') {
      return true;
    }
    if (ch == '.') {
      return true;
    }
    return ch == '_';
  }

  @Override
  public CSLabel execute()
    throws ParseException
  {
    try {
      return this.executeWithReader();
    } catch (final IOException e) {
      throw new ParseException(e.getMessage(), List.copyOf(this.errors));
    }
  }

  private CSLabel executeWithReader()
    throws IOException, ParseException
  {
    while (true) {
      switch (this.state) {
        case STATE_INITIAL -> {
          final var ch = this.readChar();
          if (ch == -1) {
            return this.finish();
          }

          if (isNameCharacter(ch)) {
            this.text.appendCodePoint(ch);
            this.state = State.STATE_NAME;
            break;
          }

          this.errorUnexpectedCharacter(
            ch,
            "one of: [a-z0-9] | '.' | '-' | '_'"
          );
          break;
        }

        case STATE_NAME -> {
          final var ch = this.readCharNotEOF();
          if (isNameCharacter(ch)) {
            this.text.appendCodePoint(ch);
            break;
          }

          if (ch == '=') {
            this.nameCurrent = this.text.toString();
            this.text.setLength(0);
            this.state = State.STATE_EQUALS;
            break;
          }

          this.errorUnexpectedCharacter(
            ch,
            "one of: [a-z0-9] | '.' | '-' | '_' | '='"
          );
          break;
        }

        case STATE_VALUE -> {
          final var ch = this.readCharNotEOF();
          if (isNameCharacter(ch)) {
            this.text.appendCodePoint(ch);
            break;
          }

          if (ch == ';') {
            this.valueCurrent = this.text.toString();
            this.text.setLength(0);
            this.state = State.STATE_INITIAL;

            this.attributes.put(
              new CSAttributeName(this.nameCurrent),
              new CSAttributeValue(this.valueCurrent)
            );
            break;
          }

          this.errorUnexpectedCharacter(
            ch,
            "one of: [a-z0-9] | '.' | '-' | '_' | ';'"
          );
          break;
        }

        case STATE_EQUALS -> {
          final var ch = this.readCharNotEOF();
          if (isNameCharacter(ch)) {
            this.text.appendCodePoint(ch);
            this.state = State.STATE_VALUE;
            break;
          }

          this.errorUnexpectedCharacter(
            ch,
            "one of: [a-z0-9] | '.' | '-' | '_'"
          );
          break;
        }
      }
    }
  }

  private CSLabel finish()
    throws ParseException
  {
    if (this.failed) {
      throw new ParseException(
        "One or more errors were encountered", this.errors);
    }
    return new CSLabel(new TreeMap<>(this.attributes));
  }

  private void errorUnexpectedCharacter(
    final int ch,
    final String expected)
  {
    this.publishStatus(
      ParseStatus.builder()
        .setLexical(this.position.toImmutable())
        .setErrorCode("unexpected-character")
        .setMessage(
          String.format(
            "Unexpected character '%s'. Expected %s",
            Character.toString(ch),
            expected
          ))
        .setSeverity(ParseSeverity.PARSE_ERROR)
        .build()
    );
  }

  private IOException errorUnexpectedEOF()
  {
    this.publishStatus(ParseStatus.builder()
                         .setLexical(this.position.toImmutable())
                         .setErrorCode("unexpected-eof")
                         .setMessage("Unexpected EOF")
                         .setSeverity(ParseSeverity.PARSE_ERROR)
                         .build());
    return new IOException("Unexpected EOF");
  }

  private void publishStatus(
    final ParseStatus error)
  {
    final var lexical = error.lexical();

    switch (error.severity()) {
      case PARSE_ERROR -> {
        this.failed = true;
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

    this.errors.add(error);
    this.statusConsumer.accept(error);
  }

  private int readCharNotEOF()
    throws IOException
  {
    final int c = this.readChar();
    if (c == -1) {
      throw this.errorUnexpectedEOF();
    }
    return c;
  }

  private int readChar()
    throws IOException
  {
    final int c = this.reader.read();
    if (c != -1) {
      this.position.setColumn(this.position.column() + 1);
    }
    return c;
  }

  @Override
  public void close()
    throws IOException
  {
    this.stream.close();
  }

  private enum State
  {
    STATE_INITIAL,
    STATE_NAME,
    STATE_VALUE,
    STATE_EQUALS
  }
}
