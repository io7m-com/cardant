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

package com.io7m.cardant.gui.internal.model;

import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType;
import javafx.beans.property.SimpleStringProperty;

import static com.io7m.cardant.gui.internal.model.CAStringSearch.containsIgnoreCase;

public record CAFileMutable(
  CAFileID id,
  SimpleStringProperty description,
  SimpleStringProperty mediaType,
  long size,
  String hashAlgorithm,
  String hashValue)
{
  public static CAFileMutable ofFile(
    final CAFileType file)
  {
    return new CAFileMutable(
      file.id(),
      new SimpleStringProperty(file.description()),
      new SimpleStringProperty(file.mediaType()),
      file.size(),
      file.hashAlgorithm(),
      file.hashValue()
    );
  }

  public void updateFrom(
    final CAFileType attachment)
  {
    this.description.set(attachment.description());
    this.mediaType.set(attachment.mediaType());
  }

  public boolean matches(
    final String search)
  {
    return containsIgnoreCase(this.description, search)
      || containsIgnoreCase(this.mediaType, search)
      || containsIgnoreCase(this.hashValue, search);
  }
}
