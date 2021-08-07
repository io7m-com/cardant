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

import com.io7m.cardant.cmdline.Main;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class CACommandLineTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CACommandLineTest.class);

  private PrintStream initialStdout;
  private PrintStream initialStderr;
  private ByteArrayOutputStream captureStdoutStream;
  private ByteArrayOutputStream captureStderrStream;
  private PrintStream captureStdout;
  private PrintStream captureStderr;
  private Path directory;

  @BeforeEach
  public void setup()
    throws IOException
  {
    this.directory = CATestDirectories.createTempDirectory();

    LOG.debug("capturing i/o");

    this.initialStdout = System.out;
    this.initialStderr = System.err;

    this.captureStdoutStream =
      new ByteArrayOutputStream();
    this.captureStderrStream =
      new ByteArrayOutputStream();
    this.captureStdout =
      new PrintStream(this.captureStdoutStream, true, UTF_8);
    this.captureStderr =
      new PrintStream(this.captureStderrStream, true, UTF_8);

    System.setOut(this.captureStdout);
    System.setErr(this.captureStderr);
  }

  @AfterEach
  public void tearDown()
  {
    System.out.flush();
    System.err.flush();

    System.setOut(this.initialStdout);
    System.setErr(this.initialStderr);

    LOG.debug("{}", this.captureStdoutStream.toString(UTF_8));
    LOG.debug("{}", this.captureStderrStream.toString(UTF_8));
  }

  @Test
  public void testNoArguments()
  {
    final var result = Main.mainExitless(new String[]{

    });
    assertEquals(1, result);
  }

  @Test
  public void testVersion()
  {
    final var result = Main.mainExitless(new String[]{
      "version"
    });
    assertEquals(0, result);

    final var stdout = this.captureStdoutStream.toString(UTF_8);
    assertTrue(stdout.contains("cardant "));
  }

  @Test
  public void testHelpHelp()
  {
    final var result = Main.mainExitless(new String[]{
      "help",
      "help"
    });
    assertEquals(0, result);
  }

  @Test
  public void testHelpVersion()
  {
    final var result = Main.mainExitless(new String[]{
      "help",
      "version"
    });
    assertEquals(0, result);
  }

  @Test
  public void testHelpServer()
  {
    final var result = Main.mainExitless(new String[]{
      "help",
      "server"
    });
    assertEquals(0, result);
  }
}
