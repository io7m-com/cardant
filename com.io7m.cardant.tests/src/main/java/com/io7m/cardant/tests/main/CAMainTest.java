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

package com.io7m.cardant.tests.main;

import com.io7m.cardant.main.CAMain;
import com.io7m.cardant.model.CAVersion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class CAMainTest
{
  private PrintStream savedOutput;
  private ByteArrayOutputStream outBuffer;

  @BeforeEach
  public void setup()
  {
    this.savedOutput = System.out;
    this.outBuffer = new ByteArrayOutputStream();
    System.setOut(new PrintStream(this.outBuffer));
  }

  @AfterEach
  public void tearDown()
  {
    System.setOut(this.savedOutput);
    System.out.println(
      new String(this.outBuffer.toString(StandardCharsets.UTF_8))
    );
  }

  @TestFactory
  public Stream<DynamicTest> testHelp()
  {
    final var names =
      List.of(
        List.of("initialize"),
        List.of("server"),
        List.of("shell"),
        List.of("help"),
        List.of("package", "get"),
        List.of("package", "list")
      );

    return names.stream()
      .map(args -> {
        return DynamicTest.dynamicTest("help " + args, () -> {
          final var argArray = new String[1 + args.size()];
          argArray[0] = "help";
          for (int index = 0; index < args.size(); ++index) {
            argArray[index + 1] = args.get(index);
          }
          assertEquals(
            0,
            CAMain.mainExitless(argArray)
          );
        });
      });
  }

  @Test
  public void testPackageList()
  {
    final var r = CAMain.mainExitless(new String[]{
      "package",
      "list"
    });
    assertEquals(0, r);
  }

  @Test
  public void testPackageGet()
  {
    final var r = CAMain.mainExitless(new String[]{
      "package",
      "get",
      "--name",
      "cardant.product",
      "--version",
      CAVersion.MAIN_VERSION
    });
    assertEquals(0, r);
  }
}
