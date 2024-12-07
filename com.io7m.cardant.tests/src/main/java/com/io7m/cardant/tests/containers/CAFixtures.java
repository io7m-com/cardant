/*
 * Copyright © 2024 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

import com.io7m.ervilla.api.EContainerPodType;
import com.io7m.ervilla.api.EContainerSupervisorType;
import com.io7m.ervilla.api.EPortAddressType;
import com.io7m.ervilla.api.EPortProtocol;
import com.io7m.ervilla.api.EPortPublish;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class CAFixtures
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAFixtures.class);

  private static final Path BASE_DIRECTORY;
  private static final List<EPortPublish> PUBLICATION_PORTS;
  private static EContainerPodType POD;
  private static CAServerFixture SERVER_FIXTURE;
  private static CADatabaseFixture DATABASE_FIXTURE;
  private static CAIdstoreFixture IDSTORE_FIXTURE;
  private static CAPostgresFixture POSTGRES_FIXTURE;

  static {
    try {
      BASE_DIRECTORY = Files.createTempDirectory("northpike");
    } catch (final IOException e) {
      throw new IllegalStateException(e);
    }

    PUBLICATION_PORTS =
      List.of(
        new EPortPublish(
          new EPortAddressType.All(),
          postgresPort(),
          postgresPort(),
          EPortProtocol.TCP
        ),
        new EPortPublish(
          new EPortAddressType.All(),
          idstoreAdminPort(),
          idstoreAdminPort(),
          EPortProtocol.TCP
        ),
        new EPortPublish(
          new EPortAddressType.All(),
          idstoreUserPort(),
          idstoreUserPort(),
          EPortProtocol.TCP
        ),
        new EPortPublish(
          new EPortAddressType.All(),
          idstoreUserViewPort(),
          idstoreUserViewPort(),
          EPortProtocol.TCP
        )
      );
  }

  private CAFixtures()
  {

  }

  public static int inventoryServicePort()
  {
    return 30000;
  }

  public static int postgresPort()
  {
    return 5432;
  }

  public static int idstoreAdminPort()
  {
    return 50000;
  }

  public static int idstoreUserPort()
  {
    return 50001;
  }

  public static int idstoreUserViewPort()
  {
    return 50002;
  }

  public static EContainerPodType pod(
    final EContainerSupervisorType supervisor)
    throws Exception
  {
    if (POD == null) {
      POD = supervisor.createPod(PUBLICATION_PORTS);
    } else {
      LOG.info("Reusing pod {}.", POD.name());
    }
    return POD;
  }

  public static CADatabaseFixture database(
    final EContainerPodType containerFactory)
    throws Exception
  {
    if (DATABASE_FIXTURE == null) {
      DATABASE_FIXTURE =
        CADatabaseFixture.create(postgres(containerFactory));
    } else {
      LOG.info("Reusing cardant database fixture.");
    }
    return DATABASE_FIXTURE;
  }

  public static CAPostgresFixture postgres(
    final EContainerPodType containerFactory)
    throws Exception
  {
    if (POSTGRES_FIXTURE == null) {
      POSTGRES_FIXTURE =
        CAPostgresFixture.create(containerFactory, postgresPort());
    } else {
      LOG.info("Reusing postgres fixture.");
    }
    return POSTGRES_FIXTURE;
  }

  public static CAIdstoreFixture idstore(
    final EContainerPodType containerFactory)
    throws Exception
  {
    if (IDSTORE_FIXTURE == null) {
      IDSTORE_FIXTURE =
        CAIdstoreFixture.create(
          containerFactory,
          postgres(containerFactory),
          BASE_DIRECTORY,
          idstoreAdminPort(),
          idstoreUserPort(),
          idstoreUserViewPort()
        );
    } else {
      LOG.info("Reusing idstore fixture.");
    }
    return IDSTORE_FIXTURE;
  }

  public static CAServerFixture server(
    final EContainerPodType containerFactory)
    throws Exception
  {
    if (SERVER_FIXTURE == null) {
      SERVER_FIXTURE =
        CAServerFixture.create(
          idstore(containerFactory),
          database(containerFactory)
        );
    } else {
      LOG.info("Reusing cardant server fixture.");
    }
    return SERVER_FIXTURE;
  }
}
