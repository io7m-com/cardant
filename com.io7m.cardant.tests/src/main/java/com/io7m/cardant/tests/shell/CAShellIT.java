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

import com.io7m.cardant.shell.CAShellConfiguration;
import com.io7m.cardant.shell.CAShellType;
import com.io7m.cardant.shell.CAShells;
import com.io7m.cardant.tests.CATestDirectories;
import com.io7m.cardant.tests.containers.CATestContainers;
import com.io7m.ervilla.api.EContainerSupervisorType;
import com.io7m.ervilla.test_extension.ErvillaCloseAfterAll;
import com.io7m.ervilla.test_extension.ErvillaConfiguration;
import com.io7m.ervilla.test_extension.ErvillaExtension;
import com.io7m.zelador.test_extension.CloseableResourcesType;
import com.io7m.zelador.test_extension.ZeladorExtension;
import org.junit.jupiter.api.BeforeAll;
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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Timeout(value = 30L)
@ExtendWith({ErvillaExtension.class, ZeladorExtension.class})
@ErvillaConfiguration(disabledIfUnsupported = true)
public final class CAShellIT
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAShellIT.class);

  private static UUID USER_ADMIN;
  private static UUID USER;
  private static CATestContainers.CAIdstoreFixture IDSTORE;
  private static CATestContainers.CADatabaseFixture DATABASE;
  private static Path DIRECTORY;
  
  private CATestContainers.CAServerFixture server;
  private CAShells shells;
  private CAShellConfiguration configuration;
  private CAFakeTerminal terminal;
  private ExecutorService executor;
  private volatile int exitCode;
  private CountDownLatch startupLatch;
  private CountDownLatch shutDownLatch;
  private Path directory;
  private volatile CAShellType shellLeaked;

  @BeforeAll
  public static void setupOnce(
    final @ErvillaCloseAfterAll EContainerSupervisorType supervisor,
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
        DIRECTORY,
        15432,
        51000,
        50000,
        50001
      );

    USER_ADMIN = IDSTORE.createUser("someone-admin");
    USER = IDSTORE.createUser("someone");

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

    this.server =
      closeables.addPerTestResource(
        CATestContainers.createServer(
          IDSTORE,
          DATABASE,
          30000
        )
      );

    this.server.setUserAsAdmin(USER_ADMIN, "someone-admin");

    closeables.addPerTestResource(
      () -> CATestDirectories.deleteDirectory(this.directory)
    );
    closeables.addPerTestResource(
      () -> {
        this.executor.shutdown();
        this.executor.awaitTermination(3L, TimeUnit.SECONDS);
      }
    );

    final var out =
      this.terminal.terminalProducedOutput();

    System.out.println(out.toString(StandardCharsets.UTF_8));
  }

  private void waitForShell()
    throws InterruptedException
  {
    this.shutDownLatch.await(3L, TimeUnit.SECONDS);
  }

  private String uri()
  {
    return this.server.server()
      .inventoryAPI()
      .toString();
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
  public void testShellLogin()
    throws Exception
  {
    this.startShell();

    final var w = this.terminal.sendInputToTerminalWriter();
    w.printf("login %s someone-admin 12345678%n", this.uri());
    w.println("self");
    w.println("logout");
    w.flush();
    w.close();

    this.waitForShell();
    assertEquals(0, this.exitCode);
  }

  @Test
  public void testShellSetFailure()
    throws Exception
  {
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
  public void testShellLocations()
    throws Exception
  {
    this.startShell();

    final var w = this.terminal.sendInputToTerminalWriter();
    w.println("set --terminate-on-errors true");
    w.printf("login %s someone-admin 12345678%n", this.uri());
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
  public void testShellCreateLocationItemsWorkflow()
    throws Exception
  {
    this.startShell();

    final var w = this.terminal.sendInputToTerminalWriter();
    w.println("set --terminate-on-errors true");
    w.printf("login %s someone-admin 12345678%n", this.uri());
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
  public void testShellFileUploadDownload()
    throws Exception
  {
    this.startShell();

    final var fileNameUp =
      this.directory.resolve("fileUp.txt");
    final var fileNameDown =
      this.directory.resolve("fileDown.txt");

    Files.writeString(fileNameUp, "HELLO!");

    final var w = this.terminal.sendInputToTerminalWriter();
    w.println("set --terminate-on-errors true");
    w.printf("login %s someone-admin 12345678%n", this.uri());

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
  public void testShellFileSearch()
    throws Exception
  {
    this.startShell();

    final var fileNameUp =
      this.directory.resolve("fileUp.txt");

    Files.writeString(fileNameUp, "HELLO!");

    final var w = this.terminal.sendInputToTerminalWriter();
    w.println("set --terminate-on-errors true");
    w.printf("login %s someone-admin 12345678%n", this.uri());

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
