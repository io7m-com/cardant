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

import com.io7m.anethum.api.ParsingException;
import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageInstallType;
import com.io7m.cardant.database.api.CADatabaseTypePackageResolver;
import com.io7m.cardant.error_codes.CAErrorCode;
import com.io7m.cardant.model.type_package.CATypePackageDeclaration;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageInstall;
import com.io7m.cardant.protocol.inventory.CAIResponseBlame;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.protocol.inventory.CAIResponseTypePackageInstall;
import com.io7m.cardant.security.CASecurityException;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.strings.CAStringConstants;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.cardant.type_packages.CATypePackageCheckerFactoryType;
import com.io7m.cardant.type_packages.CATypePackageCheckerFailure;
import com.io7m.cardant.type_packages.CATypePackageCheckerSuccess;
import com.io7m.cardant.type_packages.CATypePackageCheckers;
import com.io7m.cardant.type_packages.CATypePackageParserFactoryType;
import com.io7m.cardant.type_packages.CATypePackageParsers;
import com.io7m.seltzer.api.SStructuredError;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorIo;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorParse;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorTypeCheckFailed;
import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_ITEMS;
import static com.io7m.cardant.security.CASecurityPolicy.WRITE;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_IO;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @see CAICommandTypePackageInstall
 */

public final class CAICmdTypePackageInstall
  extends CAICmdAbstract<CAICommandTypePackageInstall>
{
  private static final CATypePackageParserFactoryType PACKAGE_PARSERS =
    new CATypePackageParsers();
  private static final CATypePackageCheckerFactoryType PACKAGE_CHECKERS =
    new CATypePackageCheckers();

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
    final var transaction =
      context.transaction();

    transaction.setUserId(context.session().userId());

    final var extras =
      new LinkedList<SStructuredError<CAErrorCode>>();

    final CATypePackageDeclaration packageDeclaration;
    try (var input = new ByteArrayInputStream(command.text().getBytes(UTF_8))) {
      packageDeclaration = PACKAGE_PARSERS.parse(URI.create("urn:in"), input);
    } catch (final IOException e) {
      throw context.failFormatted(
        e,
        500,
        errorIo(),
        context.attributes(),
        ERROR_IO
      );
    } catch (final ParsingException e) {
      final var errors = e.statusValues();
      for (final var error : errors) {
        extras.add(
          new SStructuredError<>(
            new CAErrorCode(error.errorCode()),
            error.message(),
            error.attributes(),
            error.remediatingAction(),
            error.exception()
          )
        );
      }
      return new CAIResponseError(
        context.requestId(),
        e.getMessage(),
        errorParse(),
        Map.of(),
        Optional.empty(),
        Optional.empty(),
        CAIResponseBlame.BLAME_CLIENT,
        List.copyOf(extras)
      );
    }

    final var resolver =
      CADatabaseTypePackageResolver.create(transaction);
    final var checker =
      PACKAGE_CHECKERS.createChecker(strings, resolver, packageDeclaration);

    final var checkResult = checker.execute();
    switch (checkResult) {
      case final CATypePackageCheckerFailure f -> {
        for (final var error : f.errors()) {
          extras.add(
            new SStructuredError<>(
              new CAErrorCode(error.errorCode()),
              error.message(),
              error.attributes(),
              error.remediatingAction(),
              error.exception()
            )
          );
        }
        return new CAIResponseError(
          context.requestId(),
          strings.format(CAStringConstants.ERROR_TYPE_CHECKING),
          errorTypeCheckFailed(),
          Map.of(),
          Optional.empty(),
          Optional.empty(),
          CAIResponseBlame.BLAME_CLIENT,
          List.copyOf(extras)
        );
      }

      case final CATypePackageCheckerSuccess s -> {
        final var install =
          transaction.queries(TypePackageInstallType.class);

        install.execute(s.typePackage());
        return new CAIResponseTypePackageInstall(
          context.requestId(),
          s.typePackage().identifier()
        );
      }
    }
  }
}
