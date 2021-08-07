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

package com.io7m.cardant.model;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Objects;

/**
 * Functions over users.
 */

public final class CAUsers
{
  private CAUsers()
  {

  }

  /**
   * Create a new user.
   *
   * @param random   A strong random instance
   * @param userID   The user ID
   * @param name     The user name
   * @param password The password
   *
   * @return A new user
   *
   * @throws GeneralSecurityException On security exceptions
   */

  public static CAUser createUser(
    final SecureRandom random,
    final CAUserID userID,
    final String name,
    final String password)
    throws GeneralSecurityException
  {
    Objects.requireNonNull(random, "random");
    Objects.requireNonNull(userID, "userID");
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(password, "password");

    final var salt = new byte[32];
    random.nextBytes(salt);

    return createUser(userID, name, password, salt);
  }

  /**
   * Create a new user.
   *
   * @param userID   The user ID
   * @param name     The user name
   * @param password The password
   * @param salt     The random salt bytes
   *
   * @return A new user
   *
   * @throws GeneralSecurityException On security exceptions
   */

  public static CAUser createUser(
    final CAUserID userID,
    final String name,
    final String password,
    final byte[] salt)
    throws GeneralSecurityException
  {
    Objects.requireNonNull(userID, "userID");
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(password, "password");
    Objects.requireNonNull(salt, "salt");

    final var formatter =
      HexFormat.of();
    final var passwordSalt =
      formatter.formatHex(salt);
    final var keyFactory =
      SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    final var keySpec =
      new PBEKeySpec(password.toCharArray(), salt, 10000, 256);
    final var hash =
      keyFactory.generateSecret(keySpec).getEncoded();
    final var passwordHash =
      formatter.formatHex(hash);

    return new CAUser(
      userID,
      name,
      passwordHash,
      passwordSalt,
      "PBKDF2WithHmacSHA256:10000"
    );
  }

  /**
   * Check the user's password.
   *
   * @param user     The user
   * @param password The password
   *
   * @return {@code true} if the given password hashes to the stored user password
   *
   * @throws GeneralSecurityException On security exceptions
   */

  public static boolean checkUserPassword(
    final CAUser user,
    final String password)
    throws GeneralSecurityException
  {
    Objects.requireNonNull(user, "user");
    Objects.requireNonNull(password, "password");

    final var passwordAlgorithm = user.passwordAlgorithm();
    return switch (passwordAlgorithm) {
      case "PBKDF2WithHmacSHA256:10000" -> checkUserPasswordPBKDF2(
        user,
        password);
      default -> throw new GeneralSecurityException(
        "Unsupported password hashing method: %s".formatted(passwordAlgorithm));
    };
  }

  private static boolean checkUserPasswordPBKDF2(
    final CAUser user,
    final String password)
    throws GeneralSecurityException
  {
    final var formatter =
      HexFormat.of();
    final var expectedHash =
      formatter.parseHex(user.passwordHash());
    final var salt =
      formatter.parseHex(user.passwordSalt());

    final var keyFactory =
      SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    final var keySpec =
      new PBEKeySpec(password.toCharArray(), salt, 10000, 256);
    final var hash =
      keyFactory.generateSecret(keySpec).getEncoded();

    boolean ok = true;
    for (int index = 0; index < hash.length; ++index) {
      final var ei = (int) hash[index];
      final var hi = (int) expectedHash[index];
      ok = ei == hi && ok;
    }
    return ok;
  }
}
