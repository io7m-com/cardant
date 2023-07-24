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

package com.io7m.cardant.database.api;

import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileSearchParameters;
import com.io7m.cardant.model.CAFileType;

import java.util.Optional;

/**
 * Model database queries (Files).
 */

public sealed interface CADatabaseQueriesFilesType
  extends CADatabaseQueriesType
{
  /**
   * Create or update the given file.
   */

  non-sealed interface PutType
    extends CADatabaseQueryType<CAFileType, CADatabaseUnit>,
    CADatabaseQueriesFilesType
  {

  }

  /**
   * Get the given file.
   */

  non-sealed interface GetType
    extends CADatabaseQueryType<GetType.Parameters, Optional<CAFileType>>,
    CADatabaseQueriesFilesType
  {
    /**
     * Parameters for retrieving a file.
     *
     * @param file     The file ID
     * @param withData {@code true} if file data should be included
     */

    record Parameters(
      CAFileID file,
      boolean withData)
    {

    }
  }

  /**
   * Remove the given file.
   */

  non-sealed interface RemoveType
    extends CADatabaseQueryType<CAFileID, CADatabaseUnit>,
    CADatabaseQueriesFilesType
  {

  }

  /**
   * Search for files.
   */

  non-sealed interface SearchType
    extends CADatabaseQueryType<CAFileSearchParameters, CADatabaseFileSearchType>,
    CADatabaseQueriesFilesType
  {

  }
}
