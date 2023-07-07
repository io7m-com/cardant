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

import com.io7m.cardant.protocol.inventory.CAIResponseWithElementType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public final class CAGenArbitraries
{
  private static final String COPYRIGHT = """
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
    """;

  private CAGenArbitraries()
  {

  }

  public static void main(
    final String[] args)
    throws IOException
  {
    final var commandClasses =
      CAIResponseWithElementType.class.getPermittedSubclasses();

    for (final var clazz: commandClasses) {
      generate(clazz);
    }
  }

  private static void generate(final Class<?> clazz)
    throws IOException
  {
    final var origName =
      clazz.getSimpleName();
    final var className =
      origName.replace("CAI", "CAArb");

    final var file =
      Paths.get(className + ".java")
        .toAbsolutePath();

    try (var writer =
           Files.newBufferedWriter(file, UTF_8, TRUNCATE_EXISTING, CREATE)) {
      writer.write(COPYRIGHT.trim());
      writer.newLine();
      writer.write("package com.io7m.cardant.tests.arbitraries;");
      writer.newLine();
      writer.write("import %s;".formatted(clazz.getCanonicalName()));
      writer.write(
        "public final class %s extends CAArbAbstract<%s> {"
          .formatted(className, origName)
      );
      writer.write("public %s() {".formatted(className));
      writer.newLine();
      writer.write("super(");
      writer.newLine();
      writer.write("%s.class,".formatted(origName));
      writer.newLine();
      writer.write("() -> {");
      writer.newLine();
      writer.newLine();
      writer.write("}");
      writer.newLine();
      writer.write(");");
      writer.newLine();
      writer.write("}");
      writer.newLine();
      writer.write("}");
      writer.newLine();
    }
  }
}
