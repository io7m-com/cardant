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


package com.io7m.cardant.server.controller.inventory;

import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CATypeChecking;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.strings.CAStringConstantApplied;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.cardant.type_packages.resolver.api.CATypePackageResolverType;

import java.util.Set;
import java.util.stream.Collectors;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorTypeCheckFailed;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_INDEXED;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_INDEXED_ATTRIBUTE;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_TYPE_CHECKING;

final class CAITypeChecking
{
  private CAITypeChecking()
  {

  }

  static void checkTypes(
    final CAICommandContext context,
    final CATypePackageResolverType resolver,
    final CALocation location)
    throws CACommandExecutionFailure
  {
    final var types =
      location.types()
        .stream()
        .flatMap(t -> resolver.findTypeRecord(t).stream())
        .collect(Collectors.toSet());

    final var checker =
      CATypeChecking.create(
        context.services().requireService(CAStrings.class),
        types,
        Set.copyOf(location.metadata().values())
      );

    final var errors = checker.execute();
    if (!errors.isEmpty()) {
      for (int index = 0; index < errors.size(); ++index) {
        final var error = errors.get(index);
        context.setAttribute(
          new CAStringConstantApplied(
            ERROR_INDEXED,
            Integer.valueOf(index)
          ),
          error.message()
        );

        for (final var attributeEntry : error.attributes().entrySet()) {
          context.setAttribute(
            new CAStringConstantApplied(
              ERROR_INDEXED_ATTRIBUTE,
              new Object[]{
                Integer.valueOf(index),
                attributeEntry.getKey(),
              }
            ),
            attributeEntry.getValue()
          );
        }
      }
      throw context.failFormatted(
        400,
        errorTypeCheckFailed(),
        context.attributes(),
        ERROR_TYPE_CHECKING
      );
    }
  }

  static void checkTypes(
    final CAICommandContext context,
    final CATypePackageResolverType resolver,
    final CAItem item)
    throws CACommandExecutionFailure
  {
    final var types =
      item.types()
        .stream()
        .flatMap(t -> resolver.findTypeRecord(t).stream())
        .collect(Collectors.toSet());

    final var checker =
      CATypeChecking.create(
        context.services().requireService(CAStrings.class),
        types,
        Set.copyOf(item.metadata().values())
      );

    final var errors = checker.execute();
    if (!errors.isEmpty()) {
      for (int index = 0; index < errors.size(); ++index) {
        final var error = errors.get(index);
        context.setAttribute(
          new CAStringConstantApplied(
            ERROR_INDEXED,
            Integer.valueOf(index)
          ),
          error.message()
        );

        for (final var attributeEntry : error.attributes().entrySet()) {
          context.setAttribute(
            new CAStringConstantApplied(
              ERROR_INDEXED_ATTRIBUTE,
              new Object[]{
                Integer.valueOf(index),
                attributeEntry.getKey(),
              }
            ),
            attributeEntry.getValue()
          );
        }
      }
      throw context.failFormatted(
        400,
        errorTypeCheckFailed(),
        context.attributes(),
        ERROR_TYPE_CHECKING
      );
    }
  }
}
