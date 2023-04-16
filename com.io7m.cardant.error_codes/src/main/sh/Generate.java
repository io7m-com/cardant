import org.apache.commons.text.CaseUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static java.lang.System.out;

public final class Generate
{
  private static final Pattern ERROR_CODE_LINE =
    Pattern.compile("^(.*)\\|(.*)\\|(.*)$");

  private Generate()
  {

  }

  private record ErrorCode(
    String constantName,
    String externalName,
    String description)
  {

  }

  public static void main(
    final String[] args)
    throws IOException, ParseException
  {
    final var file =
      Paths.get(args[0])
        .toAbsolutePath();

    var lineNumber = 0;
    final var errorCodes = new ArrayList<ErrorCode>();
    try (var reader = Files.newBufferedReader(file)) {
      while (true) {
        final var line = reader.readLine();
        ++lineNumber;
        if (line == null) {
          break;
        }
        if (line.startsWith("#")) {
          continue;
        }
        final var trimmed = line.trim();
        if (trimmed.isBlank()) {
          continue;
        }

        final var matcher = ERROR_CODE_LINE.matcher(trimmed);
        if (!matcher.matches()) {
          throw new ParseException(
            "%d: Unparseable line '%s\""
              .formatted(Integer.valueOf(lineNumber), line),
            lineNumber
          );
        }

        final var code =
          new ErrorCode(
            matcher.group(1),
            matcher.group(2),
            matcher.group(3)
          );

        errorCodes.add(code);
      }
    }

    final var containerPackage =
      "com.io7m.cardant.error_codes";
    final var containerClass =
      "CAStandardErrorCodes";
    final var errorCodeClass =
      "CAErrorCode";
    final var errorCodePackage =
      "com.io7m.cardant.error_codes";

    out.printf("package %s;\n", containerPackage);
    out.printf("import %s.%s;\n", errorCodePackage, errorCodeClass);

    out.printf("/**\n");
    out.printf(" * <p>The standard error codes.</p>\n");
    out.printf(" * <p>Note: This file is generated from codes.txt and should not be hand-edited.</p>\n");
    out.printf(" */\n");

    out.printf("public final class %s {\n",containerClass);
    out.printf("  private %s () { }\n", containerClass);

    for (final var code : errorCodes) {
      out.printf(
        "private static final %s ERROR_%s =\n",
        errorCodeClass,
        code.constantName
      );
      out.printf(
        "  new %s (\"error-%s\");\n",
        errorCodeClass,
        code.externalName
      );

      final var methodName =
        "error" + CaseUtils.toCamelCase(
          code.constantName,
          true,
          '_'
        );

      out.printf("/**\n");
      out.printf(" * %s\n", code.description);
      out.printf(" * @return The error code\n");
      out.printf(" */\n");
      out.printf(
        "public static %s %s () {\n",
        errorCodeClass,
        methodName
      );
      out.printf(
        "  return ERROR_%s;\n",
        code.constantName
      );
      out.print(
        "}\n"
      );
    }
    out.println("}");
    out.println();
  }
}