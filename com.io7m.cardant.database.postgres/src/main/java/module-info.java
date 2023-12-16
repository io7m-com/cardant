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


import com.io7m.cardant.database.api.CADatabaseFactoryType;
import com.io7m.cardant.database.postgres.CAPGDatabases;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType;

/**
 * Inventory system (Postgres database)
 */

module com.io7m.cardant.database.postgres
{
  requires static org.osgi.annotation.bundle;
  requires static org.osgi.annotation.versioning;

  requires com.io7m.cardant.database.api;
  requires com.io7m.cardant.security;
  requires com.io7m.cardant.strings;

  requires com.io7m.anethum.api;
  requires com.io7m.jmulticlose.core;
  requires com.io7m.jqpage.core;
  requires com.io7m.lanark.core;
  requires com.io7m.trasco.api;
  requires com.io7m.trasco.vanilla;
  requires com.zaxxer.hikari;
  requires io.opentelemetry.api;
  requires io.opentelemetry.context;
  requires io.opentelemetry.semconv;
  requires java.sql;
  requires org.jgrapht.core;
  requires org.jooq.postgres.extensions;
  requires org.jooq;
  requires org.postgresql.jdbc;
  requires org.slf4j;

  exports com.io7m.cardant.database.postgres.internal.tables
    to org.jooq;
  exports com.io7m.cardant.database.postgres.internal
    to org.jooq, com.io7m.cardant.tests;

  provides CADatabaseFactoryType
    with CAPGDatabases;

  uses CADBQueryProviderType;

  provides CADBQueryProviderType with
    com.io7m.cardant.database.postgres.internal.CADBQFileGet,
    com.io7m.cardant.database.postgres.internal.CADBQFilePut,
    com.io7m.cardant.database.postgres.internal.CADBQFileRemove,
    com.io7m.cardant.database.postgres.internal.CADBQFileSearch,
    com.io7m.cardant.database.postgres.internal.CADBQItemAttachmentAdd,
    com.io7m.cardant.database.postgres.internal.CADBQItemAttachmentRemove,
    com.io7m.cardant.database.postgres.internal.CADBQItemCreate,
    com.io7m.cardant.database.postgres.internal.CADBQItemDelete,
    com.io7m.cardant.database.postgres.internal.CADBQItemDeleteMarkOnly,
    com.io7m.cardant.database.postgres.internal.CADBQItemGet,
    com.io7m.cardant.database.postgres.internal.CADBQItemLocations,
    com.io7m.cardant.database.postgres.internal.CADBQItemMetadataPut,
    com.io7m.cardant.database.postgres.internal.CADBQItemMetadataRemove,
    com.io7m.cardant.database.postgres.internal.CADBQItemReposit,
    com.io7m.cardant.database.postgres.internal.CADBQItemSearch,
    com.io7m.cardant.database.postgres.internal.CADBQItemSetName,
    com.io7m.cardant.database.postgres.internal.CADBQItemTypesAssign,
    com.io7m.cardant.database.postgres.internal.CADBQItemTypesRevoke,
    com.io7m.cardant.database.postgres.internal.CADBQLocationAttachmentAdd,
    com.io7m.cardant.database.postgres.internal.CADBQLocationAttachmentRemove,
    com.io7m.cardant.database.postgres.internal.CADBQLocationGet,
    com.io7m.cardant.database.postgres.internal.CADBQLocationList,
    com.io7m.cardant.database.postgres.internal.CADBQLocationMetadataPut,
    com.io7m.cardant.database.postgres.internal.CADBQLocationMetadataRemove,
    com.io7m.cardant.database.postgres.internal.CADBQLocationPut,
    com.io7m.cardant.database.postgres.internal.CADBQLocationTypesAssign,
    com.io7m.cardant.database.postgres.internal.CADBQLocationTypesRevoke,
    com.io7m.cardant.database.postgres.internal.CADBQMaintenance,
    com.io7m.cardant.database.postgres.internal.CADBQTypeDeclGet,
    com.io7m.cardant.database.postgres.internal.CADBQTypeDeclGetMultiple,
    com.io7m.cardant.database.postgres.internal.CADBQTypeDeclPut,
    com.io7m.cardant.database.postgres.internal.CADBQTypeDeclRemove,
    com.io7m.cardant.database.postgres.internal.CADBQTypeDeclsReferencingScalar,
    com.io7m.cardant.database.postgres.internal.CADBQTypeDeclsSearch,
    com.io7m.cardant.database.postgres.internal.CADBQTypeScalarGet,
    com.io7m.cardant.database.postgres.internal.CADBQTypeScalarPut,
    com.io7m.cardant.database.postgres.internal.CADBQTypeScalarRemove,
    com.io7m.cardant.database.postgres.internal.CADBQTypeScalarSearch,
    com.io7m.cardant.database.postgres.internal.CADBQUserGet,
    com.io7m.cardant.database.postgres.internal.CADBQUserPut
    ;

  exports com.io7m.cardant.database.postgres;
}
