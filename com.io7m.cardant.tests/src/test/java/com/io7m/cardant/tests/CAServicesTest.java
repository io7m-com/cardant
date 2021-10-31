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

package com.io7m.cardant.tests;

import com.io7m.cardant.services.api.CAServiceDirectory;
import com.io7m.cardant.services.api.CAServiceException;
import com.io7m.cardant.services.api.CAServiceType;
import org.junit.jupiter.api.Test;

import java.io.Closeable;
import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class CAServicesTest
{
  @Test
  public void testRequiredButEmpty()
  {
    final var services = new CAServiceDirectory();
    assertThrows(CAServiceException.class, () -> {
      services.requireService(CAServiceType.class);
    });
  }

  @Test
  public void testRequiredButOptional()
  {
    final var services = new CAServiceDirectory();
    assertFalse(services.optionalService(CAServiceType.class).isPresent());
  }

  @Test
  public void testRequiredPresent()
  {
    final var services = new CAServiceDirectory();

    final var s0 = new Service0();
    final var s1 = new Service1();
    final var s2 = new Service2();
    services.register(Service0.class, s0);
    services.register(Service1.class, s1);
    services.register(Service2.class, s2);

    assertEquals(s0, services.requireService(Service0.class));
    assertEquals(s1, services.requireService(Service1.class));
    assertEquals(s2, services.requireService(Service2.class));
  }

  @Test
  public void testOptionalPresent()
  {
    final var services = new CAServiceDirectory();

    final var s0 = new Service0();
    final var s1 = new Service1();
    final var s2 = new Service2();
    services.register(Service0.class, s0);
    services.register(Service1.class, s1);
    services.register(Service2.class, s2);

    assertEquals(s0, services.optionalService(Service0.class).get());
    assertEquals(s1, services.optionalService(Service1.class).get());
    assertEquals(s2, services.optionalService(Service2.class).get());
  }

  @Test
  public void testOptionalMultiplePresent()
  {
    final var services = new CAServiceDirectory();

    final var s0a = new Service0();
    final var s0b = new Service0();
    final var s0c = new Service0();
    services.register(Service0.class, s0a);
    services.register(Service0.class, s0b);
    services.register(Service0.class, s0c);

    assertEquals(
      Set.of(s0a, s0b, s0c),
      Set.copyOf(services.optionalServices(Service0.class))
    );
  }

  @Test
  public void testClose()
    throws IOException
  {
    final var services = new CAServiceDirectory();

    final var s0 = new Service0();
    final var s1 = new Service1();
    final var s2 = new Service2();
    services.register(Service0.class, s0);
    services.register(Service1.class, s1);
    services.register(Service2.class, s2);

    assertFalse(s0.closed);
    assertFalse(s1.closed);
    assertFalse(s2.closed);

    services.close();

    assertTrue(s0.closed);
    assertTrue(s1.closed);
    assertTrue(s2.closed);
  }

  @Test
  public void testCloseCrashes()
  {
    final var services = new CAServiceDirectory();

    final var s0 = new Service0();
    final var s1 = new Service1();
    final var s2 = new Service2();
    services.register(Service0.class, s0);
    services.register(Service1.class, s1);
    services.register(Service2.class, s2);

    assertFalse(s0.closed);
    assertFalse(s1.closed);
    assertFalse(s2.closed);

    s0.crash = true;
    s1.crash = true;
    s2.crash = true;
    assertThrows(IOException.class, services::close);

    assertTrue(s0.closed);
    assertTrue(s1.closed);
    assertTrue(s2.closed);
  }

  private static final class Service0
    implements CAServiceType, Closeable
  {
    private boolean closed;
    private boolean crash;

    @Override
    public void close()
      throws IOException
    {
      this.closed = true;
      if (this.crash) {
        throw new IOException();
      }
    }
  }

  private static final class Service1
    implements CAServiceType, Closeable
  {
    private boolean closed;
    private boolean crash;

    @Override
    public void close()
      throws IOException
    {
      this.closed = true;
      if (this.crash) {
        throw new IOException();
      }
    }
  }

  private static final class Service2
    implements CAServiceType, Closeable
  {
    private boolean closed;
    private boolean crash;

    @Override
    public void close()
      throws IOException
    {
      this.closed = true;
      if (this.crash) {
        throw new IOException();
      }
    }
  }
}
