/*
 * Copyright © 2025 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.protocol.inventory.json.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.io7m.cardant.error_codes.CAErrorCode;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * A command failed.
 *
 * @param requestId         The request ID
 * @param message           The error summary
 * @param remediatingAction The remediating action, if any
 * @param errorCode         The error kind
 * @param attributes        The error attributes
 * @param blame             The blame assignment
 * @param extras            The extra error messages
 */

@JsonTypeName("ResponseError")
public record CJ1ResponseError(
  @JsonProperty(value = "requestId", required = true)
  UUID requestId,
  @JsonProperty(value = "message", required = true)
  String message,
  @JsonProperty(value = "errorCode", required = true)
  CAErrorCode errorCode,
  @JsonProperty("attributes")
  Map<String, String> attributes,
  @JsonProperty("remediatingAction")
  Optional<String> remediatingAction,
  @JsonProperty("blame")
  CJ1ResponseBlame blame,
  @JsonProperty("extras")
  List<CJ1StructuredError> extras)
  implements CJ1ResponseType
{

}
