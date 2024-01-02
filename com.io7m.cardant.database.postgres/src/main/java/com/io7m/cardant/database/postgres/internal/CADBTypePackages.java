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


package com.io7m.cardant.database.postgres.internal;

import com.io7m.anethum.api.SerializationException;
import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.model.type_package.CATypePackage;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.type_packages.parser.api.CATypePackageSerializerFactoryType;
import com.io7m.verona.core.Version;
import org.jooq.Condition;
import org.jooq.impl.DSL;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_PACKAGES;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorIo;

final class CADBTypePackages
{
  private CADBTypePackages()
  {

  }

  public static Condition packageMatch(
    final CATypePackageIdentifier packageIdentifier)
  {
    final var packageName =
      packageIdentifier.name();
    final var packageVersion =
      packageIdentifier.version();

    return DSL.and(
        METADATA_TYPE_PACKAGES.MTP_NAME
          .eq(packageName.value()),
        METADATA_TYPE_PACKAGES.MTP_VERSION_MAJOR
          .eq(Integer.valueOf(packageVersion.major())),
        METADATA_TYPE_PACKAGES.MTP_VERSION_MINOR
          .eq(Integer.valueOf(packageVersion.minor())),
        METADATA_TYPE_PACKAGES.MTP_VERSION_PATCH
          .eq(Integer.valueOf(packageVersion.patch())),
        packageQualifierMatch(packageVersion)
    );
  }

  private static Condition packageQualifierMatch(
    final Version packageVersion)
  {
    return packageVersion.qualifier()
      .map(x -> METADATA_TYPE_PACKAGES.MTP_VERSION_QUALIFIER.eq(x.text()))
      .orElseGet(METADATA_TYPE_PACKAGES.MTP_VERSION_QUALIFIER::isNull);
  }

  public static String serialize(
    final CATypePackageSerializerFactoryType typePackageSerializers,
    final CATypePackage typePackage,
    final Map<String, String> attributes)
    throws CADatabaseException
  {
    try (var outputStream = new ByteArrayOutputStream()) {
      typePackageSerializers.serialize(
        URI.create("urn:stdout"),
        outputStream,
        typePackage
      );
      return outputStream.toString(StandardCharsets.UTF_8);
    } catch (final IOException | SerializationException e) {
      throw new CADatabaseException(
        Objects.requireNonNullElse(
          e.getMessage(),
          e.getClass().getSimpleName()),
        e,
        errorIo(),
        attributes,
        Optional.empty()
      );
    }
  }
}
