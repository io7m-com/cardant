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

import com.io7m.cardant.parsers.CASyntaxFactoryType;

/**
 * Inventory system (Parsers).
 */

module com.io7m.cardant.parsers
{
  requires static org.osgi.annotation.bundle;
  requires static org.osgi.annotation.versioning;

  requires com.io7m.cardant.model;
  requires com.io7m.cardant.strings;

  requires com.io7m.jaffirm.core;
  requires com.io7m.jeucreader.core;
  requires com.io7m.jsx.core;
  requires com.io7m.jsx.parser.api;
  requires com.io7m.jsx.parser;

  uses CASyntaxFactoryType;

  provides CASyntaxFactoryType with
    com.io7m.cardant.parsers.CADescriptionMatchExpressionsSyntax,
    com.io7m.cardant.parsers.CAItemLocationMatchExpressionsSyntax,
    com.io7m.cardant.parsers.CAItemSerialMatchExpressionsSyntax,
    com.io7m.cardant.parsers.CAMediaTypeMatchExpressionsSyntax,
    com.io7m.cardant.parsers.CAMetadataConstraintExpressionsSyntax,
    com.io7m.cardant.parsers.CAMetadataExpressionsSyntax,
    com.io7m.cardant.parsers.CAMetadataFieldMatchExpressionsSyntax,
    com.io7m.cardant.parsers.CAMetadataMatchExpressionsSyntax,
    com.io7m.cardant.parsers.CAMetadataPackageMatchExpressionsSyntax,
    com.io7m.cardant.parsers.CAMetadataTypeMatchExpressionsSyntax,
    com.io7m.cardant.parsers.CAModelSyntaxFactory,
    com.io7m.cardant.parsers.CANameMatchFuzzyExpressionsSyntax,
    com.io7m.cardant.parsers.CATypeMatchExpressionsSyntax
    ;

  exports com.io7m.cardant.parsers;
}
