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


package com.io7m.cardant.server.service.configuration.v1;

import com.io7m.blackthorne.core.BTElementHandlerConstructorType;
import com.io7m.blackthorne.core.BTElementHandlerType;
import com.io7m.blackthorne.core.BTElementParsingContextType;
import com.io7m.blackthorne.core.BTQualifiedName;
import com.io7m.cardant.server.api.CAServerHTTPServiceConfiguration;
import com.io7m.cardant.tls.CATLSConfigurationType;
import com.io7m.cardant.tls.CATLSDisabled;
import com.io7m.cardant.tls.CATLSEnabled;
import org.xml.sax.Attributes;

import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import static com.io7m.cardant.server.service.configuration.v1.CAC1Names.tlsQName;
import static java.util.Map.entry;

final class CAC1InventoryService
  implements BTElementHandlerType<Object, CAServerHTTPServiceConfiguration>
{
  private String listenAddress;
  private int listenPort;
  private URI externalAddress;
  private CATLSConfigurationType tls;
  private Optional<Duration> sessionExpiration;

  CAC1InventoryService(
    final BTElementParsingContextType context)
  {
    this.sessionExpiration =
      Optional.empty();
  }

  @Override
  public Map<BTQualifiedName, BTElementHandlerConstructorType<?, ?>>
  onChildHandlersRequested(
    final BTElementParsingContextType context)
  {
    return Map.ofEntries(
      entry(tlsQName("TLSEnabled"), CAC1TLSEnabled::new),
      entry(tlsQName("TLSDisabled"), CAC1TLSDisabled::new)
    );
  }

  @Override
  public void onChildValueProduced(
    final BTElementParsingContextType context,
    final Object result)
    throws Exception
  {
    switch (result) {
      case final CATLSEnabled s -> {
        this.tls = s;
      }
      case final CATLSDisabled s -> {
        this.tls = s;
      }
      default -> {
        throw new IllegalArgumentException(
          "Unrecognized element: %s".formatted(result)
        );
      }
    }
  }

  @Override
  public void onElementStart(
    final BTElementParsingContextType context,
    final Attributes attributes)
  {
    this.listenAddress =
      attributes.getValue("ListenAddress");
    this.listenPort =
      Integer.parseUnsignedInt(attributes.getValue("ListenPort"));
    this.externalAddress =
      URI.create(attributes.getValue("ExternalAddress"));
    this.sessionExpiration =
      Optional.ofNullable(attributes.getValue("SessionExpiration"))
        .map(CAC1Durations::parse);
  }

  @Override
  public CAServerHTTPServiceConfiguration onElementFinished(
    final BTElementParsingContextType context)
    throws Exception
  {
    return new CAServerHTTPServiceConfiguration(
      this.listenAddress,
      this.listenPort,
      this.externalAddress,
      this.sessionExpiration,
      this.tls
    );
  }
}
