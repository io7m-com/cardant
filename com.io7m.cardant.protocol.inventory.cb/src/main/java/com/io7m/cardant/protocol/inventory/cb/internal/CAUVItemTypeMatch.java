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


package com.io7m.cardant.protocol.inventory.cb.internal;

import com.io7m.cardant.model.CATypeMatchType;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1TypeMatch;
import com.io7m.cedarbridge.runtime.convenience.CBLists;
import com.io7m.cedarbridge.runtime.convenience.CBSets;
import com.io7m.lanark.core.RDottedName;

import static com.io7m.cedarbridge.runtime.api.CBCore.string;

/**
 * A validator.
 */

public enum CAUVItemTypeMatch
  implements CAProtocolMessageValidatorType<CATypeMatchType, CAI1TypeMatch>
{
  /**
   * A validator.
   */

  ITEM_TYPE_MATCH;

  @Override
  public CAI1TypeMatch convertToWire(
    final CATypeMatchType message)
  {
    return switch (message) {
      case final CATypeMatchType.CATypeMatchAny a -> {
        yield new CAI1TypeMatch.CAI1TypeMatchAny();
      }
      case final CATypeMatchType.CATypeMatchAllOf exact -> {
        yield new CAI1TypeMatch.CAI1TypeMatchAllOf(
          CBLists.ofCollection(exact.types(), x -> string(x.value()))
        );
      }
      case final CATypeMatchType.CATypeMatchAnyOf exact -> {
        yield new CAI1TypeMatch.CAI1TypeMatchAnyOf(
          CBLists.ofCollection(exact.types(), x -> string(x.value()))
        );
      }
    };
  }

  @Override
  public CATypeMatchType convertFromWire(
    final CAI1TypeMatch message)
  {
    return switch (message) {
      case final CAI1TypeMatch.CAI1TypeMatchAny a -> {
        yield CATypeMatchType.CATypeMatchAny.ANY;
      }
      case final CAI1TypeMatch.CAI1TypeMatchAllOf types -> {
        yield new CATypeMatchType.CATypeMatchAllOf(
          CBSets.toSet(types.fieldTypes(), x -> new RDottedName(x.value()))
        );
      }
      case final CAI1TypeMatch.CAI1TypeMatchAnyOf types -> {
        yield new CATypeMatchType.CATypeMatchAnyOf(
          CBSets.toSet(types.fieldTypes(), x -> new RDottedName(x.value()))
        );
      }
    };
  }
}
