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

import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.inventory.CAIEventType;
import com.io7m.cardant.protocol.inventory.CAIEventUpdated;
import com.io7m.cardant.protocol.inventory.CAIMessageType;
import com.io7m.cardant.protocol.inventory.cb.CAI1EventUpdated;
import com.io7m.cardant.protocol.inventory.cb.ProtocolCAIv1Type;
import com.io7m.cedarbridge.runtime.api.CBList;

import java.util.stream.Collectors;

public final class CAI1ValidationEvents
{
  private CAI1ValidationEvents()
  {

  }

  public static ProtocolCAIv1Type convertToWireEvent(
    final CAIEventType event)
    throws CAProtocolException
  {
    if (event instanceof final CAIEventUpdated u) {
      return convertToWireEventUpdated(u);
    }

    throw CAI1ValidationCommon.errorProtocol(event);
  }

  private static ProtocolCAIv1Type convertToWireEventUpdated(
    final CAIEventUpdated u)
  {
    return new CAI1EventUpdated(
      new CBList<>(
        u.updated()
          .stream()
          .map(CAI1ValidationCommon::convertToWireID)
          .toList()
      ),
      new CBList<>(
        u.removed()
          .stream()
          .map(CAI1ValidationCommon::convertToWireID)
          .toList()
      )
    );
  }

  public static CAIMessageType convertFromWireCAI1EventUpdated(
    final CAI1EventUpdated m)
  {
    return new CAIEventUpdated(
      m.fieldUpdated()
        .values()
        .stream()
        .map(CAI1ValidationCommon::convertFromWireId)
        .collect(Collectors.toUnmodifiableSet()),
      m.fieldRemoved()
        .values()
        .stream()
        .map(CAI1ValidationCommon::convertFromWireId)
        .collect(Collectors.toUnmodifiableSet())
    );
  }
}
