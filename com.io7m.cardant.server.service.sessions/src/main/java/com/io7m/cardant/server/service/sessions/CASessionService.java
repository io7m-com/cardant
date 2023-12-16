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
import com.github.benmanes.caffeine.cache.Scheduler;
import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.server.service.telemetry.api.CAMetricsServiceType;
import com.io7m.idstore.model.IdName;
import com.io7m.jaffirm.core.Preconditions;
import com.io7m.medrina.api.MSubject;
import com.io7m.repetoir.core.RPServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;

import static java.lang.Long.toUnsignedString;

/**
 * A service to create and manage sessions.
 */

public final class CASessionService implements RPServiceType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CASessionService.class);

  private final Cache<CASessionSecretIdentifier, CASession> sessions;
  private final ConcurrentMap<CASessionSecretIdentifier, CASession> sessionsMap;
  private final CAMetricsServiceType metrics;

  /**
   * A service to create and manage sessions.
   *
   * @param inMetrics    The metrics service
   * @param inExpiration The session expiration time
   */

  public CASessionService(
    final CAMetricsServiceType inMetrics,
    final Duration inExpiration)
  {
    this.metrics =
      Objects.requireNonNull(inMetrics, "inMetrics");

    this.sessions =
      Caffeine.newBuilder()
        .expireAfterAccess(inExpiration)
        .scheduler(createScheduler())
        .<CASessionSecretIdentifier, CASession>evictionListener(
          (key, val, removalCause) -> this.onSessionRemoved(removalCause))
        .build();

    this.sessionsMap =
      this.sessions.asMap();
  }

  private static Scheduler createScheduler()
  {
    return Scheduler.forScheduledExecutorService(
      Executors.newSingleThreadScheduledExecutor(r -> {
        final var thread = new Thread(r);
        thread.setDaemon(true);
        thread.setName(
          "com.io7m.cardant.server.service.sessions.CASessionService[%d]"
            .formatted(thread.getId())
        );
        return thread;
      })
    );
  }

  private void onSessionRemoved(
    final RemovalCause removalCause)
  {
    final var sizeNow =
      this.sessions.estimatedSize();
    final var sizeMinus =
      Math.max(0L, sizeNow - 1L);

    LOG.debug(
      "delete session ({}) ({} now active)",
      removalCause,
      toUnsignedString(sizeMinus)
    );

    this.metrics.onLoginClosed(sizeMinus);
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
    final CAUserID userId,
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

    final var session = new CASession(id, new CAUser(userId, name, subject));
    this.sessions.put(id, session);

    final var sizeNow = this.sessions.estimatedSize();
    this.metrics.onLogin(sizeNow);

    if (LOG.isDebugEnabled()) {
      LOG.debug("create session ({} now active)", toUnsignedString(sizeNow));
    }

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
