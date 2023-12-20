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


package com.io7m.cardant.tests;

import com.io7m.cardant.security.CASecurityDocumentation;
import com.io7m.cardant.security.CASecurityPolicy;
import com.io7m.medrina.api.MRoleName;

import java.util.Arrays;

public final class CARoles
{
  private CARoles()
  {

  }

  public static void main(
    final String[] args)
    throws Exception
  {
    System.out.println("""
<Table class="genericTable">
  <Columns>
    <Column>Name</Column>
    <Column>Description</Column>
  </Columns>
  """);

    final var roleFields =
      Arrays.stream(CASecurityPolicy.class.getFields())
        .filter(field -> field.getType().equals(MRoleName.class))
        .toList();

    for (final var roleField : roleFields) {
      final var roleValue =
        (MRoleName) roleField.get(CASecurityPolicy.class);
      final var description =
        roleField.getAnnotation(CASecurityDocumentation.class);

      System.out.printf("""
  <Row>
    <Cell>%s</Cell>
    <Cell>%s</Cell>
  </Row>
  """, roleValue.value().value(), description.value());
    }

    System.out.println("""
</Table>
  """);
  }
}
