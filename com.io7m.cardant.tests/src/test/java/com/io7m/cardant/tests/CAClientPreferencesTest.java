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

import com.io7m.cardant.client.preferences.api.CAPreferenceServerBookmark;
import com.io7m.cardant.client.preferences.api.CAPreferenceServerUsernamePassword;
import com.io7m.cardant.client.preferences.api.CAPreferences;
import com.io7m.cardant.client.preferences.api.CAPreferencesDebuggingEnabled;
import com.io7m.cardant.client.preferences.vanilla.CAPreferencesService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class CAClientPreferencesTest
{
  private Path directory;

  @BeforeEach
  public void setup()
    throws Exception
  {
    this.directory =
      CATestDirectories.createTempDirectory();
  }

  @AfterEach
  public void tearDown()
    throws IOException
  {
    CATestDirectories.deleteDirectory(this.directory);
  }

  @Test
  public void testPreferencesLoadStoreIdentity()
    throws IOException
  {
    final var service =
      CAPreferencesService.openOrDefault(
        this.directory.resolve("prefs.txt"));

    final var prefsInitial = service.preferences();
    service.save(prefsInitial);
    assertEquals(prefsInitial, service.preferences());
  }

  @Test
  public void testPreferencesLoadStoreChanged()
    throws IOException
  {
    final var service =
      CAPreferencesService.openOrDefault(
        this.directory.resolve("prefs.txt"));

    final var prefsNew =
      new CAPreferences(
        CAPreferencesDebuggingEnabled.DEBUGGING_ENABLED,
        List.of(
          new CAPreferenceServerBookmark(
            "name1", "host", 1000, true,
            new CAPreferenceServerUsernamePassword("user", "pass")
          ),
          new CAPreferenceServerBookmark(
            "name2", "host", 1000, true,
            new CAPreferenceServerUsernamePassword("user", "pass")
          ),
          new CAPreferenceServerBookmark(
            "name3", "host", 1000, true,
            new CAPreferenceServerUsernamePassword("user", "pass")
          )
        ),
        List.of(
          this.directory,
          this.directory.resolve("a"),
          this.directory.resolve("b")
        )
      );

    service.save(prefsNew);
    assertEquals(prefsNew, service.preferences());
  }

  @Test
  public void testPreferencesLoadStoreUpdate()
    throws IOException
  {
    final var service0 =
      CAPreferencesService.openOrDefault(
        this.directory.resolve("prefs.txt"));

    final var p =
      new CAPreferences(
        CAPreferencesDebuggingEnabled.DEBUGGING_ENABLED,
        List.of(
          new CAPreferenceServerBookmark(
            "name1", "host", 1000, true,
            new CAPreferenceServerUsernamePassword("user", "pass")
          ),
          new CAPreferenceServerBookmark(
            "name2", "host", 1000, true,
            new CAPreferenceServerUsernamePassword("user", "pass")
          ),
          new CAPreferenceServerBookmark(
            "name3", "host", 1000, true,
            new CAPreferenceServerUsernamePassword("user", "pass")
          )
        ),
        List.of(
          this.directory,
          this.directory.resolve("a"),
          this.directory.resolve("b")
        )
      );
    service0.update(q -> p);
    assertEquals(p, service0.preferences());

    final var service1 =
      CAPreferencesService.openOrDefault(
        this.directory.resolve("prefs.txt"));

    assertEquals(p, service1.preferences());
  }
}
