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

package com.io7m.cardant.protocol.inventory;

import com.io7m.cardant.error_codes.CAErrorCode;
import com.io7m.seltzer.api.SStructuredErrorType;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * A command failed.
 *
 * @param requestId         The request ID
 * @param message           The error summary
 * @param remediatingAction The remediating action, if any
 * @param errorCode         The error code
 * @param attributes        The error attributes
 * @param exception         The exception associated with the error, if any
 */

public record CAIResponseError(
  UUID requestId,
  String message,
  CAErrorCode errorCode,
  Map<String, String> attributes,
  Optional<String> remediatingAction,
  Optional<Throwable> exception)
  implements CAIResponseType, SStructuredErrorType<CAErrorCode>
{
  /**
   * A command failed.
   *
   * @param requestId         The request ID
   * @param message           The error summary
   * @param remediatingAction The remediating action, if any
   * @param errorCode         The error code
   * @param attributes        The error attributes
   * @param exception         The exception associated with the error, if any
   */

  public CAIResponseError
  {
    Objects.requireNonNull(requestId, "requestId");
    Objects.requireNonNull(errorCode, "errorCode");
    Objects.requireNonNull(remediatingAction, "remediatingAction");
    Objects.requireNonNull(exception, "exception");
    Objects.requireNonNull(message, "summary");
    Objects.requireNonNull(attributes, "attributes");
  }
}
