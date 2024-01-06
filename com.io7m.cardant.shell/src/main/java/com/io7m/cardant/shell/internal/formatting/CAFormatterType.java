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

package com.io7m.cardant.shell.internal.formatting;

import com.io7m.cardant.client.preferences.api.CAPreferenceServerBookmark;
import com.io7m.cardant.model.CAAuditEvent;
import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.model.CAFileType.CAFileWithoutData;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CAPage;
import com.io7m.cardant.model.CATypeRecord;
import com.io7m.cardant.model.CATypeRecordSummary;
import com.io7m.cardant.model.CATypeScalarType;
import com.io7m.cardant.model.type_package.CATypePackageSummary;
import com.io7m.medrina.api.MRoleName;

import java.util.List;
import java.util.Set;

/**
 * A shell formatter for data.
 */

public interface CAFormatterType
{
  /**
   * Format a file.
   *
   * @param file the file
   *
   * @throws Exception On errors
   */

  void formatFile(
    CAFileType file)
    throws Exception;

  /**
   * Format a page of file summaries.
   *
   * @param files The page
   *
   * @throws Exception On errors
   */

  void formatFilesPage(
    CAPage<CAFileWithoutData> files)
    throws Exception;

  /**
   * Format an item.
   *
   * @param item The item
   *
   * @throws Exception On errors
   */

  void formatItem(
    CAItem item)
    throws Exception;

  /**
   * Format a page of item summaries.
   *
   * @param items The items
   *
   * @throws Exception On errors
   */

  void formatItemsPage(
    CAPage<CAItemSummary> items)
    throws Exception;

  /**
   * Format a list of bookmarks.
   *
   * @param bookmarks The bookmarks
   *
   * @throws Exception On errors
   */

  void formatBookmarks(
    List<CAPreferenceServerBookmark> bookmarks)
    throws Exception;

  /**
   * Format a list of roles.
   *
   * @param roles The roles
   *
   * @throws Exception On errors
   */

  void formatRoles(
    Set<MRoleName> roles)
    throws Exception;

  /**
   * Format a set  of scalar types.
   *
   * @param types The types
   *
   * @throws Exception On errors
   */

  void formatTypesScalar(Set<CATypeScalarType> types)
    throws Exception;

  /**
   * Format a page of scalar types.
   *
   * @param types The types
   *
   * @throws Exception On errors
   */

  void formatTypesScalarPage(CAPage<CATypeScalarType> types)
    throws Exception;

  /**
   * Format a type declaration.
   *
   * @param type The type
   *
   * @throws Exception On errors
   */

  void formatTypeDeclaration(CATypeRecord type)
    throws Exception;

  /**
   * Format a page of types.
   *
   * @param types The types
   *
   * @throws Exception On errors
   */

  void formatTypeDeclarationPage(
    CAPage<CATypeRecordSummary> types)
    throws Exception;

  /**
   * Format a location.
   *
   * @param location The location
   *
   * @throws Exception On errors
   */

  void formatLocation(
    CALocation location)
    throws Exception;

  /**
   * Format a page of audit events.
   *
   * @param page The page
   *
   * @throws Exception On errors
   */

  void formatAuditPage(
    CAPage<CAAuditEvent> page)
    throws Exception;

  /**
   * Print text to the output.
   *
   * @param text The text
   *
   * @throws Exception On errors
   */

  void print(String text)
    throws Exception;

  /**
   * Print text to the output with a newline.
   *
   * @param text The text
   *
   * @throws Exception On errors
   */

  void printLine(String text)
    throws Exception;

  /**
   * Format a page of type packages.
   *
   * @param type The type packages
   *
   * @throws Exception On errors
   */

  void formatTypePackagePage(
    CAPage<CATypePackageSummary> type)
    throws Exception;

}
