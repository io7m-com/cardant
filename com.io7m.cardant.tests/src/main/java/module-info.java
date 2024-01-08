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

import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType;
import net.jqwik.api.providers.ArbitraryProvider;

/**
 * Test suite.
 */

open module com.io7m.cardant.tests
{
  requires com.io7m.cardant.client.api;
  requires com.io7m.cardant.client.basic;
  requires com.io7m.cardant.client.preferences.api;
  requires com.io7m.cardant.client.preferences.vanilla;
  requires com.io7m.cardant.database.api;
  requires com.io7m.cardant.database.postgres;
  requires com.io7m.cardant.error_codes;
  requires com.io7m.cardant.main;
  requires com.io7m.cardant.model;
  requires com.io7m.cardant.parsers;
  requires com.io7m.cardant.protocol.api;
  requires com.io7m.cardant.protocol.inventory.cb;
  requires com.io7m.cardant.protocol.inventory;
  requires com.io7m.cardant.security;
  requires com.io7m.cardant.server.api;
  requires com.io7m.cardant.server.basic;
  requires com.io7m.cardant.server.controller;
  requires com.io7m.cardant.server.http;
  requires com.io7m.cardant.server.inventory.v1;
  requires com.io7m.cardant.server.service.clock;
  requires com.io7m.cardant.server.service.configuration;
  requires com.io7m.cardant.server.service.idstore;
  requires com.io7m.cardant.server.service.maintenance;
  requires com.io7m.cardant.server.service.reqlimit;
  requires com.io7m.cardant.server.service.sessions;
  requires com.io7m.cardant.server.service.telemetry.api;
  requires com.io7m.cardant.server.service.telemetry.otp;
  requires com.io7m.cardant.server.service.verdant;
  requires com.io7m.cardant.shell;
  requires com.io7m.cardant.tests.arbitraries;
  requires com.io7m.cardant.tls;
  requires com.io7m.cardant.type_packages.checker.api;
  requires com.io7m.cardant.type_packages.checkers;
  requires com.io7m.cardant.type_packages.compiler.api;
  requires com.io7m.cardant.type_packages.compilers;
  requires com.io7m.cardant.type_packages.parser.api;
  requires com.io7m.cardant.type_packages.parsers;
  requires com.io7m.cardant.type_packages.resolver.api;
  requires com.io7m.cardant.type_packages.standard;
  requires com.io7m.cardant.type_packages.upgrades.api;
  requires com.io7m.cardant.type_packages.upgrades;

  uses ArbitraryProvider;
  uses CADBQueryProviderType;

  requires com.io7m.anethum.api;
  requires com.io7m.anethum.slf4j;
  requires com.io7m.ervilla.api;
  requires com.io7m.ervilla.native_exec;
  requires com.io7m.ervilla.postgres;
  requires com.io7m.ervilla.test_extension;
  requires com.io7m.idstore.admin_client.api;
  requires com.io7m.idstore.admin_client;
  requires com.io7m.idstore.server.api;
  requires com.io7m.idstore.server.service.configuration;
  requires com.io7m.junreachable.core;
  requires com.io7m.quarrel.ext.xstructural;
  requires com.io7m.repetoir.core;
  requires com.io7m.verdant.core.cb;
  requires com.io7m.verdant.core;
  requires com.io7m.zelador.test_extension;
  requires io.opentelemetry.api;
  requires java.net.http;
  requires java.sql;
  requires java.xml;
  requires net.bytebuddy.agent;
  requires net.bytebuddy;
  requires net.jqwik.api;
  requires org.jline;
  requires org.mockito;
  requires org.postgresql.jdbc;
  requires org.slf4j;

  requires transitive org.junit.jupiter.api;
  requires transitive org.junit.jupiter.engine;
  requires transitive org.junit.platform.commons;
  requires transitive org.junit.platform.engine;
  requires com.io7m.idstore.tls;
}
