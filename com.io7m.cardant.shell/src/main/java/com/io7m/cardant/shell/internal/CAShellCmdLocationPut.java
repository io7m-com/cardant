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
import com.io7m.cardant.client.api.CAClientSynchronousType;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.protocol.inventory.CAICommandLocationGet;
import com.io7m.cardant.protocol.inventory.CAICommandLocationPut;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationGet;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationPut;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed01;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
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

  private static final QParameterNamed01<String> DESCRIPTION =
    new QParameterNamed01<>(
      "--description",
      List.of(),
      new QConstant("The location description."),
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
    return List.of(ID, PARENT, NAME, DESCRIPTION, PARENT_DETACH);
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
    final var newName =
      context.parameterValue(NAME);
    final var newDescription =
      context.parameterValue(DESCRIPTION);
    final var newParent =
      context.parameterValue(PARENT);
    final var detach =
      context.parameterValue(PARENT_DETACH)
        .orElse(Boolean.FALSE);

    final var existing =
      fetchExisting(client, locationID);

    final CALocation toPut;
    if (existing.isPresent()) {
      toPut = delta(existing.get(), newName, newDescription, newParent, detach);
    } else {
      toPut = initial(locationID, newName, newDescription, newParent);
    }

    client.executeOrElseThrow(
      new CAICommandLocationPut(toPut),
      CAClientException::ofError
    );
    return SUCCESS;
  }

  private static CALocation initial(
    final CALocationID locationID,
    final Optional<String> newName,
    final Optional<String> newDescription,
    final Optional<CALocationID> newParent)
  {
    return new CALocation(
      locationID,
      newParent,
      newName.orElse(""),
      newDescription.orElse("")
    );
  }

  private static CALocation delta(
    final CALocation existing,
    final Optional<String> newName,
    final Optional<String> newDescription,
    final Optional<CALocationID> newParent,
    final Boolean detach)
  {
    if (detach.booleanValue()) {
      return new CALocation(
        existing.id(),
        Optional.empty(),
        newName.orElseGet(existing::name),
        newDescription.orElseGet(existing::description)
      );
    }

    return new CALocation(
      existing.id(),
      newParent.or(existing::parent),
      newName.orElseGet(existing::name),
      newDescription.orElseGet(existing::description)
    );
  }

  private static Optional<CALocation> fetchExisting(
    final CAClientSynchronousType client,
    final CALocationID locationID)
    throws CAClientException, InterruptedException
  {
    try {
      return Optional.of(
        ((CAIResponseLocationGet) client.executeOrElseThrow(
          new CAICommandLocationGet(locationID),
          CAClientException::ofError
        )).data()
      );
    } catch (final CAClientException e) {
      if (Objects.equals(e.errorCode(), errorNonexistent())) {
        return Optional.empty();
      }
      throw e;
    }
  }
}
