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
import com.io7m.cardant.database.postgres.internal.CADBQFileGet;
import com.io7m.cardant.database.postgres.internal.CADBQFilePut;
import com.io7m.cardant.database.postgres.internal.CADBQFileRemove;
import com.io7m.cardant.database.postgres.internal.CADBQFileSearch;
import com.io7m.cardant.database.postgres.internal.CADBQItemAttachmentAdd;
import com.io7m.cardant.database.postgres.internal.CADBQItemAttachmentRemove;
import com.io7m.cardant.database.postgres.internal.CADBQItemAttachmentsGet;
import com.io7m.cardant.database.postgres.internal.CADBQItemCreate;
import com.io7m.cardant.database.postgres.internal.CADBQItemDelete;
import com.io7m.cardant.database.postgres.internal.CADBQItemDeleteMarkOnly;
import com.io7m.cardant.database.postgres.internal.CADBQItemGet;
import com.io7m.cardant.database.postgres.internal.CADBQItemLocations;
import com.io7m.cardant.database.postgres.internal.CADBQItemMetadataGet;
import com.io7m.cardant.database.postgres.internal.CADBQItemMetadataPut;
import com.io7m.cardant.database.postgres.internal.CADBQItemMetadataRemove;
import com.io7m.cardant.database.postgres.internal.CADBQItemReposit;
import com.io7m.cardant.database.postgres.internal.CADBQItemSearch;
import com.io7m.cardant.database.postgres.internal.CADBQItemSetName;
import com.io7m.cardant.database.postgres.internal.CADBQItemTagAdd;
import com.io7m.cardant.database.postgres.internal.CADBQItemTagList;
import com.io7m.cardant.database.postgres.internal.CADBQItemTagRemove;
import com.io7m.cardant.database.postgres.internal.CADBQItemTypeDeclGet;
import com.io7m.cardant.database.postgres.internal.CADBQItemTypeDeclGetMultiple;
import com.io7m.cardant.database.postgres.internal.CADBQItemTypeDeclPut;
import com.io7m.cardant.database.postgres.internal.CADBQItemTypeDeclRemove;
import com.io7m.cardant.database.postgres.internal.CADBQItemTypeDeclsReferencingScalar;
import com.io7m.cardant.database.postgres.internal.CADBQItemTypeDeclsSearch;
import com.io7m.cardant.database.postgres.internal.CADBQItemTypeScalarGet;
import com.io7m.cardant.database.postgres.internal.CADBQItemTypeScalarPut;
import com.io7m.cardant.database.postgres.internal.CADBQItemTypeScalarRemove;
import com.io7m.cardant.database.postgres.internal.CADBQItemTypeScalarSearch;
import com.io7m.cardant.database.postgres.internal.CADBQLocationGet;
import com.io7m.cardant.database.postgres.internal.CADBQLocationList;
import com.io7m.cardant.database.postgres.internal.CADBQLocationPut;
import com.io7m.cardant.database.postgres.internal.CADBQMaintenance;
import com.io7m.cardant.database.postgres.internal.CADBQTagDelete;
import com.io7m.cardant.database.postgres.internal.CADBQTagGet;
import com.io7m.cardant.database.postgres.internal.CADBQTagList;
import com.io7m.cardant.database.postgres.internal.CADBQTagPut;
import com.io7m.cardant.database.postgres.internal.CADBQUserGet;
import com.io7m.cardant.database.postgres.internal.CADBQUserPut;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType;

/**
 * Inventory system (Postgres database)
 */

module com.io7m.cardant.database.postgres
{
  requires static org.osgi.annotation.bundle;
  requires static org.osgi.annotation.versioning;

  requires transitive com.io7m.cardant.database.api;
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
  requires org.jooq;
  requires org.postgresql.jdbc;
  requires org.slf4j;

  exports com.io7m.cardant.database.postgres.internal.tables
    to org.jooq;
  exports com.io7m.cardant.database.postgres.internal.tables.records
    to org.jooq;
  exports com.io7m.cardant.database.postgres.internal
    to org.jooq;

  provides CADatabaseFactoryType
    with CAPGDatabases;

  uses CADBQueryProviderType;

  provides CADBQueryProviderType with
    CADBQFileGet,
    CADBQFilePut,
    CADBQFileRemove,
    CADBQFileSearch,
    CADBQItemAttachmentAdd,
    CADBQItemAttachmentRemove,
    CADBQItemAttachmentsGet,
    CADBQItemCreate,
    CADBQItemDelete,
    CADBQItemDeleteMarkOnly,
    CADBQItemGet,
    CADBQItemLocations,
    CADBQItemMetadataGet,
    CADBQItemMetadataPut,
    CADBQItemMetadataRemove,
    CADBQItemReposit,
    CADBQItemSearch,
    CADBQItemSetName,
    CADBQItemTagAdd,
    CADBQItemTagList,
    CADBQItemTagRemove,
    CADBQItemTypeDeclGet,
    CADBQItemTypeDeclGetMultiple,
    CADBQItemTypeDeclPut,
    CADBQItemTypeDeclRemove,
    CADBQItemTypeDeclsReferencingScalar,
    CADBQItemTypeDeclsSearch,
    CADBQItemTypeScalarGet,
    CADBQItemTypeScalarPut,
    CADBQItemTypeScalarRemove,
    CADBQItemTypeScalarSearch,
    CADBQLocationGet,
    CADBQLocationList,
    CADBQLocationPut,
    CADBQMaintenance,
    CADBQTagDelete,
    CADBQTagGet,
    CADBQTagList,
    CADBQTagPut,
    CADBQUserGet,
    CADBQUserPut;

  exports com.io7m.cardant.database.postgres;
}
