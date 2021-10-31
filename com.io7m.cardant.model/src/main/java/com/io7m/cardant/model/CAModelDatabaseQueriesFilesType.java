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

import java.util.Optional;

/**
 * Model database queries (Files).
 */

public interface CAModelDatabaseQueriesFilesType
{
  /**
   * Create or update the given file.
   *
   * @param file The file
   *
   * @throws CADatabaseException On database errors
   */

  void filePut(
    CAFileType file)
    throws CADatabaseException;

  /**
   * Get the given file.
   *
   * @param file     The file
   * @param withData {@code true} if the file data should be retrieved
   *
   * @return The file, if one exists
   *
   * @throws CADatabaseException On database errors
   */

  Optional<CAFileType> fileGet(
    CAFileID file,
    boolean withData)
    throws CADatabaseException;

  /**
   * Remove the given file.
   *
   * @param file The file
   *
   * @throws CADatabaseException On database errors
   */

  void fileRemove(
    CAFileID file)
    throws CADatabaseException;
}
