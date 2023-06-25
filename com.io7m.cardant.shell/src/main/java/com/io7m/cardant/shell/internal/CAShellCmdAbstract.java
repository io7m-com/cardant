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
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QParameterType;
import org.jline.reader.Completer;
import org.jline.reader.impl.completer.StringsCompleter;

import java.util.Objects;

/**
 * The abstract command implementation.
 *
 * @param <C> The command type
 * @param <R> The response type
 */

public abstract class CAShellCmdAbstract<
  C extends CAICommandType<R>,
  R extends CAIResponseType>
  implements CAShellCmdType
{
  private final CAClientSynchronousType client;
  private final Class<R> responseClass;
  private final QCommandMetadata metadata;

  /**
   * Construct a command.
   *
   * @param inMetadata      The metadata
   * @param inCommandClass  The command class
   * @param inResponseClass The response class
   * @param inClient        The client
   */

  protected CAShellCmdAbstract(
    final CAClientSynchronousType inClient,
    final QCommandMetadata inMetadata,
    final Class<C> inCommandClass,
    final Class<R> inResponseClass)
  {
    this.client =
      Objects.requireNonNull(inClient, "client");
    this.metadata =
      Objects.requireNonNull(inMetadata, "metadata");
    Objects.requireNonNull(inCommandClass, "commandClass");
    this.responseClass =
      Objects.requireNonNull(inResponseClass, "responseClass");
  }

  protected final CAClientSynchronousType client()
  {
    return this.client;
  }

  protected final Class<R> responseClass()
  {
    return this.responseClass;
  }

  @Override
  public final Completer completer()
  {
    return new StringsCompleter(
      this.onListNamedParameters()
        .stream()
        .map(QParameterType::name)
        .toList()
    );
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
}
