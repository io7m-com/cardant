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

import com.io7m.cardant.client.api.CAClientSynchronousType;
import com.io7m.cardant.client.preferences.api.CAPreferencesServiceType;
import com.io7m.cardant.shell.internal.formatting.CAFormatterType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import org.jline.terminal.Terminal;

import java.util.Objects;

/**
 * The abstract command implementation.
 */

public abstract class CAShellCmdAbstract
  implements CAShellCmdType
{
  private final QCommandMetadata metadata;
  private final RPServiceDirectoryType services;

  /**
   * Construct a command.
   *
   * @param inMetadata The metadata
   * @param inServices The service directory
   */

  protected CAShellCmdAbstract(
    final RPServiceDirectoryType inServices,
    final QCommandMetadata inMetadata)
  {
    this.services =
      Objects.requireNonNull(inServices, "services");
    this.metadata =
      Objects.requireNonNull(inMetadata, "metadata");
  }

  protected final CAClientSynchronousType client()
  {
    return this.services.requireService(CAClientSynchronousType.class);
  }

  protected final CAPreferencesServiceType preferences()
  {
    return this.services.requireService(CAPreferencesServiceType.class);
  }

  protected final CAShellOptions options()
  {
    return this.services.requireService(CAShellOptions.class);
  }

  protected final CAShellLoginTracker loginTracker()
  {
    return this.services.requireService(CAShellLoginTracker.class);
  }

  @Override
  public final String toString()
  {
    return "[%s]".formatted(this.getClass().getSimpleName());
  }

  @Override
  public final QCommandMetadata metadata()
  {
    return this.metadata;
  }

  protected final Terminal terminal()
  {
    return this.services.requireService(CAShellTerminalHolder.class)
      .terminal();
  }

  protected final CAFormatterType formatter()
  {
    return this.options().formatter();
  }
}
