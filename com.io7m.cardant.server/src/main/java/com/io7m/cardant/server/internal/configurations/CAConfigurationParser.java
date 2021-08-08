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

import com.io7m.blackthorne.api.BTElementHandlerConstructorType;
import com.io7m.blackthorne.api.BTElementHandlerType;
import com.io7m.blackthorne.api.BTElementParsingContextType;
import com.io7m.blackthorne.api.BTQualifiedName;
import com.io7m.cardant.server.api.CAServerConfiguration;
import com.io7m.cardant.server.api.CAServerConfigurationLimits;
import com.io7m.cardant.server.api.CAServerDatabaseConfigurationType;
import com.io7m.cardant.server.api.CAServerHTTPConfiguration;

import java.nio.file.FileSystem;
import java.util.Map;
import java.util.Objects;

import static com.io7m.cardant.server.internal.CASchemas.element1;

/**
 * A parser.
 */

public final class CAConfigurationParser
  implements BTElementHandlerType<Object, CAServerConfiguration>
{
  private final FileSystem fileSystem;
  private CAServerDatabaseConfigurationType database;
  private CAServerHTTPConfiguration http;
  private CAServerConfigurationLimits limits;

  /**
   * Construct a parser.
   *
   * @param inFileSystem The filesystem
   * @param context      The parse context
   */

  public CAConfigurationParser(
    final FileSystem inFileSystem,
    final BTElementParsingContextType context)
  {
    this.fileSystem =
      Objects.requireNonNull(inFileSystem, "fileSystem");
    this.limits =
      CAServerConfigurationLimits.create();
  }

  @Override
  public Map<BTQualifiedName, BTElementHandlerConstructorType<?, ?>>
  onChildHandlersRequested(final BTElementParsingContextType context)
  {
    return Map.ofEntries(
      Map.entry(
        element1("DatabaseRemote"),
        CADatabaseRemoteParser::new
      ),
      Map.entry(
        element1("DatabaseLocal"),
        c -> new CADatabaseLocalParser(this.fileSystem, c)
      ),
      Map.entry(
        element1("HTTP"),
        c -> new CAConfigurationHTTPParser(this.fileSystem, c)
      ),
      Map.entry(
        element1("Limits"),
        CAConfigurationLimitsParser::new
      )
    );
  }

  @Override
  public void onChildValueProduced(
    final BTElementParsingContextType context,
    final Object result)
    throws Exception
  {
    if (result instanceof CAServerDatabaseConfigurationType received) {
      this.database = received;
      return;
    }
    if (result instanceof CAServerHTTPConfiguration received) {
      this.http = received;
      return;
    }
    if (result instanceof CAServerConfigurationLimits received) {
      this.limits = received;
      return;
    }
  }

  @Override
  public CAServerConfiguration onElementFinished(
    final BTElementParsingContextType context)
  {
    return new CAServerConfiguration(this.http, this.database, this.limits);
  }
}
