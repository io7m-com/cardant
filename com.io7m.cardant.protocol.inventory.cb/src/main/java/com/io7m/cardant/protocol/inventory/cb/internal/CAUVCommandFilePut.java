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

import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.CAICommandFilePut;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandFilePut;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVFile.FILE;

/**
 * A validator.
 */

public enum CAUVCommandFilePut
  implements CAProtocolMessageValidatorType<CAICommandFilePut, CAI1CommandFilePut>
{
  /**
   * A validator.
   */

  COMMAND_FILE_PUT;

  @Override
  public CAI1CommandFilePut convertToWire(
    final CAICommandFilePut message)
  {
    return new CAI1CommandFilePut(FILE.convertToWire(message.data()));
  }

  @Override
  public CAICommandFilePut convertFromWire(
    final CAI1CommandFilePut message)
  {
    return new CAICommandFilePut(FILE.convertFromWire(message.fieldFile()));
  }
}
