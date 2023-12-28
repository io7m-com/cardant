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


package com.io7m.cardant.type_packages.internal;

import com.io7m.blackthorne.core.BTElementHandlerType;
import com.io7m.blackthorne.core.BTElementParsingContextType;
import com.io7m.verona.core.Version;
import com.io7m.verona.core.VersionParser;
import com.io7m.verona.core.VersionRange;
import org.xml.sax.Attributes;

/**
 * A parser.
 */

public final class CATP1VersionRange
  implements BTElementHandlerType<Object, VersionRange>
{
  private Version versionLower;
  private Version versionUpper;
  private boolean versionLowerInclusive;
  private boolean versionUpperInclusive;

  /**
   * A parser.
   *
   * @param context The parse context
   */

  public CATP1VersionRange(
    final BTElementParsingContextType context)
  {

  }

  @Override
  public void onElementStart(
    final BTElementParsingContextType context,
    final Attributes attributes)
    throws Exception
  {
    this.versionLower =
      VersionParser.parse(attributes.getValue("VersionLower"));
    this.versionUpper =
      VersionParser.parse(attributes.getValue("VersionUpper"));
    this.versionLowerInclusive =
      Boolean.parseBoolean(attributes.getValue("VersionLowerInclusive"));
    this.versionUpperInclusive =
      Boolean.parseBoolean(attributes.getValue("VersionUpperInclusive"));
  }

  @Override
  public VersionRange onElementFinished(
    final BTElementParsingContextType context)
  {
    return new VersionRange(
      this.versionLower,
      this.versionLowerInclusive,
      this.versionUpper,
      this.versionUpperInclusive
    );
  }
}
