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

package com.io7m.cardant.server.controller.inventory;

import com.io7m.medrina.api.MActionName;
import com.io7m.medrina.api.MObject;
import com.io7m.medrina.api.MTypeName;

import java.util.Map;

/**
 * The security policy objects.
 */

public final class CAISecurityObjects
{
  /**
   * The tags section of the inventory.
   */

  public static final MObject INVENTORY_TAGS =
    new MObject(new MTypeName("inventory.tags"), Map.of());

  /**
   * The items section of the inventory.
   */

  public static final MObject INVENTORY_ITEMS =
    new MObject(new MTypeName("inventory.items"), Map.of());

  /**
   * The files section of the inventory.
   */

  public static final MObject INVENTORY_FILES =
    new MObject(new MTypeName("inventory.files"), Map.of());

  /**
   * The locations section of the inventory.
   */

  public static final MObject INVENTORY_LOCATIONS =
    new MObject(new MTypeName("inventory.locations"), Map.of());

  /**
   * A "read" action.
   */

  public static final MActionName READ =
    new MActionName("read");

  /**
   * A "write" action.
   */

  public static final MActionName WRITE =
    new MActionName("write");

  /**
   * A "delete" action.
   */

  public static final MActionName DELETE =
    new MActionName("delete");

  private CAISecurityObjects()
  {

  }
}
