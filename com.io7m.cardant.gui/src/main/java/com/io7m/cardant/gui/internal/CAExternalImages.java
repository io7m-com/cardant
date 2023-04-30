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

package com.io7m.cardant.gui.internal;

import com.io7m.cardant.model.CAByteArray;
import com.io7m.repetoir.core.RPServiceType;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorIo;

public final class CAExternalImages implements RPServiceType
{
  private final CAMainStrings strings;

  public CAExternalImages(
    final CAMainStrings inStrings)
  {
    this.strings = Objects.requireNonNull(inStrings, "inStrings");
  }

  public CAImageData open(
    final Path file)
    throws CAImageDataException
  {
    Objects.requireNonNull(file, "file");

    final var hashAlgorithm = "SHA-256";

    try {
      final var digest =
        MessageDigest.getInstance(hashAlgorithm);

      final var byteStream = new ByteArrayOutputStream();
      try (var digestStream = new DigestOutputStream(byteStream, digest)) {
        try (var inputStream = Files.newInputStream(file)) {
          inputStream.transferTo(digestStream);
        }
        digestStream.flush();
      }

      final var mediaType =
        Files.probeContentType(file);

      final var image =
        new Image(new ByteArrayInputStream(byteStream.toByteArray()));

      if (image.isError()) {
        final var exception = image.exceptionProperty().get();
        throw new CAImageDataException(
          this.strings.format("file.notAnImage"),
          exception,
          errorIo(),
          Map.ofEntries(
            Map.entry(this.strings.format("file"), file.toString())
          ),
          Optional.empty()
        );
      }

      final var data = new CAByteArray(byteStream.toByteArray());
      return new CAImageData(
        data,
        Integer.toUnsignedLong(data.data().length),
        hashAlgorithm,
        HexFormat.of().formatHex(digest.digest()),
        mediaType
      );
    } catch (final NoSuchAlgorithmException | IOException e) {
      throw new CAImageDataException(
        e.getMessage(),
        e,
        errorIo(),
        Map.of(
          this.strings.format("item.attachment.hashAlgorithm"), hashAlgorithm,
          this.strings.format("file"), file.toString()
        ),
        Optional.empty()
      );
    }
  }

  @Override
  public String toString()
  {
    return String.format(
      "[CAExternalImages 0x%08x]",
      Integer.valueOf(this.hashCode())
    );
  }

  @Override
  public String description()
  {
    return "External image service";
  }
}
