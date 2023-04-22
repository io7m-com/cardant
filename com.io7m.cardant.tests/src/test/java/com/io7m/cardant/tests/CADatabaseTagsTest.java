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

import com.io7m.cardant.database.api.CADatabaseQueriesTagsType;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CATagID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers(disabledWithoutDocker = true)
@ExtendWith(CADatabaseExtension.class)
public final class CADatabaseTagsTest
{
  /**
   * The tag list is empty.
   *
   * @param transaction The transaction
   *
   * @throws Exception On errors
   */

  @Test
  public void testTagListEmpty(
    final CADatabaseTransactionType transaction)
    throws Exception
  {
    final var q =
      transaction.queries(CADatabaseQueriesTagsType.class);

    assertEquals(new TreeSet<>(), q.tagList());
  }

  /**
   * The tag list contains created tags.
   *
   * @param transaction The transaction
   *
   * @throws Exception On errors
   */

  @Test
  public void testTagListCreate(
    final CADatabaseTransactionType transaction)
    throws Exception
  {
    final var q =
      transaction.queries(CADatabaseQueriesTagsType.class);

    final var ta = new CATag(CATagID.random(), "a");
    q.tagPut(ta);
    final var tb = new CATag(CATagID.random(), "b");
    q.tagPut(tb);
    final var tc = new CATag(CATagID.random(), "c");
    q.tagPut(tc);

    final var expected = new TreeSet<CATag>();
    expected.add(ta);
    expected.add(tb);
    expected.add(tc);

    assertEquals(expected, q.tagList());
    assertEquals(ta, q.tagGet(ta.id()).orElseThrow());
    assertEquals(tb, q.tagGet(tb.id()).orElseThrow());
    assertEquals(tc, q.tagGet(tc.id()).orElseThrow());
  }

  /**
   * The tag list does not contain deleted tags.
   *
   * @param transaction The transaction
   *
   * @throws Exception On errors
   */

  @Test
  public void testTagListDelete(
    final CADatabaseTransactionType transaction)
    throws Exception
  {
    final var q =
      transaction.queries(CADatabaseQueriesTagsType.class);

    final var ta = new CATag(CATagID.random(), "a");
    q.tagPut(ta);
    final var tb = new CATag(CATagID.random(), "b");
    q.tagPut(tb);
    final var tc = new CATag(CATagID.random(), "c");
    q.tagPut(tc);

    final var expected = new TreeSet<CATag>();
    expected.add(ta);
    expected.add(tc);

    q.tagDelete(tb);

    assertEquals(expected, q.tagList());
  }

  /**
   * Updating tags works.
   *
   * @param transaction The transaction
   *
   * @throws Exception On errors
   */

  @Test
  public void testTagUpdate(
    final CADatabaseTransactionType transaction)
    throws Exception
  {
    final var q =
      transaction.queries(CADatabaseQueriesTagsType.class);

    final var id = CATagID.random();

    final var ta = new CATag(id, "a");
    q.tagPut(ta);
    final var tb = new CATag(id, "b");
    q.tagPut(tb);
    final var tc = new CATag(id, "c");
    q.tagPut(tc);

    final var expected = new TreeSet<CATag>();
    expected.add(tc);

    assertEquals(expected, q.tagList());
    assertEquals(tc, q.tagGet(id).orElseThrow());
  }
}
