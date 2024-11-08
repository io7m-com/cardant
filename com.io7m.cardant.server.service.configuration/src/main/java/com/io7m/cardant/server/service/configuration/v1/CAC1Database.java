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


package com.io7m.cardant.server.service.configuration.v1;

import com.io7m.blackthorne.core.BTElementHandlerType;
import com.io7m.blackthorne.core.BTElementParsingContextType;
import com.io7m.cardant.server.api.CAServerDatabaseConfiguration;
import com.io7m.cardant.server.api.CAServerDatabaseKind;
import org.xml.sax.Attributes;

import java.util.Optional;

final class CAC1Database
  implements BTElementHandlerType<Object, CAServerDatabaseConfiguration>
{
  private CAServerDatabaseConfiguration result;

  CAC1Database(
    final BTElementParsingContextType context)
  {

  }

  @Override
  public void onElementStart(
    final BTElementParsingContextType context,
    final Attributes attributes)
  {
    final var minimumPoolConnections =
      Optional.ofNullable(attributes.getValue("MinimumPoolConnections"))
        .map(x -> Integer.valueOf(Integer.parseUnsignedInt(x)))
        .orElse(Integer.valueOf(0));

    final var maximumPoolConnections =
      Optional.ofNullable(attributes.getValue("MaximumPoolConnections"))
        .map(x -> Integer.valueOf(Integer.parseUnsignedInt(x)))
        .orElse(Integer.valueOf(10));

    this.result =
      new CAServerDatabaseConfiguration(
        CAServerDatabaseKind.valueOf(attributes.getValue("Kind")),
        attributes.getValue("OwnerRoleName"),
        attributes.getValue("OwnerRolePassword"),
        attributes.getValue("WorkerRolePassword"),
        Optional.ofNullable(attributes.getValue("ReaderRolePassword")),
        attributes.getValue("Address"),
        Integer.valueOf(attributes.getValue("Port")).intValue(),
        attributes.getValue("Name"),
        attributes.getValue("Language"),
        Boolean.parseBoolean(attributes.getValue("Create")),
        Boolean.parseBoolean(attributes.getValue("Upgrade")),
        minimumPoolConnections.intValue(),
        maximumPoolConnections.intValue()
      );
  }

  @Override
  public CAServerDatabaseConfiguration onElementFinished(
    final BTElementParsingContextType context)
  {
    return this.result;
  }
}
