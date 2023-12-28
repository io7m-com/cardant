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


package com.io7m.cardant.type_packages.internal;

import com.io7m.blackthorne.core.BTElementHandlerConstructorType;
import com.io7m.blackthorne.core.BTElementHandlerType;
import com.io7m.blackthorne.core.BTElementParsingContextType;
import com.io7m.blackthorne.core.BTQualifiedName;
import com.io7m.cardant.model.type_package.CATypePackageImport;
import com.io7m.lanark.core.RDottedName;
import com.io7m.verona.core.VersionRange;
import org.xml.sax.Attributes;

import java.util.Map;

import static com.io7m.cardant.type_packages.internal.CATP1Names.qName;

/**
 * A parser.
 */

public final class CATP1Import
  implements BTElementHandlerType<VersionRange, CATypePackageImport>
{
  private RDottedName name;
  private VersionRange versionRange;

  /**
   * A parser.
   *
   * @param context The parse context
   */

  public CATP1Import(
    final BTElementParsingContextType context)
  {

  }

  @Override
  public void onElementStart(
    final BTElementParsingContextType context,
    final Attributes attributes)
  {
    this.name =
      new RDottedName(attributes.getValue("Package"));
  }

  @Override
  public Map<BTQualifiedName,
    BTElementHandlerConstructorType<?, ? extends VersionRange>>
  onChildHandlersRequested(
    final BTElementParsingContextType context)
  {
    return Map.ofEntries(
      Map.entry(qName("VersionRange"), CATP1VersionRange::new)
    );
  }

  @Override
  public void onChildValueProduced(
    final BTElementParsingContextType context,
    final VersionRange result)
  {
    this.versionRange = result;
  }

  @Override
  public CATypePackageImport onElementFinished(
    final BTElementParsingContextType context)
  {
    return new CATypePackageImport(
      this.name,
      this.versionRange
    );
  }
}
