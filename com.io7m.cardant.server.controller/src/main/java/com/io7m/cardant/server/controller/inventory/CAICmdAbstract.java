/*
 * Copyright © 2022 Mark Raynsford <code@io7m.com> https://www.io7m.com
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


package com.io7m.cardant.server.controller.inventory;

import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.error_codes.CAException;
import com.io7m.cardant.model.CAValidityException;
import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.api.CAProtocolMessageType;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.security.CASecurityException;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutorType;

import java.util.Objects;

/**
 * The abstract base command class.
 *
 * @param <C> The type of accepted commands
 */

public abstract class CAICmdAbstract<C extends CAProtocolMessageType>
  implements CACommandExecutorType<CAICommandContext, C, CAIResponseType>
{
  protected CAICmdAbstract()
  {

  }

  @Override
  public final CAIResponseType execute(
    final CAICommandContext context,
    final C command)
    throws CACommandExecutionFailure
  {
    Objects.requireNonNull(context, "context");
    Objects.requireNonNull(command, "command");

    try {
      return this.executeActual(context, command);
    } catch (final CAValidityException e) {
      throw context.failValidity(e);
    } catch (final CASecurityException e) {
      throw context.failSecurity(e);
    } catch (final CADatabaseException e) {
      throw context.failDatabase(e);
    } catch (final CAProtocolException e) {
      throw context.failProtocol(e);
    } catch (final CAException e) {
      throw context.failWithCause(
        e,
        500,
        e.errorCode(),
        e.getMessage(),
        e.attributes()
      );
    }
  }

  protected abstract CAIResponseType executeActual(
    CAICommandContext context,
    C command)
    throws
    CAValidityException,
    CAException,
    CACommandExecutionFailure,
    CASecurityException,
    CADatabaseException;
}
