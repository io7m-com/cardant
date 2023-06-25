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

package com.io7m.cardant.tests.shell;

import com.io7m.cardant.server.api.CAServerType;
import com.io7m.cardant.shell.CAShellConfiguration;
import com.io7m.cardant.shell.CAShellType;
import com.io7m.cardant.shell.CAShells;
import com.io7m.cardant.tests.CATestDirectories;
import com.io7m.cardant.tests.server.CAServerExtension;
import com.io7m.idstore.admin_client.IdAClients;
import com.io7m.idstore.admin_client.api.IdAClientConfiguration;
import com.io7m.idstore.admin_client.api.IdAClientCredentials;
import com.io7m.idstore.admin_client.api.IdAClientException;
import com.io7m.idstore.model.IdEmail;
import com.io7m.idstore.model.IdName;
import com.io7m.idstore.model.IdPasswordAlgorithmPBKDF2HmacSHA256;
import com.io7m.idstore.model.IdRealName;
import com.io7m.idstore.protocol.admin.IdACommandUserCreate;
import com.io7m.idstore.server.api.IdServerType;
import com.io7m.idstore.tests.extensions.IdTestExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(IdTestExtension.class)
@ExtendWith(CAServerExtension.class)
@Timeout(value = 10L)
public final class CAShellIT
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAShellIT.class);

  private CAShells shells;
  private CAShellConfiguration configuration;
  private CAFakeTerminal terminal;
  private ExecutorService executor;
  private volatile int exitCode;
  private CountDownLatch startupLatch;
  private CountDownLatch shutDownLatch;
  private Path directory;
  private volatile CAShellType shellLeaked;

  private static void configureAdminAndUser(
    final IdServerType idServer,
    final CAServerType caServer)
    throws Exception
  {
    idServer.close();
    idServer.setup(
      Optional.empty(),
      new IdName("admin"),
      new IdEmail("someone@example.com"),
      new IdRealName("AM"),
      "1234"
    );
    idServer.start();

    final var clients =
      new IdAClients();
    final var client =
      clients.openSynchronousClient(new IdAClientConfiguration(Locale.ROOT));

    client.loginOrElseThrow(
      new IdAClientCredentials(
        "admin",
        "1234",
        idServer.adminAPI(),
        Map.of()),
      IdAClientException::ofError
    );

    final var userId =
      UUID.fromString("80189d65-7a7a-44fd-afd9-c94a2a915a0b");

    client.executeOrElseThrow(
      new IdACommandUserCreate(
        Optional.of(userId),
        new IdName("user"),
        new IdRealName("User"),
        new IdEmail("user@example.com"),
        IdPasswordAlgorithmPBKDF2HmacSHA256.create()
          .createHashed("1234")
      ),
      IdAClientException::ofError
    );

    caServer.close();
    caServer.setUserAsAdmin(userId, new IdName("user"));
    caServer.start();
  }

  private void waitForShell()
    throws InterruptedException
  {
    this.shutDownLatch.await(3L, TimeUnit.SECONDS);
  }

  @BeforeEach
  public void setup()
    throws Exception
  {
    this.directory =
      CATestDirectories.createTempDirectory();

    this.terminal =
      new CAFakeTerminal();
    this.shells =
      new CAShells();
    this.configuration =
      new CAShellConfiguration(
        Locale.ROOT,
        Clock.systemUTC(),
        Optional.of(this.terminal)
      );
    this.executor =
      Executors.newFixedThreadPool(1);

    this.startupLatch = new CountDownLatch(1);
    this.shutDownLatch = new CountDownLatch(1);
    this.exitCode = 0;
  }

  @AfterEach
  public void tearDown()
    throws Exception
  {
    this.executor.shutdown();
    this.executor.awaitTermination(3L, TimeUnit.SECONDS);

    final var out =
      this.terminal.terminalProducedOutput();

    System.out.println(out.toString(StandardCharsets.UTF_8));

    CATestDirectories.deleteDirectory(this.directory);
  }

  @Test
  public void testShellUnrecognized()
    throws Exception
  {
    this.startShell();

    final var w = this.terminal.sendInputToTerminalWriter();
    w.println("nonexistent");
    w.flush();
    w.close();

    this.waitForShell();
    assertEquals(1, this.exitCode);
  }

  @Test
  public void testShellHelp()
    throws Exception
  {
    this.startShell();

    final var w = this.terminal.sendInputToTerminalWriter();
    w.println("help");

    this.startupLatch.await(5L, TimeUnit.SECONDS);

    for (final var c : this.shellLeaked.commands()) {
      w.println("help " + c.metadata().name());
    }

    for (final var c : this.shellLeaked.commands()) {
      final String cts = c.toString();
      final String name = c.getClass().getSimpleName();
      LOG.debug("{}", cts);
      assertEquals("[" + name + "]", cts);
    }

    w.flush();
    w.close();

    this.waitForShell();
    assertEquals(0, this.exitCode);
  }

  @Test
  public void testShellVersion()
    throws Exception
  {
    this.startShell();

    final var w = this.terminal.sendInputToTerminalWriter();
    w.println("version");
    w.flush();
    w.close();

    this.waitForShell();
    assertEquals(0, this.exitCode);
  }

  @Test
  public void testShellLogin(
    final IdServerType idServer,
    final CAServerType caServer)
    throws Exception
  {
    configureAdminAndUser(idServer, caServer);
    this.startShell();

    final var w = this.terminal.sendInputToTerminalWriter();
    w.printf("login %s admin 1234%n", idServer.adminAPI());
    w.println("self");
    w.println("logout");
    w.flush();
    w.close();

    this.waitForShell();
    assertEquals(0, this.exitCode);
  }

  @Test
  public void testShellSetFailure(
    final IdServerType idServer,
    final CAServerType caServer)
    throws Exception
  {
    configureAdminAndUser(idServer, caServer);
    this.startShell();

    final var w = this.terminal.sendInputToTerminalWriter();
    w.println("set --terminate-on-errors true");
    w.println("mysterious");
    w.flush();
    w.close();

    this.waitForShell();
    assertEquals(1, this.exitCode);
  }

  @Test
  public void testShellLocations(
    final IdServerType idServer,
    final CAServerType caServer)
    throws Exception
  {
    configureAdminAndUser(idServer, caServer);

    this.startShell();

    final var w = this.terminal.sendInputToTerminalWriter();
    w.println("set --terminate-on-errors true");
    w.printf("login %s user 1234%n", caServer.inventoryAPI());
    w.println("location-put --id 9f87685b-121e-4209-b864-80b0752132b5 " +
              "--name 'Location 0'");
    w.println("location-put --id 9f87685b-121e-4209-b864-80b0752132b5 " +
              "--name 'Location 0 (A)'");
    w.println("location-list");
    w.println("location-get --id 9f87685b-121e-4209-b864-80b0752132b5 ");
    w.println("logout");
    w.flush();
    w.close();

    this.waitForShell();
    assertEquals(0, this.exitCode);
  }

  @Test
  public void testShellCreateLocationItemsWorkflow(
    final IdServerType idServer,
    final CAServerType caServer)
    throws Exception
  {
    configureAdminAndUser(idServer, caServer);

    this.startShell();

    final var w = this.terminal.sendInputToTerminalWriter();
    w.println("set --terminate-on-errors true");
    w.printf("login %s user 1234%n", caServer.inventoryAPI());
    w.println("location-put --id 9f87685b-121e-4209-b864-80b0752132b5 " +
              "--name 'Location 0'");
    w.println("location-put --id 544c6447-b5dd-4df9-a5c5-78e70b486fcf " +
              "--name 'Location 1'");
    w.println("item-create --id 8d64fc55-beae-4a91-ad45-d6968e9b82c4 " +
              "--name 'Battery'");
    w.println("item-metadata-put --id 8d64fc55-beae-4a91-ad45-d6968e9b82c4 " +
              "--key Voltage " +
              "--value 9");
    w.println("item-metadata-put --id 8d64fc55-beae-4a91-ad45-d6968e9b82c4 " +
              "--key Size " +
              "--value 23");
    w.println("item-metadata-put --id 8d64fc55-beae-4a91-ad45-d6968e9b82c4 " +
              "--key Gauge " +
              "--value 20");
    w.println("item-metadata-remove --id 8d64fc55-beae-4a91-ad45-d6968e9b82c4 " +
              "--key Gauge");
    w.println("item-reposit-add --item 8d64fc55-beae-4a91-ad45-d6968e9b82c4 " +
              "--location 9f87685b-121e-4209-b864-80b0752132b5 " +
              "--count 100");
    w.println("item-reposit-add --item 8d64fc55-beae-4a91-ad45-d6968e9b82c4 " +
              "--location 544c6447-b5dd-4df9-a5c5-78e70b486fcf " +
              "--count 50");
    w.println("item-reposit-move --item 8d64fc55-beae-4a91-ad45-d6968e9b82c4 " +
              "--location-from 9f87685b-121e-4209-b864-80b0752132b5 " +
              "--location-to 544c6447-b5dd-4df9-a5c5-78e70b486fcf " +
              "--count 15");
    w.println("item-reposit-remove --item 8d64fc55-beae-4a91-ad45-d6968e9b82c4 " +
              "--location 544c6447-b5dd-4df9-a5c5-78e70b486fcf " +
              "--count 2");
    w.println("item-get --id 8d64fc55-beae-4a91-ad45-d6968e9b82c4");
    w.println("item-search-begin");
    w.println("item-search-next");
    w.println("item-search-previous");
    w.println("logout");
    w.flush();
    w.close();

    this.waitForShell();
    assertEquals(0, this.exitCode);
  }

  @Test
  public void testShellFileUploadDownload(
    final IdServerType idServer,
    final CAServerType caServer)
    throws Exception
  {
    configureAdminAndUser(idServer, caServer);

    this.startShell();

    final var fileNameUp =
      this.directory.resolve("fileUp.txt");
    final var fileNameDown =
      this.directory.resolve("fileDown.txt");

    Files.writeString(fileNameUp, "HELLO!");

    final var w = this.terminal.sendInputToTerminalWriter();
    w.println("set --terminate-on-errors true");
    w.printf("login %s user 1234%n", caServer.inventoryAPI());

    w.println(
      "file-put --id 544c6447-b5dd-4df9-a5c5-78e70b486fcf --file "
      + fileNameUp.toAbsolutePath());

    w.println(
      "file-get --id 544c6447-b5dd-4df9-a5c5-78e70b486fcf --download-to "
      + fileNameDown.toAbsolutePath());

    w.println("logout");
    w.flush();
    w.close();

    this.waitForShell();

    assertEquals("HELLO!", Files.readString(fileNameDown));
    assertEquals(0, this.exitCode);
  }

  @Test
  public void testShellFileSearch(
    final IdServerType idServer,
    final CAServerType caServer)
    throws Exception
  {
    configureAdminAndUser(idServer, caServer);

    this.startShell();

    final var fileNameUp =
      this.directory.resolve("fileUp.txt");

    Files.writeString(fileNameUp, "HELLO!");

    final var w = this.terminal.sendInputToTerminalWriter();
    w.println("set --terminate-on-errors true");
    w.printf("login %s user 1234%n", caServer.inventoryAPI());

    w.println(
      "file-put --id 544c6447-b5dd-4df9-a5c5-78e70b486fcf --file "
      + fileNameUp.toAbsolutePath());

    w.println("file-search-begin");
    w.println("file-search-next");
    w.println("file-search-previous");
    w.println("logout");
    w.flush();
    w.close();

    this.waitForShell();
    assertEquals(0, this.exitCode);
  }

  private void startShell()
  {
    this.executor.execute(() -> {
      LOG.debug("starting shell");
      try (var shell = this.shells.create(this.configuration)) {
        this.shellLeaked = shell;
        this.startupLatch.countDown();
        shell.run();
      } catch (final Throwable e) {
        LOG.debug("shell failed: ", e);
        throw new RuntimeException(e);
      } finally {
        LOG.debug("finished shell");
        this.exitCode = this.shellLeaked.exitCode();
        this.shutDownLatch.countDown();
      }
    });
  }
}
