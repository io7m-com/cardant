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

import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.binary.Hex;
import com.io7m.cardant.database.api.CADatabaseQueriesFilesType;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.model.CAByteArray;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.security.MessageDigest;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers(disabledWithoutDocker = true)
@ExtendWith(CADatabaseExtension.class)
public final class CADatabaseFilesTest
{
  /**
   * Creating files works.
   *
   * @param transaction The transaction
   *
   * @throws Exception On errors
   */

  @Test
  public void testFileCreate(
    final CADatabaseTransactionType transaction)
    throws Exception
  {
    final var q =
      transaction.queries(CADatabaseQueriesFilesType.class);

    final var digest =
      MessageDigest.getInstance("SHA-256");
    final var hash =
      digest.digest("HELLO!".getBytes(UTF_8));
    final var hashS =
      Hex.encodeHexString(hash);

    final var file =
      new CAFileType.CAFileWithData(
        CAFileID.random(),
        "File 0",
        "text/plain",
        6L,
        "SHA-256",
        hashS,
        new CAByteArray("HELLO!".getBytes(UTF_8))
      );

    q.filePut(file);

    final var fileWithout =
      file.withoutData();

    final var resultWithout =
      q.fileGet(file.id(), false)
        .orElseThrow();

    assertEquals(fileWithout, resultWithout);

    final var resultWith =
      q.fileGet(file.id(), true)
        .orElseThrow();

    assertEquals(file, resultWith);
  }

  /**
   * Deleting files works.
   *
   * @param transaction The transaction
   *
   * @throws Exception On errors
   */

  @Test
  public void testFileDelete(
    final CADatabaseTransactionType transaction)
    throws Exception
  {
    final var q =
      transaction.queries(CADatabaseQueriesFilesType.class);

    final var digest =
      MessageDigest.getInstance("SHA-256");
    final var hash =
      digest.digest("HELLO!".getBytes(UTF_8));
    final var hashS =
      Hex.encodeHexString(hash);

    final var file =
      new CAFileType.CAFileWithData(
        CAFileID.random(),
        "File 0",
        "text/plain",
        6L,
        "SHA-256",
        hashS,
        new CAByteArray("HELLO!".getBytes(UTF_8))
      );

    q.filePut(file);
    q.fileRemove(file.id());

    assertEquals(
      Optional.empty(),
      q.fileGet(file.id(), false)
    );
    assertEquals(
      Optional.empty(),
      q.fileGet(file.id(), true)
    );
  }
}
