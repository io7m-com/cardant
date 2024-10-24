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

package com.io7m.cardant.client.preferences.vanilla.internal;

import com.io7m.cardant.client.preferences.api.CAPreferenceServerUsernamePassword;
import com.io7m.cardant.client.preferences.api.CAPreferences;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * A storer of preferences.
 */

public final class CAPreferencesStorer
{
  private final OutputStream stream;
  private final CAPreferences preferences;
  private Properties properties;

  /**
   * A storer of preferences.
   *
   * @param inStream      The output stream
   * @param inPreferences The preferences
   */

  public CAPreferencesStorer(
    final OutputStream inStream,
    final CAPreferences inPreferences)
  {
    this.stream =
      Objects.requireNonNull(inStream, "stream");
    this.preferences =
      Objects.requireNonNull(inPreferences, "preferences");
  }

  /**
   * Store preferences.
   *
   * @throws IOException On I/O errors
   */

  public void store()
    throws IOException
  {
    this.properties = new Properties();
    this.storeDebugging();
    this.storeServerBookmarks();
    this.storeRecentFiles();
    this.properties.storeToXML(this.stream, "", UTF_8);
  }

  private void storeRecentFiles()
  {
    final var recentFiles = this.preferences.recentFiles();

    this.properties.setProperty(
      "recentFiles.count",
      Integer.toUnsignedString(recentFiles.size())
    );

    for (int index = 0; index < recentFiles.size(); ++index) {
      final var path = recentFiles.get(index);
      final var i = Integer.valueOf(index);
      this.properties.setProperty(
        String.format("recentFiles.%s", i),
        path.toAbsolutePath().toString()
      );
    }
  }

  private void storeDebugging()
  {
    this.properties.setProperty(
      "debugging",
      switch (this.preferences.debuggingEnabled()) {
        case DEBUGGING_DISABLED -> "false";
        case DEBUGGING_ENABLED -> "true";
      }
    );
  }

  private void storeServerBookmarks()
  {
    final var bookmarks =
      this.preferences.serverBookmarks();

    this.properties.setProperty(
      "server.bookmarks",
      Integer.toUnsignedString(bookmarks.size())
    );

    for (int index = 0; index < bookmarks.size(); ++index) {
      final var bookmark = bookmarks.get(index);
      final var i = Integer.valueOf(index);

      this.properties.setProperty(
        String.format("server.bookmarks.%s.name", i),
        bookmark.name()
      );
      this.properties.setProperty(
        String.format("server.bookmarks.%s.host", i),
        bookmark.host()
      );
      this.properties.setProperty(
        String.format("server.bookmarks.%s.port", i),
        Integer.toUnsignedString(bookmark.port())
      );
      this.properties.setProperty(
        String.format("server.bookmarks.%s.https", i),
        Boolean.toString(bookmark.isHTTPs())
      );
      this.properties.setProperty(
        String.format("server.bookmarks.%s.loginTimeout", i),
        bookmark.loginTimeout().toString()
      );
      this.properties.setProperty(
        String.format("server.bookmarks.%s.commandTimeout", i),
        bookmark.commandTimeout().toString()
      );

      final var credentials = bookmark.credentials();
      if (credentials instanceof CAPreferenceServerUsernamePassword usernamePassword) {
        this.properties.setProperty(
          String.format("server.bookmarks.%s.credentials.type", i),
          "usernamePassword"
        );
        this.properties.setProperty(
          String.format("server.bookmarks.%s.credentials.username", i),
          usernamePassword.username()
        );
        this.properties.setProperty(
          String.format("server.bookmarks.%s.credentials.password", i),
          usernamePassword.password()
        );
      }
    }
  }

  @Override
  public String toString()
  {
    return String.format(
      "[CAPreferencesStorer 0x%08x]",
      Integer.valueOf(this.hashCode()));
  }
}
