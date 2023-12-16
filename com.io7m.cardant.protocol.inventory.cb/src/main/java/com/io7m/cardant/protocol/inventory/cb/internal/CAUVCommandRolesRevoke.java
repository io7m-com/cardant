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


package com.io7m.cardant.protocol.inventory.cb.internal;

import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.CAICommandRolesRevoke;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandRolesRevoke;
import com.io7m.cedarbridge.runtime.api.CBString;
import com.io7m.cedarbridge.runtime.api.CBUUID;
import com.io7m.cedarbridge.runtime.convenience.CBLists;
import com.io7m.cedarbridge.runtime.convenience.CBSets;
import com.io7m.medrina.api.MRoleName;

/**
 * A validator.
 */

public enum CAUVCommandRolesRevoke
  implements CAProtocolMessageValidatorType<
  CAICommandRolesRevoke, CAI1CommandRolesRevoke>
{
  /**
   * A validator.
   */

  COMMAND_ROLES_REVOKE;

  @Override
  public CAI1CommandRolesRevoke convertToWire(
    final CAICommandRolesRevoke c)
  {
    return new CAI1CommandRolesRevoke(
      new CBUUID(c.user().id()),
      CBLists.ofCollection(c.roles(), r -> new CBString(r.value().value()))
    );
  }

  @Override
  public CAICommandRolesRevoke convertFromWire(
    final CAI1CommandRolesRevoke m)
  {
    return new CAICommandRolesRevoke(
      new CAUserID(m.fieldUser().value()),
      CBSets.toSet(m.fieldRoles(), x -> MRoleName.of(x.value()))
    );
  }
}
