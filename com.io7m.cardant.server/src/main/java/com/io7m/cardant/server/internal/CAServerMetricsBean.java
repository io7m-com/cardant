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

package com.io7m.cardant.server.internal;

/**
 * The server metrics bean.
 */

public final class CAServerMetricsBean implements CAServerMetricsMXBean
{
  private volatile long databaseTransactionsCreated;
  private volatile long databaseTransactionsCommitted;
  private volatile long serverCommandsExecuted;
  private volatile long serverCommandsFailed;
  private volatile long serverLoginsFailed;
  private volatile long serverLoginsSucceeded;

  /**
   * The server metrics bean.
   */

  public CAServerMetricsBean()
  {

  }

  /**
   * Increment the number of created transactions.
   */

  public void databaseTransactionCreated()
  {
    ++this.databaseTransactionsCreated;
  }

  /**
   * Increment the number of committed transactions.
   */

  public void databaseTransactionCommitted()
  {
    ++this.databaseTransactionsCommitted;
  }

  @Override
  public long getDatabaseTransactionsCreated()
  {
    return this.databaseTransactionsCreated;
  }

  @Override
  public long getDatabaseTransactionsCommitted()
  {
    return this.databaseTransactionsCommitted;
  }

  @Override
  public long getServerCommandsExecuted()
  {
    return this.serverCommandsExecuted;
  }

  @Override
  public long getServerCommandsFailed()
  {
    return this.serverCommandsFailed;
  }

  @Override
  public long getServerLoginsFailed()
  {
    return this.serverLoginsFailed;
  }

  @Override
  public long getServerLoginsSuceeded()
  {
    return this.serverLoginsSucceeded;
  }

  /**
   * Increment the number of executed commands.
   */

  public void serverCommandExecuted()
  {
    ++this.serverCommandsExecuted;
  }

  /**
   * Increment the number of failed commands.
   */

  public void serverCommandFailed()
  {
    ++this.serverCommandsFailed;
  }

  /**
   * Increment the number of failed logins.
   */

  public void serverLoginFailed()
  {
    ++this.serverLoginsFailed;
  }

  /**
   * Increment the number of succeeded logins.
   */

  public void serverLoginSucceeded()
  {
    ++this.serverLoginsSucceeded;
  }
}
