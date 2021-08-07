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

import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.model.CAUsers;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class CAUsersTest
{
  private static Stream<String> randomPasswords()
    throws NoSuchAlgorithmException
  {
    final var instanceStrong =
      SecureRandom.getInstanceStrong();

    final var builder = new ArrayList<String>();
    for (int index = 0; index < 100; ++index) {
      final var data = new byte[16];
      instanceStrong.nextBytes(data);
      final var newPassword = HexFormat.of().formatHex(data);
      builder.add(newPassword);
    }
    return builder.stream();
  }

  private static void checkMatches(
    final String password)
    throws GeneralSecurityException
  {
    final var user =
      CAUsers.createUser(
        SecureRandom.getInstanceStrong(),
        CAUserID.random(),
        "user",
        password
      );

    assertTrue(CAUsers.checkUserPassword(user, password));
  }

  private static void checkDoesNotMatch(
    final String password)
    throws GeneralSecurityException
  {
    final var instanceStrong =
      SecureRandom.getInstanceStrong();

    final var data = new byte[16];
    instanceStrong.nextBytes(data);

    final var newPassword =
      HexFormat.of().formatHex(data);

    final var user =
      CAUsers.createUser(
        instanceStrong,
        CAUserID.random(),
        "user",
        password
      );

    assertFalse(CAUsers.checkUserPassword(user, newPassword));
  }

  private static DynamicTest testMatches(
    final String password)
  {
    return DynamicTest.dynamicTest("testMatches_" + password, () -> {
      checkMatches(password);
    });
  }

  private static DynamicTest testDoesNotMatch(final String password)
  {
    return DynamicTest.dynamicTest("testDoesNotMatch_" + password, () -> {
      checkDoesNotMatch(password);
    });
  }

  @TestFactory
  public Stream<DynamicTest> testUserCheckPasswordMatches()
    throws Exception
  {
    return randomPasswords()
      .map(CAUsersTest::testMatches);
  }

  @TestFactory
  public Stream<DynamicTest> testUserCheckPasswordDoesNotMatch()
    throws Exception
  {
    return randomPasswords()
      .map(CAUsersTest::testDoesNotMatch);
  }
}
