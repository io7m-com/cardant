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


package com.io7m.cardant.type_packages.checkers;

import com.io7m.cardant.model.type_package.CATypePackageDeclaration;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.cardant.type_packages.checker.api.CATypePackageCheckerFactoryType;
import com.io7m.cardant.type_packages.checker.api.CATypePackageCheckerType;
import com.io7m.cardant.type_packages.checkers.internal.CATypePackageChecker;
import com.io7m.cardant.type_packages.resolver.api.CATypePackageResolverType;

import java.util.Objects;

/**
 * The default factory of package checkers.
 */

public final class CATypePackageCheckers
  implements CATypePackageCheckerFactoryType
{
  /**
   * The default factory of package checkers.
   */

  public CATypePackageCheckers()
  {

  }

  @Override
  public CATypePackageCheckerType createChecker(
    final CAStrings strings,
    final CATypePackageResolverType resolver,
    final CATypePackageDeclaration declaration)
  {
    Objects.requireNonNull(strings, "strings");
    Objects.requireNonNull(resolver, "resolver");
    Objects.requireNonNull(declaration, "declaration");
    return new CATypePackageChecker(strings, resolver, declaration);
  }

  @Override
  public String toString()
  {
    return "[CATypePackageCheckers 0x%s]"
      .formatted(Integer.toUnsignedString(this.hashCode(), 16));
  }

  @Override
  public String description()
  {
    return "The package checker service.";
  }
}
