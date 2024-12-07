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


package com.io7m.cardant.server.inventory.v1;

import com.io7m.cardant.error_codes.CAException;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.protocol.inventory.CAIResponseBlame;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAITransactionResponse;
import com.io7m.cardant.protocol.inventory.cb.CAI1Messages;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.http.CAHTTPRequestInformation;
import com.io7m.cardant.server.http.CAHTTPResponseFixedSize;
import com.io7m.cardant.server.http.CAHTTPResponseType;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Functions to transform exceptions.
 */

public final class CA1Errors
{
  private CA1Errors()
  {

  }

  /**
   * Transform an exception into an error response.
   *
   * @param information The request information
   * @param blame       The blame assignment
   * @param exception   The exception
   *
   * @return An error response
   */

  static CAIResponseError errorOf(
    final CAHTTPRequestInformation information,
    final CAIResponseBlame blame,
    final CAException exception)
  {
    return new CAIResponseError(
      information.requestID(),
      exception.getMessage(),
      exception.errorCode(),
      exception.attributes(),
      exception.remediatingAction(),
      Optional.of(exception),
      blame,
      List.of()
    );
  }

  /**
   * Transform an exception into an error response.
   *
   * @param information The request information
   * @param blame       The blame assignment
   * @param exception   The exception
   *
   * @return An error response
   */

  static CAIResponseError errorOf(
    final CAHTTPRequestInformation information,
    final CAIResponseBlame blame,
    final IOException exception)
  {
    return new CAIResponseError(
      information.requestID(),
      Objects.requireNonNullElse(exception.getMessage(), exception.getClass().getName()),
      CAStandardErrorCodes.errorIo(),
      Map.of(),
      Optional.empty(),
      Optional.of(exception),
      blame,
      List.of()
    );
  }

  /**
   * Transform an exception into an error response.
   *
   * @param messages    A message serializer
   * @param information The request information
   * @param blame       The blame assignment
   * @param exception   The exception
   *
   * @return An error response
   */

  public static CAHTTPResponseType errorResponseOf(
    final CAI1Messages messages,
    final CAHTTPRequestInformation information,
    final CAIResponseBlame blame,
    final CAException exception)
  {
    return new CAHTTPResponseFixedSize(
      switch (blame) {
        case BLAME_CLIENT -> 400;
        case BLAME_SERVER -> 500;
      },
      Set.of(),
      CAI1Messages.contentType(),
      messages.serialize(errorOf(information, blame, exception))
    );
  }

  /**
   * Transform an exception into an error response.
   *
   * @param messages    A message serializer
   * @param information The request information
   * @param exception   The exception
   *
   * @return An error response
   */

  public static CAHTTPResponseType errorResponseOf(
    final CAI1Messages messages,
    final CAHTTPRequestInformation information,
    final CACommandExecutionFailure exception)
  {
    final CAIResponseBlame blame;
    if (exception.httpStatusCode() < 500) {
      blame = CAIResponseBlame.BLAME_CLIENT;
    } else {
      blame = CAIResponseBlame.BLAME_SERVER;
    }

    return new CAHTTPResponseFixedSize(
      exception.httpStatusCode(),
      Set.of(),
      CAI1Messages.contentType(),
      messages.serialize(errorOf(information, blame, exception))
    );
  }

  /**
   * Produce a transaction response type.
   *
   * @param messages A message serializer
   * @param response The transaction response
   *
   * @return An error response
   */

  public static CAHTTPResponseType transactionResponseOf(
    final CAI1Messages messages,
    final CAITransactionResponse response)
  {
    return new CAHTTPResponseFixedSize(
      response.firstError()
        .map(e -> {
          return switch (e.blame()) {
            case BLAME_CLIENT -> Integer.valueOf(400);
            case BLAME_SERVER -> Integer.valueOf(500);
          };
        })
        .orElseGet(() -> Integer.valueOf(200))
        .intValue(),
      Set.of(),
      CAI1Messages.contentTypeForSequence(),
      messages.serialize(response)
    );
  }
}
