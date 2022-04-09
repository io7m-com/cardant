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

package com.io7m.cardant.tests;

import com.io7m.anethum.common.ParseException;
import com.io7m.cardant.security.api.CSAction;
import com.io7m.cardant.security.api.CSAttributeName;
import com.io7m.cardant.security.api.CSLabel;
import com.io7m.cardant.security.vanilla.CSLabelParsers;
import com.io7m.cardant.security.api.CSMatchLabelType;
import com.io7m.cardant.security.api.CSMatchRoleType.CSMatchRolesAllOf;
import com.io7m.cardant.security.api.CSPolicyRule;
import com.io7m.cardant.security.api.CSRoleSet;
import com.io7m.cardant.security.vanilla.CSRoleSetParsers;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.io7m.cardant.security.api.CSMatchAttributeValueType.CSMatchAttributeValueAny.ANY_VALUE;
import static com.io7m.cardant.security.api.CSMatchAttributeValueType.exactValue;
import static com.io7m.cardant.security.api.CSMatchLabelType.CSMatchLabelAny.ANY_LABEL;
import static com.io7m.cardant.security.api.CSMatchObjectType.CSMatchObjectByType.ANY_OBJECT;
import static com.io7m.cardant.security.api.CSMatchObjectType.CSMatchObjectByType.ITEM;
import static com.io7m.cardant.security.api.CSMatchObjectType.CSMatchObjectByType.LOCATION;
import static com.io7m.cardant.security.api.CSMatchObjectType.CSMatchObjectByType.USER;
import static com.io7m.cardant.security.api.CSMatchRoleType.CSMatchRolesAny.ANY_ROLES;
import static com.io7m.cardant.security.api.CSPolicyConclusion.ALLOW;
import static com.io7m.cardant.security.api.CSPolicyConclusion.ALLOW_IMMEDIATELY;
import static com.io7m.cardant.security.api.CSPolicyConclusion.DENY;
import static com.io7m.cardant.security.api.CSPolicyConclusion.DENY_IMMEDIATELY;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class CSPolicyRuleTest
{
  private static CSRoleSet rolesOf(
    final String text)
  {
    try {
      return new CSRoleSetParsers().parseFromString(text);
    } catch (final ParseException e) {
      throw new IllegalStateException(e);
    }
  }

  private static CSLabel labelOf(
    final String text)
  {
    try {
      return new CSLabelParsers().parseFromString(text);
    } catch (final ParseException e) {
      throw new IllegalStateException(e);
    }
  }

  @Test
  public void testSerialize0()
  {
    final var rule =
      new CSPolicyRule(
        DENY,
        ANY_ROLES,
        new CSAction("write"),
        ANY_OBJECT,
        ANY_LABEL
      );

    assertEquals("deny roles * action write to type any label *", rule.serialized());
  }

  @Test
  public void testSerialize1()
  {
    final var rule =
      new CSPolicyRule(
        ALLOW,
        ANY_ROLES,
        new CSAction("write"),
        ANY_OBJECT,
        ANY_LABEL
      );

    assertEquals("allow roles * action write to type any label *", rule.serialized());
  }

  @Test
  public void testSerialize2()
  {
    final var rule =
      new CSPolicyRule(
        DENY_IMMEDIATELY,
        ANY_ROLES,
        new CSAction("write"),
        ANY_OBJECT,
        ANY_LABEL
      );

    assertEquals("deny immediately roles * action write to type any label *", rule.serialized());
  }

  @Test
  public void testSerialize3()
  {
    final var rule =
      new CSPolicyRule(
        ALLOW_IMMEDIATELY,
        ANY_ROLES,
        new CSAction("write"),
        ANY_OBJECT,
        ANY_LABEL
      );

    assertEquals("allow immediately roles * action write to type any label *", rule.serialized());
  }

  @Test
  public void testSerialize4()
  {
    final var rule =
      new CSPolicyRule(
        DENY,
        new CSMatchRolesAllOf(rolesOf("a;b;")),
        new CSAction("write"),
        ANY_OBJECT,
        ANY_LABEL
      );

    assertEquals("deny roles all a;b; action write to type any label *", rule.serialized());
  }

  @Test
  public void testSerialize5()
  {
    final var rule =
      new CSPolicyRule(
        DENY,
        new CSMatchRolesAllOf(rolesOf("a;b;")),
        new CSAction("read"),
        ANY_OBJECT,
        ANY_LABEL
      );

    assertEquals("deny roles all a;b; action read to type any label *", rule.serialized());
  }

  @Test
  public void testSerialize6()
  {
    {
      final var rule =
        new CSPolicyRule(
          DENY,
          new CSMatchRolesAllOf(rolesOf("a;b;")),
          new CSAction("write"),
          ITEM,
          ANY_LABEL
        );

      assertEquals("deny roles all a;b; action write to type item label *", rule.serialized());
    }

    {
      final var rule =
        new CSPolicyRule(
          DENY,
          new CSMatchRolesAllOf(rolesOf("a;b;")),
          new CSAction("write"),
          LOCATION,
          ANY_LABEL
        );

      assertEquals("deny roles all a;b; action write to type location label *", rule.serialized());
    }

    {
      final var rule =
        new CSPolicyRule(
          DENY,
          new CSMatchRolesAllOf(rolesOf("a;b;")),
          new CSAction("write"),
          USER,
          ANY_LABEL
        );

      assertEquals("deny roles all a;b; action write to type user label *", rule.serialized());
    }
  }

  @Test
  public void testSerialize7()
  {
    final var rule =
      new CSPolicyRule(
        DENY,
        ANY_ROLES,
        new CSAction("write"),
        ITEM,
        CSMatchLabelType.anyOf(
          Map.entry(new CSAttributeName("x"), ANY_VALUE),
          Map.entry(new CSAttributeName("y"), exactValue("q"))
        )
      );

    assertEquals("deny roles * action write to type item label x=*;y=q;", rule.serialized());
  }
}
