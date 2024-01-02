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

package com.io7m.cardant.server.controller.inventory;

import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageInstallType;
import com.io7m.cardant.database.api.CADatabaseTypePackageResolver;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageInstall;
import com.io7m.cardant.protocol.inventory.CAIResponseBlame;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.protocol.inventory.CAIResponseTypePackageInstall;
import com.io7m.cardant.security.CASecurityException;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.cardant.type_packages.compiler.api.CATypePackageCompileCheckingFailed;
import com.io7m.cardant.type_packages.compiler.api.CATypePackageCompileOK;
import com.io7m.cardant.type_packages.compiler.api.CATypePackageCompileParsingFailed;
import com.io7m.cardant.type_packages.compiler.api.CATypePackageCompileResultType;
import com.io7m.cardant.type_packages.compiler.api.CATypePackageCompilerFactoryType;

import java.io.IOException;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorIo;
import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_ITEMS;
import static com.io7m.cardant.security.CASecurityPolicy.WRITE;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_IO;

/**
 * @see CAICommandTypePackageInstall
 */

public final class CAICmdTypePackageInstall
  extends CAICmdAbstract<CAICommandTypePackageInstall>
{
  /**
   * @see CAICommandTypePackageInstall
   */

  public CAICmdTypePackageInstall()
  {

  }

  @Override
  protected CAIResponseType executeActual(
    final CAICommandContext context,
    final CAICommandTypePackageInstall command)
    throws CASecurityException, CACommandExecutionFailure, CADatabaseException
  {
    context.securityCheck(INVENTORY_ITEMS, WRITE);

    final var services =
      context.services();
    final var strings =
      services.requireService(CAStrings.class);
    final var compilers =
      services.requireService(CATypePackageCompilerFactoryType.class);

    final var transaction = context.transaction();
    transaction.setUserId(context.session().userId());

    final var resolver =
      CADatabaseTypePackageResolver.create(compilers, transaction);
    final var compiler =
      compilers.createCompiler(resolver);

    final CATypePackageCompileResultType compileResult;
    try {
      compileResult = compiler.execute(command.text());
    } catch (final IOException e) {
      throw context.failFormatted(
        e,
        500,
        errorIo(),
        context.attributes(),
        ERROR_IO
      );
    }

    return switch (compileResult) {
      case final CATypePackageCompileOK ok -> {
        transaction.queries(TypePackageInstallType.class)
          .execute(ok.typePackage());

        yield new CAIResponseTypePackageInstall(
          context.requestId(),
          ok.typePackage().identifier()
        );
      }
      case final CATypePackageCompileParsingFailed f -> {
        yield new CAIResponseError(
          context.requestId(),
          f.message(),
          f.errorCode(),
          f.attributes(),
          f.remediatingAction(),
          f.exception(),
          CAIResponseBlame.BLAME_CLIENT,
          f.extras()
        );
      }
      case final CATypePackageCompileCheckingFailed f -> {
        yield new CAIResponseError(
          context.requestId(),
          f.message(),
          f.errorCode(),
          f.attributes(),
          f.remediatingAction(),
          f.exception(),
          CAIResponseBlame.BLAME_CLIENT,
          f.extras()
        );
      }
    };
  }
}
