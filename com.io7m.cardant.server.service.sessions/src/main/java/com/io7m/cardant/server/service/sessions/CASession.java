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

package com.io7m.cardant.server.service.sessions;

import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.model.CAUserID;
import com.io7m.medrina.api.MSubject;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A controller for a single user session.
 */

public final class CASession
{
  private final CASessionSecretIdentifier sessionId;
  private final CAUser user;
  private final ConcurrentHashMap<Class<?>, Object> properties;

  /**
   * A controller for a single user session.
   *
   * @param inSessionId The session ID
   * @param inUser      The user
   */

  public CASession(
    final CASessionSecretIdentifier inSessionId,
    final CAUser inUser)
  {
    this.user =
      Objects.requireNonNull(inUser, "user");
    this.sessionId =
      Objects.requireNonNull(inSessionId, "sessionId");
    this.properties =
      new ConcurrentHashMap<>();
  }

  @Override
  public String toString()
  {
    return "[CASession]";
  }

  /**
   * @return The user subject
   */

  public MSubject subject()
  {
    return this.user.subject();
  }

  /**
   * @return The session identifier
   */

  public CASessionSecretIdentifier id()
  {
    return this.sessionId;
  }

  /**
   * @return The user ID
   */

  public CAUserID userId()
  {
    return this.user.userId();
  }

  /**
   * Set the property.
   *
   * @param clazz The property class
   * @param item  The property value
   * @param <T>   The property type
   */

  public <T> void setProperty(
    final Class<T> clazz,
    final T item)
  {
    this.properties.put(
      Objects.requireNonNull(clazz, "clazz"),
      clazz.cast(Objects.requireNonNull(item, "item"))
    );
  }

  /**
   * Retrieve the value of a property.
   *
   * @param clazz The property class
   * @param <T>   The property type
   *
   * @return The property value, if any
   */

  public <T> Optional<T> property(
    final Class<T> clazz)
  {
    return Optional.ofNullable(this.properties.get(clazz))
      .map(clazz::cast);
  }
}
