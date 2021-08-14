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

import com.io7m.cardant.database.api.CADatabaseException;

import java.util.Map;
import java.util.Optional;

/**
 * Model database queries (Users).
 */

public interface CAModelCADatabaseQueriesUsersType
{
  /**
   * Create or update the given user.
   *
   * @param user The user
   *
   * @throws CADatabaseException On database errors
   */

  void userPut(CAUser user)
    throws CADatabaseException;

  /**
   * @param id The user id
   *
   * @return The user with the given ID
   *
   * @throws CADatabaseException On database errors
   */

  Optional<CAUser> userGet(CAUserID id)
    throws CADatabaseException;

  /**
   * @param name The user name
   *
   * @return The user with the given name
   *
   * @throws CADatabaseException On database errors
   */

  Optional<CAUser> userGetByName(String name)
    throws CADatabaseException;

  /**
   * @return The list of users in the database
   *
   * @throws CADatabaseException On database errors
   */

  Map<CAUserID, CAUser> userList()
    throws CADatabaseException;
}
