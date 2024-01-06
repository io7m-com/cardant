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


package com.io7m.cardant.model.type_package;

/**
 * The type removal behavior for package upgrades/uninstalls.
 */

public enum CATypePackageTypeRemovalBehavior
{
  /**
   * If upgrading or removing a package would remove a type, and one or more
   * items refer to that type, then fail the change.
   */

  TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED,

  /**
   * If upgrading or removing a package would remove a type, and one or more
   * items refer to that type, then revoke the type from those items.
   */

  TYPE_REMOVAL_REVOKE_TYPES
}
