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

import com.io7m.cardant.database.api.CADatabaseProviderType;
import com.io7m.cardant.protocol.inventory.api.CAMessageParserFactoryType;
import com.io7m.cardant.protocol.inventory.api.CAMessageSerializerFactoryType;
import com.io7m.cardant.server.CAServerConfigurations;
import com.io7m.cardant.server.CAServers;
import com.io7m.cardant.server.api.CAServerConfigurationParserFactoryType;
import com.io7m.cardant.server.api.CAServerConfigurationSerializerFactoryType;
import com.io7m.cardant.server.api.CAServerFactoryType;

/**
 * Inventory system (Server implementation).
 */

module com.io7m.cardant.server
{
  uses CADatabaseProviderType;
  uses CAMessageParserFactoryType;
  uses CAMessageSerializerFactoryType;

  requires static org.osgi.annotation.versioning;
  requires static org.osgi.annotation.bundle;

  requires transitive com.io7m.cardant.server.api;
  requires transitive com.io7m.cardant.database.api;

  requires com.io7m.blackthorne.api;
  requires com.io7m.blackthorne.jxe;
  requires com.io7m.cardant.model;
  requires com.io7m.cardant.protocol.inventory.api;
  requires com.io7m.cardant.protocol.versioning;
  requires com.io7m.jlexing.core;
  requires com.io7m.jmulticlose.core;
  requires com.io7m.junreachable.core;
  requires com.io7m.jxe.core;
  requires com.io7m.jxtrand.vanilla;
  requires java.management;
  requires java.xml;
  requires org.apache.commons.io;
  requires org.eclipse.jetty.jmx;
  requires org.eclipse.jetty.server;
  requires org.eclipse.jetty.servlet;
  requires org.slf4j;

  provides CAServerFactoryType
    with CAServers;
  provides CAServerConfigurationParserFactoryType
    with CAServerConfigurations;
  provides CAServerConfigurationSerializerFactoryType
    with CAServerConfigurations;

  exports com.io7m.cardant.server;
}
