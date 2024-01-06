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


package com.io7m.cardant.type_packages.compilers.internal;

import com.io7m.anethum.api.ParsingException;
import com.io7m.cardant.error_codes.CAErrorCode;
import com.io7m.cardant.model.type_package.CATypePackageDeclaration;
import com.io7m.cardant.strings.CAStringConstants;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.cardant.type_packages.checker.api.CATypePackageCheckerFactoryType;
import com.io7m.cardant.type_packages.checker.api.CATypePackageCheckerFailure;
import com.io7m.cardant.type_packages.checker.api.CATypePackageCheckerSuccess;
import com.io7m.cardant.type_packages.compiler.api.CATypePackageCompileCheckingFailed;
import com.io7m.cardant.type_packages.compiler.api.CATypePackageCompileOK;
import com.io7m.cardant.type_packages.compiler.api.CATypePackageCompileParsingFailed;
import com.io7m.cardant.type_packages.compiler.api.CATypePackageCompileResultType;
import com.io7m.cardant.type_packages.compiler.api.CATypePackageCompilerType;
import com.io7m.cardant.type_packages.parser.api.CATypePackageParserFactoryType;
import com.io7m.cardant.type_packages.resolver.api.CATypePackageResolverType;
import com.io7m.seltzer.api.SStructuredError;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorParse;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorTypeCheckFailed;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * A type package compiler.
 */

public final class CATypePackageCompiler implements CATypePackageCompilerType
{
  private final CAStrings strings;
  private final CATypePackageResolverType resolver;
  private final CATypePackageParserFactoryType parsers;
  private final CATypePackageCheckerFactoryType checkers;

  /**
   * A type package compiler.
   *
   * @param inStrings  The strings
   * @param inResolver The resolver
   * @param inCheckers The checkers
   * @param inParsers  The parsers
   */

  public CATypePackageCompiler(
    final CAStrings inStrings,
    final CATypePackageResolverType inResolver,
    final CATypePackageParserFactoryType inParsers,
    final CATypePackageCheckerFactoryType inCheckers)
  {
    this.strings =
      Objects.requireNonNull(inStrings, "strings");
    this.resolver =
      Objects.requireNonNull(inResolver, "inResolver");
    this.parsers =
      Objects.requireNonNull(inParsers, "parsers");
    this.checkers =
      Objects.requireNonNull(inCheckers, "checkers");
  }

  @Override
  public CATypePackageCompileResultType execute(
    final String text)
    throws IOException
  {
    Objects.requireNonNull(text, "text");

    final var extras =
      new LinkedList<SStructuredError<CAErrorCode>>();

    final CATypePackageDeclaration packageDeclaration;
    try (var input = new ByteArrayInputStream(text.getBytes(UTF_8))) {
      packageDeclaration = this.parsers.parse(URI.create("urn:in"), input);
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

      return new CATypePackageCompileParsingFailed(
        e.getMessage(),
        errorParse(),
        Map.of(),
        Optional.empty(),
        Optional.of(e),
        List.copyOf(extras)
      );
    }

    final var checker =
      this.checkers.createChecker(
        this.strings,
        this.resolver,
        packageDeclaration
      );

    final var checkResult = checker.execute();
    return switch (checkResult) {
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

        yield new CATypePackageCompileCheckingFailed(
          this.strings.format(CAStringConstants.ERROR_TYPE_CHECKING),
          errorTypeCheckFailed(),
          Map.of(),
          Optional.empty(),
          Optional.empty(),
          List.copyOf(extras)
        );
      }

      case final CATypePackageCheckerSuccess s -> {
        yield new CATypePackageCompileOK(s.typePackage());
      }
    };
  }
}
