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
import com.io7m.cardant.client.api.CAClientCredentials;
import com.io7m.cardant.client.api.CAClientException;
import com.io7m.cardant.client.api.CAClientSynchronousType;
import com.io7m.cardant.client.basic.CAClients;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.model.CAAttachment;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.model.type_package.CATypePackageSearchParameters;
import com.io7m.cardant.model.type_package.CATypePackageUninstall;
import com.io7m.cardant.model.type_package.CATypePackageUninstallBehavior;
import com.io7m.cardant.protocol.inventory.CAICommandFileGet;
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
import com.io7m.cardant.tests.containers.CATestContainers;
import com.io7m.cardant.tests.server.controller.CAICmdTypePackageInstallTest;
import com.io7m.ervilla.api.EContainerSupervisorType;
import com.io7m.ervilla.test_extension.ErvillaCloseAfterClass;
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
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorApiMisuse;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorIo;
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
  private static CAUserID USER_ADMIN;
  private static CAUserID USER;
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

    USER_ADMIN =
      IDSTORE.createUser("someone-admin");
    USER =
      IDSTORE.createUser("someone");

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
    this.downloads =
      this.directory.resolve("downloads");

    this.clients =
      new CAClients();
    this.client =
      closeables.addPerTestResource(
        this.clients.openSynchronousClient(
          new CAClientConfiguration(Locale.ROOT, Clock.systemUTC())
        )
      );

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
  }

  private CAClients clients;
  private CAClientSynchronousType client;
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
    this.client.fileUploadOrThrow(
      fileId,
      file,
      "text/plain",
      "A text file.",
      statistics -> {

      }
    );

    final var response =
      (CAIResponseFileGet)
        this.client.executeOrElseThrow(new CAICommandFileGet(fileId));

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

    this.client.fileDownloadOrThrow(
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
        this.client.fileUploadOrThrow(
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
    this.client.fileUploadOrThrow(
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
          this.client.executeOrElseThrow(new CAICommandFileGet(fileId));
        });

      assertEquals(errorSecurityPolicyDenied(), ex.errorCode());
    }

    {
      final var ex =
        assertThrows(CAClientException.class, () -> {
          this.client.fileDownloadOrThrow(
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
   * Sending garbage results in errors.
   *
   * @throws Exception On errors
   */

  @Test
  public void testGarbage()
    throws Exception
  {
    this.login(new IdName("someone-admin"));

    final var ex =
      assertThrows(CAClientException.class, () -> {
        this.client.garbageOrElseThrow();
      });

    assertEquals(errorIo(), ex.errorCode());
  }

  /**
   * Sending invalid messages results in errors.
   *
   * @throws Exception On errors
   */

  @Test
  public void testInvalid()
    throws Exception
  {
    this.login(new IdName("someone-admin"));

    final var ex =
      assertThrows(CAClientException.class, () -> {
        this.client.invalidOrElseThrow();
      });

    assertEquals(errorApiMisuse(), ex.errorCode());
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
        this.client.executeOrElseThrow(new CAICommandRolesGet(USER));
      assertFalse(r.roles().contains(role));
    }

    this.client.executeOrElseThrow(
      new CAICommandRolesAssign(USER, Set.of(role)));

    {
      final var r =
        this.client.executeOrElseThrow(new CAICommandRolesGet(USER));
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
        this.client.executeOrElseThrow(new CAICommandRolesGet(USER));
      assertFalse(r.roles().contains(role));
    }

    this.client.executeOrElseThrow(
      new CAICommandRolesAssign(USER, Set.of(role)));

    {
      final var r =
        this.client.executeOrElseThrow(new CAICommandRolesGet(USER));
      assertTrue(r.roles().contains(role));
    }

    this.client.executeOrElseThrow(
      new CAICommandRolesRevoke(USER, Set.of(role)));

    {
      final var r =
        this.client.executeOrElseThrow(new CAICommandRolesGet(USER));
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
    this.client.executeOrElseThrow(new CAICommandLocationPut(
      new CALocation(
        id,
        Optional.empty(),
        "Location 0",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      )
    ));

    final var meta0 =
      new CAMetadataType.Text(new RDottedName("a.b0"), "x");
    final var meta1 =
      new CAMetadataType.Text(new RDottedName("a.b1"), "y");
    final var meta2 =
      new CAMetadataType.Text(new RDottedName("a.b2"), "z");

    this.client.executeOrElseThrow(
      new CAICommandLocationMetadataPut(id, Set.of(meta0, meta1, meta2))
    );

    {
      final var r =
        this.client.executeOrElseThrow(new CAICommandLocationGet(id));
      final var m = r.data().metadata();
      assertEquals(meta0, m.get(meta0.name()));
      assertEquals(meta1, m.get(meta1.name()));
      assertEquals(meta2, m.get(meta2.name()));
    }

    this.client.executeOrElseThrow(
      new CAICommandLocationMetadataRemove(id, Set.of(meta1.name()))
    );

    {
      final var r =
        this.client.executeOrElseThrow(new CAICommandLocationGet(id));
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
      this.client.fileUploadOrThrow(
        CAFileID.random(),
        file,
        "text/plain",
        "A file",
        stats -> {
        }
      );

    final var fileData =
      this.client.executeOrElseThrow(new CAICommandFileGet(fileId))
        .data();

    final var id = CALocationID.random();
    this.client.executeOrElseThrow(new CAICommandLocationPut(
      new CALocation(
        id,
        Optional.empty(),
        "Location 0",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      )
    ));

    this.client.executeOrElseThrow(
      new CAICommandLocationAttachmentAdd(id, fileId, "text")
    );

    {
      final var r =
        this.client.executeOrElseThrow(new CAICommandLocationGet(id));
      final var m = r.data().attachments();
      final var attach0 = new CAAttachment(fileData, "text");
      assertEquals(attach0, m.get(attach0.key()));
    }

    this.client.executeOrElseThrow(
      new CAICommandLocationAttachmentRemove(id, fileId, "text")
    );

    {
      final var r =
        this.client.executeOrElseThrow(new CAICommandLocationGet(id));
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

    this.client.executeOrElseThrow(new CAICommandTypePackageInstall(text));

    {
      final var r =
        this.client.executeOrElseThrow(new CAICommandTypePackageGetText(
          new CATypePackageIdentifier(
            new RDottedName("com.io7m.example"),
            Version.of(1,0,0)
          )
        ));
    }

    {
      this.client.executeOrElseThrow(new CAICommandTypePackageSearchBegin(
        new CATypePackageSearchParameters(
          new CAComparisonFuzzyType.Anything<>(),
          100L
        )
      ));
      this.client.executeOrElseThrow(new CAICommandTypePackageSearchNext());
      this.client.executeOrElseThrow(new CAICommandTypePackageSearchPrevious());
    }

    {
      final var r =
        this.client.executeOrElseThrow(
          new CAICommandTypePackageUninstall(
            new CATypePackageUninstall(
              CATypePackageUninstallBehavior.UNINSTALL_FAIL_IF_TYPES_REFERENCED,
              new CATypePackageIdentifier(
                new RDottedName("com.io7m.example"),
                Version.of(1,0,0)
              )
            )
          )
        );
    }
  }

  private void login(
    final IdName userName)
    throws CAClientException, InterruptedException
  {
    this.client.loginOrElseThrow(
      new CAClientCredentials(
        this.server.server()
          .inventoryAPI()
          .getHost(),
        this.server.server()
          .inventoryAPI()
          .getPort(),
        false,
        userName,
        "12345678",
        Map.of()
      ),
      CAClientException::ofError
    );
  }
}
