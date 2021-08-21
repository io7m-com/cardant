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

package com.io7m.cardant.client.preferences.vanilla.internal;

import com.io7m.cardant.client.preferences.api.CAPreferenceServerUsernamePassword;
import com.io7m.cardant.client.preferences.api.CAPreferences;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CAPreferencesStorer
{
  private final OutputStream stream;
  private final CAPreferences preferences;
  private Properties properties;

  public CAPreferencesStorer(
    final OutputStream inStream,
    final CAPreferences inPreferences)
  {
    this.stream =
      Objects.requireNonNull(inStream, "stream");
    this.preferences =
      Objects.requireNonNull(inPreferences, "preferences");
  }

  public void store()
    throws IOException
  {
    this.properties = new Properties();
    this.storeServerBookmarks();
    this.properties.storeToXML(this.stream, "", UTF_8);
  }

  private void storeServerBookmarks()
  {
    final var bookmarks =
      this.preferences.serverBookmarks();

    this.properties.put(
      "server.bookmarks",
      Integer.toUnsignedString(bookmarks.size())
    );

    for (int index = 0; index < bookmarks.size(); ++index) {
      final var bookmark = bookmarks.get(index);
      final var i = Integer.valueOf(index);

      this.properties.put(
        String.format("server.bookmarks.%s.name", i),
        bookmark.name()
      );
      this.properties.put(
        String.format("server.bookmarks.%s.host", i),
        bookmark.host()
      );
      this.properties.put(
        String.format("server.bookmarks.%s.port", i),
        Integer.toUnsignedString(bookmark.port())
      );
      this.properties.put(
        String.format("server.bookmarks.%s.https", i),
        Boolean.toString(bookmark.isHTTPs())
      );

      final var credentials = bookmark.credentials();
      if (credentials instanceof CAPreferenceServerUsernamePassword usernamePassword) {
        this.properties.put(
          String.format("server.bookmarks.%s.credentials.type", i),
          "usernamePassword"
        );
        this.properties.put(
          String.format("server.bookmarks.%s.credentials.username", i),
          usernamePassword.username()
        );
        this.properties.put(
          String.format("server.bookmarks.%s.credentials.password", i),
          usernamePassword.password()
        );
      }
    }
  }
}
