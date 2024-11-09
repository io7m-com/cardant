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


package com.io7m.cardant.tests.integration;

import com.io7m.cardant.client.api.CAClientConfiguration;
import com.io7m.cardant.client.api.CAClientConnectionParameters;
import com.io7m.cardant.client.api.CAClientException;
import com.io7m.cardant.client.api.CAClientType;
import com.io7m.cardant.client.basic.CAClients;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.model.CAAttachment;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocationPath;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.model.CATypeRecordFieldIdentifier;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.model.type_package.CATypePackageSearchParameters;
import com.io7m.cardant.model.type_package.CATypePackageTypeRemovalBehavior;
import com.io7m.cardant.model.type_package.CATypePackageUninstall;
import com.io7m.cardant.protocol.inventory.CAICommandDebugInvalid;
import com.io7m.cardant.protocol.inventory.CAICommandFileGet;
import com.io7m.cardant.protocol.inventory.CAICommandItemCreate;
import com.io7m.cardant.protocol.inventory.CAICommandItemGet;
import com.io7m.cardant.protocol.inventory.CAICommandLocationAttachmentAdd;
import com.io7m.cardant.protocol.inventory.CAICommandLocationAttachmentRemove;
import com.io7m.cardant.protocol.inventory.CAICommandLocationGet;
import com.io7m.cardant.protocol.inventory.CAICommandLocationMetadataPut;
import com.io7m.cardant.protocol.inventory.CAICommandLocationMetadataRemove;
import com.io7m.cardant.protocol.inventory.CAICommandLocationPut;
import com.io7m.cardant.protocol.inventory.CAICommandRolesAssign;
import com.io7m.cardant.protocol.inventory.CAICommandRolesGet;
import com.io7m.cardant.protocol.inventory.CAICommandRolesRevoke;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageGetText;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageInstall;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageSearchNext;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageSearchPrevious;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageUninstall;
import com.io7m.cardant.protocol.inventory.CAIResponseFileGet;
import com.io7m.cardant.tests.CATestDirectories;
import com.io7m.cardant.tests.containers.CADatabaseFixture;
import com.io7m.cardant.tests.containers.CAFixtures;
import com.io7m.cardant.tests.containers.CAIdstoreFixture;
import com.io7m.cardant.tests.containers.CAServerFixture;
import com.io7m.cardant.tests.server.controller.CAICmdTypePackageInstallTest;
import com.io7m.ervilla.api.EContainerSupervisorType;
import com.io7m.ervilla.test_extension.ErvillaCloseAfterSuite;
import com.io7m.ervilla.test_extension.ErvillaConfiguration;
import com.io7m.ervilla.test_extension.ErvillaExtension;
import com.io7m.idstore.model.IdName;
import com.io7m.lanark.core.RDottedName;
import com.io7m.medrina.api.MRoleName;
import com.io7m.verona.core.Version;
import com.io7m.zelador.test_extension.CloseableResourcesType;
import com.io7m.zelador.test_extension.ZeladorExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSecurityPolicyDenied;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
@Tag("client")
@ExtendWith({ErvillaExtension.class, ZeladorExtension.class})
@ErvillaConfiguration(projectName = "com.io7m.cardant", disabledIfUnsupported = true)
public final class CAClientIT
{
  private static final Duration TIMEOUT =
    Duration.ofSeconds(30L);

  private static CAUserID USER_ADMIN;
  private static CAUserID USER;
  private static CAIdstoreFixture IDSTORE;
  private static CADatabaseFixture DATABASE;
  private static Path DIRECTORY;
  private CAServerFixture server;

  @BeforeAll
  public static void setupOnce(
    final @ErvillaCloseAfterSuite EContainerSupervisorType supervisor,
    final CloseableResourcesType closeables)
    throws Exception
  {
    DIRECTORY =
      Files.createTempDirectory("cardant-");
    DATABASE =
      CAFixtures.database(CAFixtures.pod(supervisor));
    IDSTORE =
      CAFixtures.idstore(CAFixtures.pod(supervisor));

    USER_ADMIN =
      new CAUserID(IDSTORE.userWithAdmin().id());
    USER =
      new CAUserID(IDSTORE.userWithLogin().id());

    closeables.addPerTestClassResource(
      () -> CATestDirectories.deleteDirectory(DIRECTORY)
    );
  }

  @BeforeEach
  public void setupEach(
    final @ErvillaCloseAfterSuite EContainerSupervisorType supervisor,
    final CloseableResourcesType closeables)
    throws Exception
  {
    DATABASE.reset();

    this.directory =
      CATestDirectories.createTempDirectory();
    this.downloads =
      this.directory.resolve("downloads");

    this.clients =
      new CAClients();
    this.client =
      closeables.addPerTestResource(
        this.clients.create(
          new CAClientConfiguration(Locale.ROOT, Clock.systemUTC())
        )
      );

    this.server =
      closeables.addPerTestResource(
        CAFixtures.server(CAFixtures.pod(supervisor))
      );

    this.server.server().start();
    this.server.setUserAsAdmin(USER_ADMIN, "someone-admin");

    closeables.addPerTestResource(
      () -> CATestDirectories.deleteDirectory(this.directory)
    );
  }

  private CAClients clients;
  private CAClientType client;
  private Path directory;
  private Path downloads;

  /**
   * Logging in fails if the user does not exist.
   */

  @Test
  public void testLoginNoSuchUser()
  {
    final var ex =
      assertThrows(CAClientException.class, () -> {
        this.login(new IdName("nonexistent"));
      });

    assertEquals(CAStandardErrorCodes.errorAuthentication(), ex.errorCode());
  }

  /**
   * Logging in succeeds if the user exists.
   *
   * @throws Exception On errors
   */

  @Test
  public void testLoginOK()
    throws Exception
  {
    this.login(new IdName("someone"));
  }

  /**
   * The version endpoint returns something sensible.
   *
   * @throws Exception On errors
   */

  @Test
  public void testServerVersionEndpoint()
    throws Exception
  {
    final var httpClient =
      HttpClient.newHttpClient();

    final var request =
      HttpRequest.newBuilder(
          this.server.server()
            .inventoryAPI()
            .resolve("/version")
            .normalize()
        ).GET()
        .build();

    final var response =
      httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(
      "text/plain",
      response.headers()
        .firstValue("content-type")
        .orElseThrow()
    );
    assertTrue(response.body().startsWith("com.io7m.cardant "));
  }

  /**
   * Uploading a file works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testFileUploadOK()
    throws Exception
  {
    this.login(new IdName("someone-admin"));

    final var file = this.directory.resolve("data.txt");
    Files.writeString(file, "HELLO!");

    final var fileId = CAFileID.random();
    this.client.fileUpload(
      fileId,
      file,
      "text/plain",
      "A text file.",
      statistics -> {

      }
    );

    final var response =
      (CAIResponseFileGet)
        this.client.sendAndWaitOrThrow(new CAICommandFileGet(fileId), TIMEOUT);

    final var data = response.data();
    assertEquals(
      "a2f6017f1fab81333a4288f68557b74495a27337c7d37b3eba46c866aa885098",
      data.hashValue()
    );
    assertEquals(
      "SHA-256",
      data.hashAlgorithm()
    );
    assertEquals(6L, data.size());

    this.client.fileDownload(
      fileId,
      this.directory.resolve("data2.txt"),
      this.directory.resolve("data2.txt.tmp"),
      6L,
      "SHA-256",
      "a2f6017f1fab81333a4288f68557b74495a27337c7d37b3eba46c866aa885098",
      statistics -> {

      }
    );

    assertEquals(
      "HELLO!",
      Files.readString(this.directory.resolve("data2.txt"))
    );
  }

  /**
   * Uploading a file fails without permissions.
   *
   * @throws Exception On errors
   */

  @Test
  public void testFileUploadNotPermitted()
    throws Exception
  {
    this.login(new IdName("someone"));

    final var file = this.directory.resolve("data.txt");
    Files.writeString(file, "HELLO!");

    final var fileId = CAFileID.random();

    final var ex =
      assertThrows(CAClientException.class, () -> {
        this.client.fileUpload(
          fileId,
          file,
          "text/plain",
          "A text file.",
          statistics -> {

          }
        );
      });

    assertEquals(errorSecurityPolicyDenied(), ex.errorCode());
  }

  /**
   * Downloading a file fails without permissions.
   *
   * @throws Exception On errors
   */

  @Test
  public void testFileDownloadNotPermitted()
    throws Exception
  {
    this.login(new IdName("someone-admin"));

    final var file = this.directory.resolve("data.txt");
    final var fileTmp = this.directory.resolve("data.txt");
    Files.writeString(file, "HELLO!");

    final var fileId = CAFileID.random();
    this.client.fileUpload(
      fileId,
      file,
      "text/plain",
      "A text file.",
      statistics -> {

      }
    );

    this.login(new IdName("someone"));

    {
      final var ex =
        assertThrows(CAClientException.class, () -> {
          this.client.sendAndWaitOrThrow(
            new CAICommandFileGet(fileId),
            TIMEOUT);
        });

      assertEquals(errorSecurityPolicyDenied(), ex.errorCode());
    }

    {
      final var ex =
        assertThrows(CAClientException.class, () -> {
          this.client.fileDownload(
            fileId,
            file,
            fileTmp,
            6L,
            "SHA-256",
            "a2f6017f1fab81333a4288f68557b74495a27337c7d37b3eba46c866aa885098",
            statistics -> {

            }
          );
        });

      assertEquals(errorSecurityPolicyDenied(), ex.errorCode());
    }
  }

  /**
   * Assigning roles works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRolesAssign()
    throws Exception
  {
    this.login(new IdName("someone"));
    this.login(new IdName("someone-admin"));

    final var role =
      MRoleName.of("inventory.files.writer");

    {
      final var r =
        this.client.sendAndWaitOrThrow(new CAICommandRolesGet(USER), TIMEOUT);
      assertFalse(r.roles().contains(role));
    }

    this.client.sendAndWaitOrThrow(
      new CAICommandRolesAssign(USER, Set.of(role)), TIMEOUT);

    {
      final var r =
        this.client.sendAndWaitOrThrow(new CAICommandRolesGet(USER), TIMEOUT);
      assertTrue(r.roles().contains(role));
    }
  }

  /**
   * Revoking roles works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRolesRevoke()
    throws Exception
  {
    this.login(new IdName("someone"));
    this.login(new IdName("someone-admin"));

    final var role =
      MRoleName.of("inventory.files.writer");

    {
      final var r =
        this.client.sendAndWaitOrThrow(new CAICommandRolesGet(USER), TIMEOUT);
      assertFalse(r.roles().contains(role));
    }

    this.client.sendAndWaitOrThrow(
      new CAICommandRolesAssign(USER, Set.of(role)), TIMEOUT);

    {
      final var r =
        this.client.sendAndWaitOrThrow(new CAICommandRolesGet(USER), TIMEOUT);
      assertTrue(r.roles().contains(role));
    }

    this.client.sendAndWaitOrThrow(
      new CAICommandRolesRevoke(USER, Set.of(role)), TIMEOUT);

    {
      final var r =
        this.client.sendAndWaitOrThrow(new CAICommandRolesGet(USER), TIMEOUT);
      assertFalse(r.roles().contains(role));
    }
  }

  /**
   * Creating and modifying locations works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testLocationMetadataWorkflow()
    throws Exception
  {
    this.login(new IdName("someone-admin"));

    final var id = CALocationID.random();
    this.client.sendAndWaitOrThrow(new CAICommandLocationPut(
      new CALocation(
        id,
        Optional.empty(),
        CALocationPath.singleton("Location 0"),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      )
    ), TIMEOUT);

    final var meta0 =
      new CAMetadataType.Text(
        CATypeRecordFieldIdentifier.of("z:a.b0"), "x");
    final var meta1 =
      new CAMetadataType.Text(
        CATypeRecordFieldIdentifier.of("z:a.b1"), "y");
    final var meta2 =
      new CAMetadataType.Text(
        CATypeRecordFieldIdentifier.of("z:a.b2"), "z");

    this.client.sendAndWaitOrThrow(
      new CAICommandLocationMetadataPut(id, Set.of(meta0, meta1, meta2)),
      TIMEOUT
    );

    {
      final var r =
        this.client.sendAndWaitOrThrow(new CAICommandLocationGet(id), TIMEOUT);
      final var m = r.data().metadata();
      assertEquals(meta0, m.get(meta0.name()));
      assertEquals(meta1, m.get(meta1.name()));
      assertEquals(meta2, m.get(meta2.name()));
    }

    this.client.sendAndWaitOrThrow(
      new CAICommandLocationMetadataRemove(id, Set.of(meta1.name())),
      TIMEOUT
    );

    {
      final var r =
        this.client.sendAndWaitOrThrow(new CAICommandLocationGet(id), TIMEOUT);
      final var m = r.data().metadata();
      assertEquals(meta0, m.get(meta0.name()));
      assertNull(m.get(meta1.name()));
      assertEquals(meta2, m.get(meta2.name()));
    }
  }

  /**
   * Creating and modifying locations works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testLocationAttachmentWorkflow(
    final @TempDir Path directory)
    throws Exception
  {
    this.login(new IdName("someone-admin"));

    final var file =
      Files.writeString(
        directory.resolve("file.txt"),
        "Hello!",
        CREATE,
        TRUNCATE_EXISTING
      );

    final var fileId =
      CAFileID.random();

    this.client.fileUpload(
      fileId,
      file,
      "text/plain",
      "A file",
      stats -> {
      }
    );

    final var fileData =
      this.client.sendAndWaitOrThrow(new CAICommandFileGet(fileId), TIMEOUT)
        .data();

    final var id = CALocationID.random();
    this.client.sendAndWaitOrThrow(new CAICommandLocationPut(
      new CALocation(
        id,
        Optional.empty(),
        CALocationPath.singleton("Location 0"),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      )
    ), TIMEOUT);

    this.client.sendAndWaitOrThrow(
      new CAICommandLocationAttachmentAdd(id, fileId, "text"),
      TIMEOUT
    );

    {
      final var r =
        this.client.sendAndWaitOrThrow(new CAICommandLocationGet(id), TIMEOUT);
      final var m = r.data().attachments();
      final var attach0 = new CAAttachment(fileData, "text");
      assertEquals(attach0, m.get(attach0.key()));
    }

    this.client.sendAndWaitOrThrow(
      new CAICommandLocationAttachmentRemove(id, fileId, "text"),
      TIMEOUT
    );

    {
      final var r =
        this.client.sendAndWaitOrThrow(new CAICommandLocationGet(id), TIMEOUT);
      final var m = r.data().attachments();
      assertEquals(0, m.size());
    }
  }

  /**
   * Creating and modifying locations works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypePackageWorkflow(
    final @TempDir Path directory)
    throws Exception
  {
    this.login(new IdName("someone-admin"));

    final var text =
      new String(
        CAICmdTypePackageInstallTest.class.getResourceAsStream(
            "/com/io7m/cardant/tests/tpack2.xml")
          .readAllBytes(),
        StandardCharsets.UTF_8
      );

    this.client.sendAndWaitOrThrow(
      new CAICommandTypePackageInstall(text), TIMEOUT);

    {
      final var r =
        this.client.sendAndWaitOrThrow(new CAICommandTypePackageGetText(
          new CATypePackageIdentifier(
            new RDottedName("com.io7m.example"),
            Version.of(1, 0, 0)
          )
        ), TIMEOUT);
    }

    {
      this.client.sendAndWaitOrThrow(new CAICommandTypePackageSearchBegin(
        new CATypePackageSearchParameters(
          new CAComparisonFuzzyType.Anything<>(),
          100L
        )
      ), TIMEOUT);
      this.client.sendAndWaitOrThrow(
        new CAICommandTypePackageSearchNext(), TIMEOUT);
      this.client.sendAndWaitOrThrow(
        new CAICommandTypePackageSearchPrevious(), TIMEOUT);
    }

    {
      final var r =
        this.client.sendAndWaitOrThrow(
          new CAICommandTypePackageUninstall(
            new CATypePackageUninstall(
              CATypePackageTypeRemovalBehavior.TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED,
              new CATypePackageIdentifier(
                new RDottedName("com.io7m.example"),
                Version.of(1, 0, 0)
              )
            )
          ), TIMEOUT
        );
    }
  }

  /**
   * Transactions are transactional.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTransactionWorkflow0(
    final @TempDir Path directory)
    throws Exception
  {
    this.login(new IdName("someone-admin"));

    final var id0 = CAItemID.random();
    final var id1 = CAItemID.random();
    final var id2 = CAItemID.random();
    final var id3 = CAItemID.random();
    final var id4 = CAItemID.random();
    final var id5 = CAItemID.random();

    /*
     * The first three items exist as the transaction completes.
     */

    this.client.transaction(
      List.of(
        new CAICommandItemCreate(id0, "Item 0"),
        new CAICommandItemCreate(id1, "Item 1"),
        new CAICommandItemCreate(id2, "Item 2")
      )
    );

    this.client.sendAndWaitOrThrow(new CAICommandItemGet(id0), TIMEOUT);
    this.client.sendAndWaitOrThrow(new CAICommandItemGet(id1), TIMEOUT);
    this.client.sendAndWaitOrThrow(new CAICommandItemGet(id2), TIMEOUT);

    /*
     * None of the other items exist.
     */

    assertEquals(
      errorNonexistent(),
      assertThrows(CAClientException.class, () -> {
        this.client.sendAndWaitOrThrow(new CAICommandItemGet(id3), TIMEOUT);
      }).errorCode()
    );

    assertEquals(
      errorNonexistent(),
      assertThrows(CAClientException.class, () -> {
        this.client.sendAndWaitOrThrow(new CAICommandItemGet(id4), TIMEOUT);
      }).errorCode()
    );

    assertEquals(
      errorNonexistent(),
      assertThrows(CAClientException.class, () -> {
        this.client.sendAndWaitOrThrow(new CAICommandItemGet(id5), TIMEOUT);
      }).errorCode()
    );

    /*
     * The next three items aren't created because the last command in
     * the transaction fails.
     */

    this.client.transaction(
      List.of(
        new CAICommandItemCreate(id3, "Item 3"),
        new CAICommandItemCreate(id4, "Item 4"),
        new CAICommandItemCreate(id5, "Item 5"),
        new CAICommandDebugInvalid()
      )
    );

    assertEquals(
      errorNonexistent(),
      assertThrows(CAClientException.class, () -> {
        this.client.sendAndWaitOrThrow(new CAICommandItemGet(id3), TIMEOUT);
      }).errorCode()
    );

    assertEquals(
      errorNonexistent(),
      assertThrows(CAClientException.class, () -> {
        this.client.sendAndWaitOrThrow(new CAICommandItemGet(id4), TIMEOUT);
      }).errorCode()
    );

    assertEquals(
      errorNonexistent(),
      assertThrows(CAClientException.class, () -> {
        this.client.sendAndWaitOrThrow(new CAICommandItemGet(id5), TIMEOUT);
      }).errorCode()
    );
  }

  private void login(
    final IdName userName)
    throws CAClientException, InterruptedException
  {
    this.client.connectOrThrow(
      new CAClientConnectionParameters(
        this.server.server()
          .inventoryAPI()
          .getHost(),
        this.server.server()
          .inventoryAPI()
          .getPort(),
        false,
        userName,
        "12345678",
        Map.of(),
        Duration.ofSeconds(30L),
        Duration.ofSeconds(30L)
      )
    );
  }
}
