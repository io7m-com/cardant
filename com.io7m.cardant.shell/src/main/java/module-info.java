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

import com.io7m.cardant.parsers.CASyntaxFactoryType;
import com.io7m.cardant.shell.CAShellFactoryType;
import com.io7m.cardant.shell.CAShells;

/**
 * Inventory server (Shell)
 */

module com.io7m.cardant.shell
{
  requires static org.osgi.annotation.bundle;
  requires static org.osgi.annotation.versioning;

  requires com.io7m.cardant.client.api;
  requires com.io7m.cardant.client.basic;
  requires com.io7m.cardant.client.preferences.api;
  requires com.io7m.cardant.parsers;
  requires com.io7m.cardant.strings;
  requires com.io7m.cardant.type_packages.parsers;

  requires com.io7m.jeucreader.core;
  requires com.io7m.jmulticlose.core;
  requires com.io7m.jxe.core;
  requires com.io7m.lanark.core;
  requires com.io7m.quarrel.core;
  requires com.io7m.tabla.core;
  requires hu.webarticum.treeprinter;
  requires org.apache.commons.io;
  requires org.apache.tika.core;
  requires org.jline;

  uses CASyntaxFactoryType;

  provides CAShellFactoryType
    with CAShells;

  exports com.io7m.cardant.shell;

  exports com.io7m.cardant.shell.internal.converters
    to com.io7m.cardant.tests;
}
