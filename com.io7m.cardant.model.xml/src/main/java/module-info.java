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

import com.io7m.cardant.model.xml.CAInventoryParserFactoryType;
import com.io7m.cardant.model.xml.CAInventoryParsers;

/**
 * Inventory system (Model XML).
 */

module com.io7m.cardant.model.xml
{
  requires static org.osgi.annotation.bundle;
  requires static org.osgi.annotation.versioning;

  requires transitive com.io7m.anethum.api;
  requires transitive com.io7m.anethum.common;
  requires transitive com.io7m.cardant.model;
  requires com.io7m.blackthorne.api;
  requires com.io7m.jxe.core;
  requires com.io7m.blackthorne.jxe;

  provides CAInventoryParserFactoryType
    with CAInventoryParsers;

  exports com.io7m.cardant.model.xml;
}
