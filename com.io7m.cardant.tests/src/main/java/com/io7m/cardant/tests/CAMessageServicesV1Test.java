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

package com.io7m.cardant.tests;

import com.io7m.cardant.protocol.inventory.CAICommandDebugInvalid;
import com.io7m.cardant.protocol.inventory.CAICommandDebugRandom;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.protocol.inventory.cb.CAI1Messages;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public final class CAMessageServicesV1Test
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAMessageServicesV1Test.class);

  @Property(tries = 3_000)
  public void testRoundTripCommands(
    final @ForAll CAICommandType<?> command)
    throws Exception
  {
    assumeFalse(command instanceof CAICommandDebugRandom);
    assumeFalse(command instanceof CAICommandDebugInvalid);

    LOG.debug("Check {}", command.getClass().getCanonicalName());

    final var messages =
      new CAI1Messages();
    final var serialized =
      messages.serialize(command);
    final var result =
      messages.parse(serialized);

    assertEquals(command, result);
    assertEquals(command.responseClass(), ((CAICommandType) result).responseClass());
  }

  @Property(tries = 3_000)
  public void testRoundTripResponses(
    final @ForAll CAIResponseType response)
    throws Exception
  {
    LOG.debug("Check {}", response.getClass().getCanonicalName());

    final var messages =
      new CAI1Messages();
    final var serialized =
      messages.serialize(response);
    final var result =
      messages.parse(serialized);

    assertEquals(response, result);
  }
}
