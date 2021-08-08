/*
 * Copyright © 2021 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.server.internal.metrics;

import javax.management.MXBean;

/**
 * The type of server metrics.
 */

// CHECKSTYLE:OFF
@MXBean
public interface CAServerMetricsMXBean
{
  // CHECKSTYLE:ON

  /**
   * @return The number of database transactions that have been created
   */

  long getDatabaseTransactionsCreated();

  /**
   * @return The number of database transactions that have been committed
   */

  long getDatabaseTransactionsCommitted();

  /**
   * @return The number of server commands that have been executed
   */

  long getServerCommandsExecuted();

  /**
   * @return The number of server commands that failed to execute
   */

  long getServerCommandsFailed();

  /**
   * @return The number of server logins that failed
   */

  long getServerLoginsFailed();

  /**
   * @return The number of server logins that succeeded
   */

  long getServerLoginsSuceeded();
}
