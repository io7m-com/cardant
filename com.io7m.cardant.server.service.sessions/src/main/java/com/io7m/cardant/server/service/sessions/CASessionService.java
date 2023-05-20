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

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.io7m.cardant.model.CAUser;
import com.io7m.idstore.model.IdName;
import com.io7m.jaffirm.core.Preconditions;
import com.io7m.medrina.api.MSubject;
import com.io7m.repetoir.core.RPServiceType;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.ObservableLongMeasurement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

/**
 * A service to create and manage sessions.
 */

public final class CASessionService implements RPServiceType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CASessionService.class);

  private final ObservableLongMeasurement sessionsGauge;
  private final Cache<CASessionSecretIdentifier, CASession> sessions;
  private final ConcurrentMap<CASessionSecretIdentifier, CASession> sessionsMap;

  /**
   * A service to create and manage sessions.
   *
   * @param inTelemetry  The telemetry service
   * @param inExpiration The session expiration time
   * @param type         The session type
   */

  public CASessionService(
    final OpenTelemetry inTelemetry,
    final Duration inExpiration,
    final String type)
  {
    this.sessions =
      Caffeine.newBuilder()
        .expireAfterAccess(inExpiration)
        .<CASessionSecretIdentifier, CASession>evictionListener(
          (key, val, removalCause) -> this.onSessionRemoved(removalCause))
        .build();

    this.sessionsMap =
      this.sessions.asMap();

    final var meter =
      inTelemetry.meterBuilder(CASessionService.class.getCanonicalName())
        .build();

    this.sessionsGauge =
      meter.gaugeBuilder("cardant.active%sSessions".formatted(type))
        .setDescription("Active %s sessions.".formatted(type))
        .ofLongs()
        .buildObserver();
  }

  private void onSessionRemoved(
    final RemovalCause removalCause)
  {
    final var sizeNow = this.sessions.estimatedSize();
    LOG.debug(
      "delete session ({}) ({} now active)",
      removalCause,
      Long.toUnsignedString(sizeNow)
    );
    this.sessionsGauge.record(sizeNow);
  }

  /**
   * Find a session with the given identifier.
   *
   * @param id The identifier
   *
   * @return The session
   */

  public Optional<CASession> findSession(
    final CASessionSecretIdentifier id)
  {
    return Optional.ofNullable(
      this.sessionsMap.get(Objects.requireNonNull(id, "id"))
    );
  }

  /**
   * Create a new session.
   *
   * @param userId  The user ID
   * @param name    The user's latest username
   * @param subject The user subject
   *
   * @return A new session
   */

  public CASession createSession(
    final UUID userId,
    final IdName name,
    final MSubject subject)
  {
    Objects.requireNonNull(userId, "userId");
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(subject, "subject");

    final var id =
      CASessionSecretIdentifier.generate();

    Preconditions.checkPreconditionV(
      !this.sessionsMap.containsKey(id),
      "Session ID cannot already have been used."
    );

    LOG.debug("{} create session", id.value());
    final var session = new CASession(id, new CAUser(userId, name, subject));
    this.sessions.put(id, session);
    this.sessionsGauge.record(this.sessions.estimatedSize());
    return session;
  }

  /**
   * Delete an existing session.
   *
   * @param id The session ID
   */

  public void deleteSession(
    final CASessionSecretIdentifier id)
  {
    Objects.requireNonNull(id, "id");

    this.sessions.invalidate(id);
    this.sessionsGauge.record(this.sessions.estimatedSize());
  }

  @Override
  public String description()
  {
    return "Session service.";
  }

  @Override
  public String toString()
  {
    return "[CASessionService 0x%s]"
      .formatted(Long.toUnsignedString(this.hashCode(), 16));
  }
}
