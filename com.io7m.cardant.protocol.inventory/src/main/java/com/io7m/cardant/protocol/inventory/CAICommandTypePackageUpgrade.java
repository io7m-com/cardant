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

package com.io7m.cardant.protocol.inventory;


import com.io7m.cardant.model.type_package.CATypePackageTypeRemovalBehavior;
import com.io7m.cardant.model.type_package.CATypePackageVersionBehavior;

import java.util.Objects;

/**
 * Upgrade/downgrade a type package.
 *
 * @param versionBehavior     The version behavior
 * @param typeRemovalBehavior The type removal behavior
 * @param text                The text
 */

public record CAICommandTypePackageUpgrade(
  CATypePackageTypeRemovalBehavior typeRemovalBehavior,
  CATypePackageVersionBehavior versionBehavior,
  String text)
  implements CAICommandType<CAIResponseTypePackageUpgrade>
{
  /**
   * Upgrade/downgrade a type package.
   *
   * @param versionBehavior     The version behavior
   * @param typeRemovalBehavior The type removal behavior
   * @param text                The text
   */

  public CAICommandTypePackageUpgrade
  {
    Objects.requireNonNull(typeRemovalBehavior, "typeRemovalBehavior");
    Objects.requireNonNull(versionBehavior, "versionBehavior");
    Objects.requireNonNull(text, "text");
  }

  @Override
  public Class<CAIResponseTypePackageUpgrade> responseClass()
  {
    return CAIResponseTypePackageUpgrade.class;
  }
}
