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

package com.io7m.cardant.server.internal.configurations;

import com.io7m.blackthorne.api.BTElementHandlerType;
import com.io7m.blackthorne.api.BTElementParsingContextType;
import com.io7m.cardant.server.api.CAServerHTTPConfiguration;
import org.xml.sax.Attributes;

import java.nio.file.FileSystem;
import java.util.Objects;

/**
 * A parser.
 */

public final class CAConfigurationHTTPParser
  implements BTElementHandlerType<Object, CAServerHTTPConfiguration>
{
  private final FileSystem fileSystem;
  private CAServerHTTPConfiguration result;

  /**
   * Construct a parser.
   *
   * @param inFileSystem The filesystem
   * @param context      The parse context
   */

  public CAConfigurationHTTPParser(
    final FileSystem inFileSystem,
    final BTElementParsingContextType context)
  {
    this.fileSystem =
      Objects.requireNonNull(inFileSystem, "fileSystem");
  }

  @Override
  public void onElementStart(
    final BTElementParsingContextType context,
    final Attributes attributes)
  {
    this.result = new CAServerHTTPConfiguration(
      Integer.parseInt(attributes.getValue("port")),
      this.fileSystem.getPath(attributes.getValue("sessionDirectory"))
    );
  }

  @Override
  public CAServerHTTPConfiguration onElementFinished(
    final BTElementParsingContextType context)
  {
    return this.result;
  }
}
