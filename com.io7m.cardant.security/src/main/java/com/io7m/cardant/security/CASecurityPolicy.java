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

package com.io7m.cardant.security;

import com.io7m.anethum.common.ParseException;
import com.io7m.anethum.common.ParseStatus;
import com.io7m.medrina.api.MActionName;
import com.io7m.medrina.api.MObject;
import com.io7m.medrina.api.MPolicy;
import com.io7m.medrina.api.MRoleName;
import com.io7m.medrina.api.MTypeName;
import com.io7m.medrina.vanilla.MPolicyParsers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Set;

/**
 * The security policy objects.
 */

public final class CASecurityPolicy
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CASecurityPolicy.class);

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

  /**
   * A writer of inventory files.
   */

  public static final MRoleName ROLE_INVENTORY_FILES_WRITER =
    new MRoleName("inventory.files.writer");

  /**
   * A reader of inventory files.
   */

  public static final MRoleName ROLE_INVENTORY_FILES_READER =
    new MRoleName("inventory.files.reader");

  /**
   * A writer of inventory items.
   */

  public static final MRoleName ROLE_INVENTORY_ITEMS_WRITER =
    new MRoleName("inventory.items.writer");

  /**
   * A reader of inventory items.
   */

  public static final MRoleName ROLE_INVENTORY_ITEMS_READER =
    new MRoleName("inventory.items.reader");

  /**
   * A writer of inventory tags.
   */

  public static final MRoleName ROLE_INVENTORY_TAGS_WRITER =
    new MRoleName("inventory.tags.writer");

  /**
   * A reader of inventory tags.
   */

  public static final MRoleName ROLE_INVENTORY_TAGS_READER =
    new MRoleName("inventory.tags.reader");

  /**
   * A writer of inventory locations.
   */

  public static final MRoleName ROLE_INVENTORY_LOCATIONS_WRITER =
    new MRoleName("inventory.locations.writer");

  /**
   * A reader of inventory locations.
   */

  public static final MRoleName ROLE_INVENTORY_LOCATIONS_READER =
    new MRoleName("inventory.locations.reader");

  /**
   * An all-powerful administrator of inventories.
   */

  public static final MRoleName ROLE_INVENTORY_ADMIN =
    new MRoleName("inventory.admin");

  /**
   * All roles.
   */

  public static final Set<MRoleName> ROLES_ALL =
    Set.of(
      ROLE_INVENTORY_ADMIN,
      ROLE_INVENTORY_FILES_READER,
      ROLE_INVENTORY_FILES_WRITER,
      ROLE_INVENTORY_ITEMS_READER,
      ROLE_INVENTORY_ITEMS_WRITER,
      ROLE_INVENTORY_LOCATIONS_READER,
      ROLE_INVENTORY_LOCATIONS_WRITER,
      ROLE_INVENTORY_TAGS_READER,
      ROLE_INVENTORY_TAGS_WRITER
    );

  /**
   * Load the internal security policy.
   *
   * @return A policy
   *
   * @throws IOException On errors
   */

  public static MPolicy open()
    throws IOException
  {
    final var parsers = new MPolicyParsers();

    final var resource =
      "/com/io7m/cardant/security/Policy.mp";
    try (var stream =
           CASecurityPolicy.class.getResourceAsStream(resource)) {
      final var source = URI.create(resource);
      try (var parser =
             parsers.createParser(
               source,
               stream,
               CASecurityPolicy::logStatus)) {
        return parser.execute();
      } catch (final ParseException e) {
        LOG.error("One or more parse errors were encountered.");
        throw new IOException(e.getMessage(), e);
      }
    }
  }

  private static void logStatus(
    final ParseStatus status)
  {
    switch (status.severity()) {
      case PARSE_ERROR -> {
        LOG.error(
          "{}:{}: {}: {}",
          Integer.valueOf(status.lexical().line()),
          Integer.valueOf(status.lexical().column()),
          status.errorCode(),
          status.message()
        );
      }
      case PARSE_WARNING -> {
        LOG.warn(
          "{}:{}: {}: {}",
          Integer.valueOf(status.lexical().line()),
          Integer.valueOf(status.lexical().column()),
          status.errorCode(),
          status.message()
        );
      }
      case PARSE_INFO -> {
        LOG.info(
          "{}:{}: {}: {}",
          Integer.valueOf(status.lexical().line()),
          Integer.valueOf(status.lexical().column()),
          status.errorCode(),
          status.message()
        );
      }
    }
  }

  private CASecurityPolicy()
  {

  }
}
