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

import com.io7m.cardant.client.preferences.api.CAPreferencesServiceType;
import com.io7m.cardant.client.preferences.vanilla.CAPreferencesService;
import com.io7m.cardant.security.CASecurityPolicy;
import com.io7m.cardant.shell.CAShellConfiguration;
import com.io7m.cardant.shell.CAShellType;
import com.io7m.cardant.shell.CAShells;
import com.io7m.cardant.tests.CATestDirectories;
import com.io7m.cardant.tests.containers.CATestContainers;
import com.io7m.ervilla.api.EContainerSupervisorType;
import com.io7m.ervilla.test_extension.ErvillaCloseAfterClass;
import com.io7m.ervilla.test_extension.ErvillaConfiguration;
import com.io7m.ervilla.test_extension.ErvillaExtension;
import com.io7m.zelador.test_extension.CloseableResourcesType;
import com.io7m.zelador.test_extension.ZeladorExtension;
import org.junit.jupiter.api.AfterEach;
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
@ErvillaConfiguration(projectName = "com.io7m.cardant", disabledIfUnsupported = true)
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
  private CAPreferencesServiceType preferences;

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

    this.preferences =
      CAPreferencesService.openOrDefault(
        this.directory.resolve("preferences.xml"));

    this.terminal =
      new CAFakeTerminal();
    this.shells =
      new CAShells();
    this.configuration =
      new CAShellConfiguration(
        this.preferences,
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
  }

  @AfterEach
  public void tearDown()
  {
    final var out =
      this.terminal.terminalProducedOutput();
    final var outText =
      out.toString(StandardCharsets.UTF_8);

    System.out.println(outText);
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

  private String host()
  {
    return this.server.server()
      .inventoryAPI()
      .getHost();
  }

  private String port()
  {
    return Integer.toString(
      this.server.server()
        .inventoryAPI()
        .getPort()
    );
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
  public void testShellLocationAttachmentWorkflow()
    throws Exception
  {
    this.startShell();

    final var fileNameUp =
      this.directory.resolve("fileUp.txt");

    Files.writeString(fileNameUp, "HELLO!");

    final var w = this.terminal.sendInputToTerminalWriter();
    w.println("set --terminate-on-errors true");
    w.printf("login %s someone-admin 12345678%n", this.uri());

    final var fileId =
      "544c6447-b5dd-4df9-a5c5-78e70b486fcf";
    final var locationId =
      "6c44c6ad-3fb9-4b2c-9230-4eabaf9295ae";

    w.printf(
      "file-put --id %s --file %s%n",
      fileId,
      fileNameUp.toAbsolutePath()
    );
    w.printf(
      "location-put --id %s --name Battery%n",
      locationId
    );
    w.printf(
      "location-attachment-add --id %s --relation icon --file-id %s%n",
      locationId,
      fileId
    );
    w.printf(
      "location-attachment-remove --id %s --relation icon --file-id %s%n",
      locationId,
      fileId
    );
    w.println("logout");
    w.flush();
    w.close();

    this.waitForShell();
    assertEquals(0, this.exitCode);
  }

  @Test
  public void testShellLocations()
    throws Exception
  {
    this.startShell();

    final var w = this.terminal.sendInputToTerminalWriter();
    w.println("set --terminate-on-errors true");
    w.printf("login %s someone-admin 12345678%n", this.uri());

    w.println(
      "location-put --id 9f87685b-121e-4209-b864-80b0752132b5 " +
        "--name 'Location 0'"
    );
    w.println(
      "location-put --id 9f87685b-121e-4209-b864-80b0752132b5 " +
        "--name 'Location 0 (A)'"
    );
    w.println(
      "location-put --id e892e153-6487-46eb-9042-4cf6c21953a4 " +
        "--name 'Location 1'"
    );
    w.println(
      "location-put " +
        "--id 9f87685b-121e-4209-b864-80b0752132b5 " +
        "--parent e892e153-6487-46eb-9042-4cf6c21953a4 "
    );
    w.println(
      "location-put " +
        "--id 9f87685b-121e-4209-b864-80b0752132b5 " +
        "--detach true "
    );

    w.println("location-list");
    w.println("location-get --id 9f87685b-121e-4209-b864-80b0752132b5 ");
    w.println("logout");
    w.flush();
    w.close();

    this.waitForShell();
    assertEquals(0, this.exitCode);
  }

  @Test
  public void testShellLocationsCombinationNotAllowed()
    throws Exception
  {
    this.startShell();

    final var w = this.terminal.sendInputToTerminalWriter();
    w.println("set --terminate-on-errors true");
    w.printf("login %s someone-admin 12345678%n", this.uri());
    w.println(
      "location-put " +
        "--id 9f87685b-121e-4209-b864-80b0752132b5 " +
        "--name 'Location 0' " +
        "--parent 2d566517-195f-4e4e-9a3d-f86b6b876f46 " +
        "--detach true"
    );
    w.flush();
    w.close();

    this.waitForShell();
    assertEquals(1, this.exitCode);
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
                "--metadata '[integer voltage 9]'");
    w.println("item-metadata-put --id 8d64fc55-beae-4a91-ad45-d6968e9b82c4 " +
                "--metadata '[integer size 23]'");
    w.println("item-metadata-put --id 8d64fc55-beae-4a91-ad45-d6968e9b82c4 " +
                "--metadata '[integer gauge 20]'");
    w.println("item-metadata-remove --id 8d64fc55-beae-4a91-ad45-d6968e9b82c4 " +
                "--key gauge");
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
  public void testShellItemAttachmentWorkflow()
    throws Exception
  {
    this.startShell();

    final var fileNameUp =
      this.directory.resolve("fileUp.txt");

    Files.writeString(fileNameUp, "HELLO!");

    final var w = this.terminal.sendInputToTerminalWriter();
    w.println("set --terminate-on-errors true");
    w.printf("login %s someone-admin 12345678%n", this.uri());

    final var fileId =
      "544c6447-b5dd-4df9-a5c5-78e70b486fcf";
    final var itemId =
      "6c44c6ad-3fb9-4b2c-9230-4eabaf9295ae";

    w.printf(
      "file-put --id %s --file %s%n",
      fileId,
      fileNameUp.toAbsolutePath()
    );
    w.printf(
      "item-create --id %s --name Battery%n",
      itemId
    );
    w.printf(
      "item-attachment-add --id %s --relation icon --file-id %s%n",
      itemId,
      fileId
    );
    w.printf(
      "item-attachment-remove --id %s --relation icon --file-id %s%n",
      itemId,
      fileId
    );
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

  @Test
  public void testShellBookmarksWorkflow()
    throws Exception
  {
    this.startShell();

    final var w = this.terminal.sendInputToTerminalWriter();
    w.println("set --terminate-on-errors true");

    w.printf(
      "bookmark-put --name b1 " +
        "--hostname %s " +
        "--port %s " +
        "--user %s " +
        "--password %s%n",
      this.host(),
      this.port(),
      "someone-admin",
      "12345678"
    );
    w.printf("bookmark-list%n");
    w.printf("bookmark-login --name b1%n");
    w.printf("bookmark-remove --name b1%n");
    w.printf("bookmark-list%n");
    w.println("logout");
    w.flush();
    w.close();

    this.waitForShell();
    assertEquals(0, this.exitCode);
  }

  @Test
  public void testShellTypeScalarsWorkflow()
    throws Exception
  {
    this.startShell();

    final var w = this.terminal.sendInputToTerminalWriter();
    w.println("set --terminate-on-errors true");

    w.printf("login %s someone-admin 12345678%n", this.uri());
    w.println(
      "type-scalar-put " +
        "--name com.x " +
        "--description 'A description of things.' " +
        "--base-is-text '.*'"
    );
    w.println(
      "type-scalar-put " +
        "--name com.y " +
        "--description 'A description of things.' " +
        "--base-is-integral '[23 24]'"
    );
    w.println(
      "type-scalar-put " +
        "--name com.z " +
        "--description 'A description of things.' " +
        "--base-is-real '[23 24]'"
    );
    w.println(
      "type-scalar-put " +
        "--name com.a " +
        "--description 'A description of things.' " +
        "--base-is-time '[2023-01-01T00:00:00+00:00 2023-03-01T00:00:00+00:00]'"
    );
    w.println(
      "type-scalar-put " +
        "--name com.b " +
        "--description 'A description of things.' " +
        "--base-is-monetary '[23 24]'"
    );
    w.println("type-scalar-get --name com.x");
    w.println("type-scalar-search-begin --query things");
    w.println("type-scalar-search-next");
    w.println("type-scalar-search-previous");
    w.println("type-scalar-remove --name com.x");
    w.println("type-scalar-search-begin --query things");
    w.println("type-scalar-search-next");
    w.println("type-scalar-search-previous");
    w.println("logout");
    w.flush();
    w.close();

    this.waitForShell();
    assertEquals(0, this.exitCode);
  }

  @Test
  public void testShellTypeDeclarationWorkflow()
    throws Exception
  {
    this.startShell();

    final var w = this.terminal.sendInputToTerminalWriter();
    w.println("set --terminate-on-errors true");

    w.printf("login %s someone-admin 12345678%n", this.uri());
    w.println(
      "type-scalar-put " +
        "--name com.x " +
        "--description 'A description of things.' " +
        "--base-is-text '.*'"
    );
    w.println("type-create --name com.y --description 'A type'");
    w.println(
      "type-field-put " +
        "--type com.y " +
        "--field-name x " +
        "--field-type com.x " +
        "--field-description 'A field' " +
        "--field-required true"
    );
    w.println(
      "type-field-put " +
        "--type com.y " +
        "--field-name y " +
        "--field-type com.x " +
        "--field-description 'Another field' " +
        "--field-required true"
    );
    w.println(
      "type-field-put " +
        "--type com.y " +
        "--field-name z " +
        "--field-type com.x " +
        "--field-description 'Yet another field' " +
        "--field-required false"
    );
    w.println(
      "type-field-put " +
        "--type com.y " +
        "--field-name a " +
        "--field-type com.x " +
        "--field-description 'A wrong field' " +
        "--field-required true"
    );
    w.println(
      "type-field-remove --type com.y --field-name a"
    );
    w.println("type-get --name com.y");
    w.println("type-search-begin --query type");
    w.println("type-search-next");
    w.println("type-search-previous");
    w.println("logout");
    w.flush();
    w.close();

    this.waitForShell();
    assertEquals(0, this.exitCode);
  }

  @Test
  public void testShellRolesWorkflow()
    throws Exception
  {
    this.startShell();

    final var w = this.terminal.sendInputToTerminalWriter();
    w.println("set --terminate-on-errors true");
    w.printf("login %s someone 12345678%n", this.uri());
    w.printf("login %s someone-admin 12345678%n", this.uri());
    w.println(
      "roles-assign " +
        "--user " + USER + " " +
        "--role " + CASecurityPolicy.ROLE_INVENTORY_FILES_READER.value() + " "
    );
    w.println(
      "roles-revoke " +
        "--user " + USER + " " +
        "--role " + CASecurityPolicy.ROLE_INVENTORY_FILES_READER.value() + " "
    );
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
