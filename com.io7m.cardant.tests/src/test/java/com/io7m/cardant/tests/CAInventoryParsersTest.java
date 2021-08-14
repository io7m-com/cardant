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

import com.io7m.cardant.model.CAByteArray;
import com.io7m.cardant.model.CAItemAttachmentID;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItems;
import com.io7m.cardant.model.CATags;
import com.io7m.cardant.model.xml.CAInventoryParsers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class CAInventoryParsersTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAInventoryParsersTest.class);

  private Path directory;
  private CAInventoryParsers parsers;

  private static String sha256Of(
    final CAByteArray data)
    throws NoSuchAlgorithmException
  {
    final var digest =
      MessageDigest.getInstance("SHA-256");
    final var result =
      digest.digest(data.data());
    return HexFormat.of().formatHex(result);
  }

  @BeforeEach
  public void testSetup()
    throws IOException
  {
    this.directory =
      CATestDirectories.createTempDirectory();
    this.parsers =
      new CAInventoryParsers();
  }

  @AfterEach
  public void testTearDown()
    throws IOException
  {
    CATestDirectories.deleteDirectory(this.directory);
  }

  @Test
  public void testInventory0()
    throws Exception
  {
    final var element =
      this.parsers.parseFile(this.resource("inventory0.xml"));

    final var item = ((CAItems) element).items().iterator().next();
    assertEquals(
      CAItemID.of("fbebaa76-58a2-4e0b-8e61-a4dbcdc8e2bc"),
      item.id());
    assertEquals("item", item.name());
    assertEquals(123L, item.count());

    final var meta0 =
      item.metadata().get("Example");
    final var meta1 =
      item.metadata().get("Q");

    assertEquals("Example", meta0.name());
    assertTrue(meta0.value().startsWith("Was Phileas Fogg rich?"));
    assertEquals("Q", meta1.name());
    assertTrue(meta1.value().startsWith("The mansion in Saville Row"));

    final var iter = item.tags().iterator();

    {
      final var tag = iter.next();
      assertEquals("Current", tag.name());
      assertEquals(
        "9710ede2-f7f5-4751-a9e2-78d0e7796fd2",
        tag.id().id().toString());
    }

    {
      final var tag = iter.next();
      assertEquals("Automation", tag.name());
      assertEquals(
        "039fe875-ee52-4b83-8a8c-09497d9983af",
        tag.id().id().toString());
    }

    {
      final var tag = iter.next();
      assertEquals("Control", tag.name());
      assertEquals(
        "1f68ddf6-2584-4b3c-a212-5ecf37bb7400",
        tag.id().id().toString());
    }

    assertEquals(3, item.tags().size());

    final var attach0 = item.attachments().get(CAItemAttachmentID.of(
      "94b649f5-88f1-4c81-baaf-0036df9f2bab"));
    assertEquals(
      "94b649f5-88f1-4c81-baaf-0036df9f2bab",
      attach0.id().id().toString());
    assertEquals("text/plain", attach0.mediaType());
    assertEquals("info", attach0.relation());
    assertEquals(505L, attach0.size());
    assertEquals("SHA-256", attach0.hashAlgorithm());
    assertEquals(
      "5891b5b522d5df086d0ff0b110fbd9d21bb4fc7163af34d08286a2e846f6be03",
      attach0.hashValue());
    assertEquals(
      "430467260bf734ff05ed3f44c06c285a4b3d902161d4b03e4620c020c225feab",
      sha256Of(attach0.data().get()));
  }

  @Test
  public void testInventory1()
    throws Exception
  {
    final CATags tags =
      (CATags) this.parsers.parseFile(this.resource("inventory1.xml"));

    final var iter = tags.tags().iterator();

    {
      final var tag = iter.next();
      assertEquals("Current", tag.name());
      assertEquals("9710ede2-f7f5-4751-a9e2-78d0e7796fd2", tag.id().id().toString());
    }

    {
      final var tag = iter.next();
      assertEquals("Automation", tag.name());
      assertEquals("039fe875-ee52-4b83-8a8c-09497d9983af", tag.id().id().toString());
    }

    {
      final var tag = iter.next();
      assertEquals("Control", tag.name());
      assertEquals("1f68ddf6-2584-4b3c-a212-5ecf37bb7400", tag.id().id().toString());
    }

    assertEquals(3, tags.tags().size());
  }

  private Path resource(
    final String name)
    throws IOException
  {
    return CATestDirectories.resourceOf(
      CAInventoryParsersTest.class,
      this.directory,
      name
    );
  }
}
