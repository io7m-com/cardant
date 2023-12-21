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

import com.io7m.cardant.client.api.CAClientException;
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarRemove;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeScalarRemove;
import com.io7m.lanark.core.RDottedName;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed0N;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "type-scalar-remove"
 */

public final class CAShellCmdTypeScalarRemove
  extends CAShellCmdAbstractCR<CAICommandTypeScalarRemove, CAIResponseTypeScalarRemove>
{
  private static final QParameterNamed0N<RDottedName> TYPE_NAME =
    new QParameterNamed0N<>(
      "--name",
      List.of(),
      new QConstant("The type name."),
      List.of(),
      RDottedName.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The shell context
   */

  public CAShellCmdTypeScalarRemove(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "type-scalar-remove",
        new QConstant("Remove one or more scalar types"),
        Optional.empty()
      ),
      CAICommandTypeScalarRemove.class
    );
  }


  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(TYPE_NAME);
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    final var client =
      this.client();

    final var typeNames =
      context.parameterValues(TYPE_NAME);

    client.executeOrElseThrow(
      new CAICommandTypeScalarRemove(Set.copyOf(typeNames)),
      CAClientException::ofError
    );

    return SUCCESS;
  }
}
