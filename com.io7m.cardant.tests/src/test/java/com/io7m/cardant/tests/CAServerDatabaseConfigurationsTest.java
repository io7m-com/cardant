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

import com.io7m.anethum.common.ParseException;
import com.io7m.cardant.server.CAServerConfigurations;
import com.io7m.cardant.server.api.CAServerDatabaseLocalConfiguration;
import com.io7m.cardant.server.api.CAServerDatabaseRemoteConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class CAServerDatabaseConfigurationsTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAServerDatabaseConfigurationsTest.class);

  private Path directory;
  private CAServerConfigurations configurations;

  @BeforeEach
  public void testSetup()
    throws IOException
  {
    this.directory =
      CATestDirectories.createTempDirectory();
    this.configurations =
      new CAServerConfigurations();
  }

  @AfterEach
  public void testTearDown()
    throws IOException
  {
    CATestDirectories.deleteDirectory(this.directory);
  }

  @Test
  public void testEmpty0()
    throws IOException
  {
    final var ex = assertThrows(ParseException.class, () -> {
      this.configurations.parseFileWithContext(
        FileSystems.getDefault(),
        this.resource("empty0.xml"));
    });
    LOG.debug("", ex);
  }

  @Test
  public void testEmpty1()
    throws IOException
  {
    final var ex = assertThrows(ParseException.class, () -> {
      this.configurations.parseFileWithContext(
        FileSystems.getDefault(),
        this.resource("empty1.xml"));
    });
    LOG.debug("", ex);
  }

  @Test
  public void testLocal0()
    throws IOException, ParseException
  {
    final var configuration =
      this.configurations.parseFileWithContext(
        FileSystems.getDefault(),
        this.resource("local0.xml"));

    assertEquals(
      CAServerDatabaseLocalConfiguration.class,
      configuration.database().getClass()
    );

    final var database =
      (CAServerDatabaseLocalConfiguration) configuration.database();
    assertEquals(Paths.get("/tmp/example"), database.file());
  }

  @Test
  public void testRemote0()
    throws IOException, ParseException
  {
    final var configuration =
      this.configurations.parseFileWithContext(
        FileSystems.getDefault(),
        this.resource("remote0.xml"));

    assertEquals(
      CAServerDatabaseRemoteConfiguration.class,
      configuration.database().getClass()
    );

    final var database =
      (CAServerDatabaseRemoteConfiguration) configuration.database();
    assertEquals("database.example.com", database.host());
    assertEquals(9999, database.port());
  }

  @Test
  public void testRemote0XML()
    throws IOException, ParseException
  {
    final var configuration =
      this.configurations.parseFileWithContext(
        FileSystems.getDefault(),
        this.resource("remote0.xml"));

    assertEquals(
      CAServerDatabaseRemoteConfiguration.class,
      configuration.database().getClass()
    );

    final var database =
      (CAServerDatabaseRemoteConfiguration) configuration.database();
    assertEquals("database.example.com", database.host());
    assertEquals(9999, database.port());
  }

  @Test
  public void testRemote1()
    throws IOException
  {
    final var ex =
      assertThrows(ParseException.class, () -> {
        this.configurations.parseFileWithContext(
          FileSystems.getDefault(),
          this.resource("remote1_bad.xml"));
      });
    LOG.debug("", ex);
  }

  private Path resource(
    final String name)
    throws IOException
  {
    return CATestDirectories.resourceOf(
      CAServerDatabaseConfigurationsTest.class,
      this.directory,
      name
    );
  }
}
