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

import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jlexing.core.LexicalType;

import java.net.URI;

public sealed interface CSTokenType extends LexicalType<URI>
{
  String serialized();

  record CSTokenEquals(LexicalPosition<URI> lexical)
    implements CSTokenType
  {
    @Override
    public String serialized()
    {
      return "=";
    }
  }

  record CSTokenSemicolon(LexicalPosition<URI> lexical)
    implements CSTokenType
  {
    @Override
    public String serialized()
    {
      return ";";
    }
  }

  record CSTokenComma(LexicalPosition<URI> lexical)
    implements CSTokenType
  {
    @Override
    public String serialized()
    {
      return ",";
    }
  }

  record CSTokenStar(LexicalPosition<URI> lexical)
    implements CSTokenType
  {
    @Override
    public String serialized()
    {
      return "*";
    }
  }

  record CSTokenEOF(LexicalPosition<URI> lexical)
    implements CSTokenType
  {
    @Override
    public String serialized()
    {
      return "<EOF>";
    }
  }

  record CSTokenKeywordLabel(LexicalPosition<URI> lexical)
    implements CSTokenType
  {
    @Override
    public String serialized()
    {
      return "LABEL";
    }
  }

  record CSTokenKeywordType(LexicalPosition<URI> lexical)
    implements CSTokenType
  {
    @Override
    public String serialized()
    {
      return "TYPE";
    }
  }

  record CSTokenKeywordItem(LexicalPosition<URI> lexical)
    implements CSTokenType
  {
    @Override
    public String serialized()
    {
      return "ITEM";
    }
  }

  record CSTokenKeywordLocation(LexicalPosition<URI> lexical)
    implements CSTokenType
  {
    @Override
    public String serialized()
    {
      return "LOCATION";
    }
  }

  record CSTokenKeywordUser(LexicalPosition<URI> lexical)
    implements CSTokenType
  {
    @Override
    public String serialized()
    {
      return "USER";
    }
  }

  record CSTokenKeywordAny(LexicalPosition<URI> lexical)
    implements CSTokenType
  {
    @Override
    public String serialized()
    {
      return "ANY";
    }
  }

  record CSTokenKeywordImmediately(LexicalPosition<URI> lexical)
    implements CSTokenType
  {
    @Override
    public String serialized()
    {
      return "IMMEDIATELY";
    }
  }

  record CSTokenKeywordRoles(LexicalPosition<URI> lexical)
    implements CSTokenType
  {
    @Override
    public String serialized()
    {
      return "ROLES";
    }
  }

  record CSTokenKeywordAll(LexicalPosition<URI> lexical)
    implements CSTokenType
  {
    @Override
    public String serialized()
    {
      return "ALL";
    }
  }

  record CSTokenIdentifier(
    LexicalPosition<URI> lexical,
    String text)
    implements CSTokenType
  {
    @Override
    public String serialized()
    {
      return this.text;
    }
  }
}
