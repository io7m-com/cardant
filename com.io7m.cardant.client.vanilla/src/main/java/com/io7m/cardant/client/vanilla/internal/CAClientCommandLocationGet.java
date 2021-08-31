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

package com.io7m.cardant.client.vanilla.internal;

import com.io7m.cardant.client.api.CAClientCommandResultType;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;

import java.util.concurrent.CompletableFuture;

public record CAClientCommandLocationGet(
  CompletableFuture<CAClientCommandResultType<CALocation>> future,
  CALocationID id)
  implements CAClientCommandType<CALocation>
{
  @Override
  public Class<CALocation> returnType()
  {
    return CALocation.class;
  }
}
