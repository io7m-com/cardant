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


package com.io7m.cardant.shell.internal;

import com.io7m.cardant.client.api.CAClientException;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.protocol.inventory.CAICommandLocationGet;
import com.io7m.cardant.protocol.inventory.CAICommandLocationPut;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationPut;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed01;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorApiMisuse;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_SHELL_OPTIONS_COMBINATION;
import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "location-put"
 */

public final class CAShellCmdLocationPut
  extends CAShellCmdAbstractCR<CAICommandLocationPut, CAIResponseLocationPut>
{
  private static final QParameterNamed1<CALocationID> ID =
    new QParameterNamed1<>(
      "--id",
      List.of(),
      new QConstant("The location ID."),
      Optional.of(CALocationID.random()),
      CALocationID.class
    );

  private static final QParameterNamed01<CALocationID> PARENT =
    new QParameterNamed01<>(
      "--parent",
      List.of(),
      new QConstant("The parent location ID."),
      Optional.empty(),
      CALocationID.class
    );

  private static final QParameterNamed01<String> NAME =
    new QParameterNamed01<>(
      "--name",
      List.of(),
      new QConstant("The location name."),
      Optional.empty(),
      String.class
    );

  private static final QParameterNamed01<Boolean> PARENT_DETACH =
    new QParameterNamed01<>(
      "--detach",
      List.of(),
      new QConstant("Detach the location from its parent."),
      Optional.empty(),
      Boolean.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The context
   */

  public CAShellCmdLocationPut(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "location-put",
        new QConstant("Create or update a location."),
        Optional.empty()
      ),
      CAICommandLocationPut.class
    );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(ID, PARENT, NAME, PARENT_DETACH);
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    final var client =
      this.client();

    final var locationID =
      context.parameterValue(ID);
    final var newParent =
      context.parameterValue(PARENT);
    final var detach =
      context.parameterValue(PARENT_DETACH)
        .orElse(Boolean.FALSE);

    if (detach.booleanValue() && newParent.isPresent()) {
      throw new CAClientException(
        this.strings()
          .format(
            ERROR_SHELL_OPTIONS_COMBINATION,
            Set.of(PARENT.name(), PARENT_DETACH.name())
          ),
        errorApiMisuse(),
        Map.of(),
        Optional.empty(),
        Optional.empty()
      );
    }

    final CALocation location;
    try {
      location =
        client.sendAndWaitOrThrow(
          new CAICommandLocationGet(locationID),
          this.commandTimeout()
        ).data();
    } catch (final CAClientException e) {
      if (Objects.equals(e.errorCode(), errorNonexistent())) {
        return this.createNewLocation(context);
      }
      throw e;
    }

    return this.modifyExistingLocation(context, location);
  }

  private QCommandStatus modifyExistingLocation(
    final QCommandContextType context,
    final CALocation location)
    throws Exception
  {
    var newLocation = location;

    final var newName =
      context.parameterValue(NAME);

    if (newName.isPresent()) {
      newLocation = new CALocation(
        newLocation.id(),
        newLocation.parent(),
        newName.get(),
        newLocation.metadata(),
        newLocation.attachments(),
        newLocation.types()
      );
    }

    final var newParent =
      context.parameterValue(PARENT);

    if (newParent.isPresent()) {
      newLocation = new CALocation(
        newLocation.id(),
        newParent,
        newLocation.name(),
        newLocation.metadata(),
        newLocation.attachments(),
        newLocation.types()
      );
    }

    final var detach =
      context.parameterValue(PARENT_DETACH)
        .orElse(Boolean.FALSE);

    if (detach.booleanValue()) {
      newLocation = new CALocation(
        newLocation.id(),
        Optional.empty(),
        newLocation.name(),
        newLocation.metadata(),
        newLocation.attachments(),
        newLocation.types()
      );
    }

    final var client =
      this.client();
    final var result =
      client.sendAndWaitOrThrow(
        new CAICommandLocationPut(newLocation),
        this.commandTimeout()
      );

    this.formatter().formatLocation(result.data());
    return SUCCESS;
  }

  private QCommandStatus createNewLocation(
    final QCommandContextType context)
    throws Exception
  {
    final var locationID =
      context.parameterValue(ID);
    final var newName =
      context.parameterValue(NAME);
    final var newParent =
      context.parameterValue(PARENT);

    final var newLocation =
      new CALocation(
        locationID,
        newParent,
        newName.orElse(""),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    final var client =
      this.client();
    final var result =
      client.sendAndWaitOrThrow(
        new CAICommandLocationPut(newLocation),
        this.commandTimeout()
      );

    this.formatter().formatLocation(result.data());
    return SUCCESS;
  }
}
