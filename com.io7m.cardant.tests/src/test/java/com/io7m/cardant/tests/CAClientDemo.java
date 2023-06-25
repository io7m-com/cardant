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

import com.io7m.cardant.client.api.CAClientConfiguration;
import com.io7m.cardant.client.api.CAClientCredentials;
import com.io7m.cardant.client.api.CAClientException;
import com.io7m.cardant.client.basic.CAClients;
import com.io7m.cardant.model.CAFileID;
import com.io7m.idstore.model.IdName;

import java.nio.file.Paths;
import java.time.Clock;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class CAClientDemo
{
  public static void main(
    final String[] args)
    throws Exception
  {
    final var clients =
      new CAClients();
    final var config =
      new CAClientConfiguration(Locale.ROOT, Clock.systemUTC());

    try (var client = clients.openSynchronousClient(config)) {
      client.loginOrElseThrow(
        new CAClientCredentials(
          "localhost",
          9999,
          false,
          new IdName("rm"),
          "12345678",
          Map.of()
        ),
        CAClientException::ofError
      );

      client.fileUpload(
        new CAFileID(UUID.fromString("2d0d5eaf-172f-47ab-993c-b31aa541bc7d")),
        Paths.get("data.bin"),
        "application/octet-stream",
        System.out::println
      );
    }
  }
}
