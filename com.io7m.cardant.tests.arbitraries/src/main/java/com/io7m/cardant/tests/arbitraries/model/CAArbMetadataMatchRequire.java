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


package com.io7m.cardant.tests.arbitraries.model;

import com.io7m.cardant.model.CAItemSearchParameters.CAMetadataMatchType.CAMetadataRequire;
import com.io7m.cardant.model.CAItemSearchParameters.CAMetadataValueMatchType;
import com.io7m.cardant.tests.arbitraries.CAArbAbstract;
import com.io7m.lanark.core.RDottedName;
import net.jqwik.api.Arbitraries;

public final class CAArbMetadataMatchRequire
  extends CAArbAbstract<CAMetadataRequire>
{
  public CAArbMetadataMatchRequire()
  {
    super(
      CAMetadataRequire.class,
      () -> Arbitraries.maps(
        Arbitraries.defaultFor(RDottedName.class),
        Arbitraries.defaultFor(CAMetadataValueMatchType.class)
      ).map(CAMetadataRequire::new)
    );
  }
}