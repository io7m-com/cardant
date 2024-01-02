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


package com.io7m.cardant.type_packages.compilers;

import com.io7m.cardant.strings.CAStrings;
import com.io7m.cardant.type_packages.checker.api.CATypePackageCheckerFactoryType;
import com.io7m.cardant.type_packages.compiler.api.CATypePackageCompilerFactoryType;
import com.io7m.cardant.type_packages.compiler.api.CATypePackageCompilerType;
import com.io7m.cardant.type_packages.compilers.internal.CATypePackageCompiler;
import com.io7m.cardant.type_packages.parser.api.CATypePackageParserFactoryType;
import com.io7m.cardant.type_packages.resolver.api.CATypePackageResolverType;

import java.util.Objects;

/**
 * The default compilers factory.
 */

public final class CATypePackageCompilers
  implements CATypePackageCompilerFactoryType
{
  private final CAStrings strings;
  private final CATypePackageParserFactoryType parsers;
  private final CATypePackageCheckerFactoryType checkers;

  private CATypePackageCompilers(
    final CAStrings inStrings,
    final CATypePackageParserFactoryType inParsers,
    final CATypePackageCheckerFactoryType inCheckers)
  {
    this.strings =
      Objects.requireNonNull(inStrings, "strings");
    this.parsers =
      Objects.requireNonNull(inParsers, "parsers");
    this.checkers =
      Objects.requireNonNull(inCheckers, "checkers");
  }

  /**
   * Create a compilers factory.
   *
   * @param inStrings  The string resources
   * @param inParsers  The parsers
   * @param inCheckers The checkers
   *
   * @return A compilers factory
   */

  public static CATypePackageCompilerFactoryType create(
    final CAStrings inStrings,
    final CATypePackageParserFactoryType inParsers,
    final CATypePackageCheckerFactoryType inCheckers)
  {
    return new CATypePackageCompilers(inStrings, inParsers, inCheckers);
  }

  @Override
  public CATypePackageCompilerType createCompiler(
    final CATypePackageResolverType resolver)
  {
    return new CATypePackageCompiler(
      this.strings,
      resolver,
      this.parsers,
      this.checkers
    );
  }

  @Override
  public String description()
  {
    return "The compilers factory service.";
  }

  @Override
  public String toString()
  {
    return "[CATypePackageCompilers 0x%s]"
      .formatted(Integer.toUnsignedString(this.hashCode(), 16));
  }
}
