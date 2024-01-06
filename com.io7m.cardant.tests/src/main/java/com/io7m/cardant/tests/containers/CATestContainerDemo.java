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


package com.io7m.cardant.tests.containers;

import com.io7m.ervilla.api.EContainerConfiguration;
import com.io7m.ervilla.api.EContainerSupervisorScope;
import com.io7m.ervilla.native_exec.ENContainerSupervisors;
import com.io7m.lanark.core.RDottedName;

import java.nio.file.Files;

import static java.util.concurrent.TimeUnit.SECONDS;

public final class CATestContainerDemo
{
  private CATestContainerDemo()
  {

  }

  public static void main(
    final String[] args)
    throws Exception
  {
    final var supervisors =
      new ENContainerSupervisors();
    final var configuration =
      new EContainerConfiguration(
        new RDottedName("com.io7m.cardant"), "podman", 180L, SECONDS);

    final var directory =
      Files.createTempDirectory("cardant-");

    try (var supervisor = supervisors.create(configuration, EContainerSupervisorScope.PER_SUITE)) {
      final var databaseFixture =
        CATestContainers.createDatabase(supervisor, 25432);

      final var idstoreFixture =
        CATestContainers.createIdstore(
          supervisor,
          databaseFixture,
          directory,
          "idstore",
          51000,
          50000,
          50001
        );

      while (true) {
        Thread.sleep(5000L);
      }
    }
  }
}
