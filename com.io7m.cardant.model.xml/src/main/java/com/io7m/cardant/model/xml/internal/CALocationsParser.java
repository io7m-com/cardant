/*
 * Copyright © 2021 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.model.xml.internal;

import com.io7m.blackthorne.api.BTElementHandlerConstructorType;
import com.io7m.blackthorne.api.BTElementHandlerType;
import com.io7m.blackthorne.api.BTElementParsingContextType;
import com.io7m.blackthorne.api.BTQualifiedName;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocations;

import java.util.Map;
import java.util.TreeMap;

import static com.io7m.cardant.model.xml.CAInventorySchemas.element1;
import static java.util.Map.entry;

/**
 * A parser.
 */

public final class CALocationsParser implements BTElementHandlerType<CALocation, CALocations>
{
  private final TreeMap<CALocationID, CALocation> results;

  /**
   * Construct a parser.
   *
   * @param context The parse context
   */

  public CALocationsParser(
    final BTElementParsingContextType context)
  {
    this.results = new TreeMap<>();
  }

  @Override
  public Map<BTQualifiedName, BTElementHandlerConstructorType<?, ? extends CALocation>>
  onChildHandlersRequested(final BTElementParsingContextType context)
  {
    return Map.ofEntries(
      entry(element1("Location"), CALocationParser::new)
    );
  }

  @Override
  public void onChildValueProduced(
    final BTElementParsingContextType context,
    final CALocation result)
  {
    this.results.put(result.id(), result);
  }

  @Override
  public CALocations onElementFinished(
    final BTElementParsingContextType context)
  {
    return new CALocations(this.results);
  }
}
