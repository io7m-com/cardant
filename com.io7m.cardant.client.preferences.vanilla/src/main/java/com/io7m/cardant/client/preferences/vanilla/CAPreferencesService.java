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

package com.io7m.cardant.client.preferences.vanilla;

import com.io7m.cardant.client.preferences.api.CAPreferences;
import com.io7m.cardant.client.preferences.api.CAPreferencesServiceType;
import com.io7m.cardant.client.preferences.vanilla.internal.CAPreferencesLoader;
import com.io7m.cardant.client.preferences.vanilla.internal.CAPreferencesStorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * The default preferences service.
 */

public final class CAPreferencesService implements CAPreferencesServiceType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAPreferencesService.class);

  private final Path file;
  private volatile CAPreferences preferences;

  private CAPreferencesService(
    final Path inFile,
    final CAPreferences inPreferences)
  {
    this.file =
      Objects.requireNonNull(inFile, "file");
    this.preferences =
      Objects.requireNonNull(inPreferences, "preferences");
  }

  /**
   * Open preferences or return the default preferences values.
   *
   * @param file The preferences file
   *
   * @return A preferences service
   *
   * @throws IOException On I/O errors
   */

  public static CAPreferencesServiceType openOrDefault(
    final Path file)
    throws IOException
  {
    final var properties = new Properties();
    try (var stream = Files.newInputStream(file)) {
      properties.loadFromXML(stream);
    } catch (final NoSuchFileException e) {
      LOG.info("preferences file {} does not exist, creating a new one", file);
    }

    return new CAPreferencesService(
      file,
      new CAPreferencesLoader(file.getFileSystem(), properties).load()
    );
  }

  @Override
  public CAPreferences preferences()
  {
    return this.preferences;
  }

  @Override
  public void save(final CAPreferences newPreferences)
    throws IOException
  {
    this.preferences =
      Objects.requireNonNull(newPreferences, "newPreferences");

    final var parent = this.file.getParent();
    Files.createDirectories(parent);

    final var tmp =
      this.file.resolveSibling(String.format("%s.xml", UUID.randomUUID()));

    try (var stream = Files.newOutputStream(tmp)) {
      new CAPreferencesStorer(stream, this.preferences).store();
    } catch (final Exception e) {
      Files.deleteIfExists(tmp);
      throw e;
    }

    Files.move(tmp, this.file, ATOMIC_MOVE, REPLACE_EXISTING);
  }

  @Override
  public String toString()
  {
    return String.format(
      "[CAPreferencesService 0x%08x]",
      Integer.valueOf(this.hashCode()));
  }
}
