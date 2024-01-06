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

package com.io7m.cardant.tests.server.v1;

import com.io7m.cardant.tests.CATestDirectories;
import com.io7m.cardant.tests.containers.CATestContainers;
import com.io7m.ervilla.api.EContainerSupervisorType;
import com.io7m.ervilla.test_extension.ErvillaCloseAfterClass;
import com.io7m.ervilla.test_extension.ErvillaConfiguration;
import com.io7m.ervilla.test_extension.ErvillaExtension;
import com.io7m.zelador.test_extension.CloseableResourcesType;
import com.io7m.zelador.test_extension.ZeladorExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The health endpoint.
 */

@ExtendWith({ErvillaExtension.class, ZeladorExtension.class})
@ErvillaConfiguration(projectName = "com.io7m.cardant", disabledIfUnsupported = true)
public final class CAI1ServerTest
{
  private static CATestContainers.CAIdstoreFixture IDSTORE;
  private static CATestContainers.CADatabaseFixture DATABASE;
  private static Path DIRECTORY;
  private CATestContainers.CAServerFixture server;

  @BeforeAll
  public static void setupOnce(
    final @ErvillaCloseAfterClass EContainerSupervisorType supervisor,
    final CloseableResourcesType closeables)
    throws Exception
  {
    DIRECTORY =
      Files.createTempDirectory("cardant-");
    DATABASE =
      CATestContainers.createDatabase(supervisor, 15433);
    IDSTORE =
      CATestContainers.createIdstore(
        supervisor,
        DATABASE,
        DIRECTORY,
        "idstore",
        51000,
        50000,
        50001
      );

    closeables.addPerTestClassResource(
      () -> CATestDirectories.deleteDirectory(DIRECTORY)
    );
  }

  @BeforeEach
  public void setupEach(
    final CloseableResourcesType closeables)
    throws Exception
  {
    DATABASE.reset();

    this.server =
      closeables.addPerTestResource(
        CATestContainers.createServer(
          IDSTORE,
          DATABASE,
          30000
        )
      );
  }

  @Test
  public void testHealth()
    throws Exception
  {
    final var client =
      HttpClient.newHttpClient();

    final var request =
      HttpRequest.newBuilder(
          this.server.server()
            .inventoryAPI()
            .resolve("/health")
            .normalize()
        )
        .GET()
        .build();

    final var response =
      client.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(200, response.statusCode());
    assertEquals("OK", response.body());
  }
}
