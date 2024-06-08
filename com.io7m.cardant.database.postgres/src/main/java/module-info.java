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
import com.io7m.cardant.database.postgres.internal.CADBQAuditEventAdd;
import com.io7m.cardant.database.postgres.internal.CADBQAuditEventSearch;
import com.io7m.cardant.database.postgres.internal.CADBQFileGet;
import com.io7m.cardant.database.postgres.internal.CADBQFilePut;
import com.io7m.cardant.database.postgres.internal.CADBQFileRemove;
import com.io7m.cardant.database.postgres.internal.CADBQFileSearch;
import com.io7m.cardant.database.postgres.internal.CADBQItemAttachmentAdd;
import com.io7m.cardant.database.postgres.internal.CADBQItemAttachmentRemove;
import com.io7m.cardant.database.postgres.internal.CADBQItemCreate;
import com.io7m.cardant.database.postgres.internal.CADBQItemDelete;
import com.io7m.cardant.database.postgres.internal.CADBQItemDeleteMarkOnly;
import com.io7m.cardant.database.postgres.internal.CADBQItemGet;
import com.io7m.cardant.database.postgres.internal.CADBQItemMetadataPut;
import com.io7m.cardant.database.postgres.internal.CADBQItemMetadataRemove;
import com.io7m.cardant.database.postgres.internal.CADBQItemSearch;
import com.io7m.cardant.database.postgres.internal.CADBQItemSetName;
import com.io7m.cardant.database.postgres.internal.CADBQItemTypesAssign;
import com.io7m.cardant.database.postgres.internal.CADBQItemTypesRevoke;
import com.io7m.cardant.database.postgres.internal.CADBQLocationAttachmentAdd;
import com.io7m.cardant.database.postgres.internal.CADBQLocationAttachmentRemove;
import com.io7m.cardant.database.postgres.internal.CADBQLocationDelete;
import com.io7m.cardant.database.postgres.internal.CADBQLocationDeleteMarkOnly;
import com.io7m.cardant.database.postgres.internal.CADBQLocationGet;
import com.io7m.cardant.database.postgres.internal.CADBQLocationList;
import com.io7m.cardant.database.postgres.internal.CADBQLocationMetadataPut;
import com.io7m.cardant.database.postgres.internal.CADBQLocationMetadataRemove;
import com.io7m.cardant.database.postgres.internal.CADBQLocationPut;
import com.io7m.cardant.database.postgres.internal.CADBQLocationTypesAssign;
import com.io7m.cardant.database.postgres.internal.CADBQLocationTypesRevoke;
import com.io7m.cardant.database.postgres.internal.CADBQMaintenance;
import com.io7m.cardant.database.postgres.internal.CADBQStockCount;
import com.io7m.cardant.database.postgres.internal.CADBQStockReposit;
import com.io7m.cardant.database.postgres.internal.CADBQStockSearch;
import com.io7m.cardant.database.postgres.internal.CADBQTypePackageGetText;
import com.io7m.cardant.database.postgres.internal.CADBQTypePackageInstall;
import com.io7m.cardant.database.postgres.internal.CADBQTypePackageSatisfying;
import com.io7m.cardant.database.postgres.internal.CADBQTypePackageSearch;
import com.io7m.cardant.database.postgres.internal.CADBQTypePackageUninstall;
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

  requires com.io7m.cardant.database.api;
  requires com.io7m.cardant.model;
  requires com.io7m.cardant.security;
  requires com.io7m.cardant.strings;
  requires com.io7m.cardant.type_packages.parser.api;

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
CADBQAuditEventAdd,
CADBQAuditEventSearch,
CADBQFileGet,
CADBQFilePut,
CADBQFileRemove,
CADBQFileSearch,
CADBQItemAttachmentAdd,
CADBQItemAttachmentRemove,
CADBQItemCreate,
CADBQItemDelete,
CADBQItemDeleteMarkOnly,
CADBQItemGet,
CADBQItemMetadataPut,
CADBQItemMetadataRemove,
CADBQItemSearch,
CADBQItemSetName,
CADBQItemTypesAssign,
CADBQItemTypesRevoke,
CADBQLocationAttachmentAdd,
CADBQLocationAttachmentRemove,
CADBQLocationDelete,
CADBQLocationDeleteMarkOnly,
CADBQLocationGet,
CADBQLocationList,
CADBQLocationMetadataPut,
CADBQLocationMetadataRemove,
CADBQLocationPut,
CADBQLocationTypesAssign,
CADBQLocationTypesRevoke,
CADBQMaintenance,
CADBQStockCount,
CADBQStockReposit,
CADBQStockSearch,
CADBQTypePackageGetText,
CADBQTypePackageInstall,
CADBQTypePackageSatisfying,
CADBQTypePackageSearch,
CADBQTypePackageUninstall,
CADBQUserGet,
CADBQUserPut
    ;

  exports com.io7m.cardant.database.postgres;
}
