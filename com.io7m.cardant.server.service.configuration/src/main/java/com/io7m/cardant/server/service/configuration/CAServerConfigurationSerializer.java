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


package com.io7m.cardant.server.service.configuration;

import com.io7m.anethum.api.SerializationException;
import com.io7m.cardant.server.api.CAServerConfigurationFile;
import com.io7m.cardant.server.api.CAServerDatabaseConfiguration;
import com.io7m.cardant.server.api.CAServerHTTPServiceConfiguration;
import com.io7m.cardant.server.api.CAServerIdstoreConfiguration;
import com.io7m.cardant.server.api.CAServerLimitsConfiguration;
import com.io7m.cardant.server.api.CAServerOpenTelemetryConfiguration;
import com.io7m.cardant.tls.CATLSConfigurationType;
import com.io7m.cardant.tls.CATLSDisabled;
import com.io7m.cardant.tls.CATLSEnabled;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;

import static java.lang.Integer.toUnsignedString;

final class CAServerConfigurationSerializer
  implements CAServerConfigurationSerializerType
{
  private final OutputStream stream;
  private final XMLStreamWriter output;

  CAServerConfigurationSerializer(
    final URI inTarget,
    final OutputStream inStream)
  {
    Objects.requireNonNull(inTarget, "target");

    this.stream =
      Objects.requireNonNull(inStream, "stream");

    try {
      this.output =
        XMLOutputFactory.newFactory()
          .createXMLStreamWriter(this.stream, "UTF-8");
    } catch (final XMLStreamException e) {
      throw new IllegalStateException(e);
    }
  }

  private static String findNS()
  {
    return CAServerConfigurationSchemas.schema1().namespace().toString();
  }

  @Override
  public String toString()
  {
    return "[CAServerConfigurationSerializer 0x%x]"
      .formatted(Integer.valueOf(this.hashCode()));
  }

  @Override
  public void execute(
    final CAServerConfigurationFile value)
    throws SerializationException
  {
    try {
      this.output.writeStartDocument("UTF-8", "1.0");
      this.serializeFile(value);
      this.output.writeEndDocument();
    } catch (final XMLStreamException e) {
      throw new SerializationException(e.getMessage(), e);
    }
  }

  private void serializeFile(
    final CAServerConfigurationFile value)
    throws XMLStreamException
  {
    this.output.writeStartElement("Configuration");
    this.output.writeDefaultNamespace(findNS());
    this.output.writeNamespace("tls", findTLSNS());

    this.serializeInventoryService(value.inventoryService());
    this.serializeDatabase(value.databaseConfiguration());
    this.serializeIdstore(value.idstoreConfiguration());
    this.serializeLimits(value.limitsConfiguration());
    this.serializeOpenTelemetryOpt(value.openTelemetry());

    this.output.writeEndElement();
  }

  private void serializeLimits(
    final CAServerLimitsConfiguration c)
    throws XMLStreamException
  {
    this.output.writeStartElement("Limits");
    this.output.writeAttribute(
      "MaximumCommandSizeOctets",
      Long.toUnsignedString(c.maximumCommandSizeOctets())
    );
    this.output.writeAttribute(
      "MaximumFileUploadSizeOctets",
      Long.toUnsignedString(c.maximumFileUploadSizeOctets())
    );
    this.output.writeEndElement();
  }

  private void serializeIdstore(
    final CAServerIdstoreConfiguration c)
    throws XMLStreamException
  {
    this.output.writeStartElement("Idstore");
    this.output.writeAttribute("BaseURI", c.baseURI().toString());
    this.output.writeAttribute(
      "PasswordResetURI",
      c.passwordResetURI().toString());
    this.output.writeEndElement();
  }

  private void serializeInventoryService(
    final CAServerHTTPServiceConfiguration c)
    throws XMLStreamException
  {
    this.output.writeStartElement("InventoryService");
    this.output.writeAttribute("ListenAddress", c.listenAddress());
    this.output.writeAttribute("ListenPort", toUnsignedString(c.listenPort()));
    this.output.writeAttribute("ExternalAddress", c.externalAddress().toString());
    this.serializeTLS(c.tlsConfiguration());
    this.output.writeEndElement();
  }

  private void serializeTLS(
    final CATLSConfigurationType c)
    throws XMLStreamException
  {
    final var tlsNs = findTLSNS();

    switch (c) {
      case final CATLSDisabled ignored -> {
        this.output.writeStartElement("tls", "TLSDisabled", tlsNs);
        this.output.writeEndElement();
      }
      case final CATLSEnabled e -> {
        this.output.writeStartElement("tls", "TLSEnabled", tlsNs);

        final var ks = e.keyStore();
        this.output.writeStartElement("tls", "KeyStore", tlsNs);
        this.output.writeAttribute("Type", ks.storeType());
        this.output.writeAttribute("Provider", ks.storeProvider());
        this.output.writeAttribute("Password", ks.storePassword());
        this.output.writeAttribute("File", ks.storePath().toString());
        this.output.writeEndElement();

        final var ts = e.trustStore();
        this.output.writeStartElement("tls", "TrustStore", tlsNs);
        this.output.writeAttribute("Type", ts.storeType());
        this.output.writeAttribute("Provider", ts.storeProvider());
        this.output.writeAttribute("Password", ts.storePassword());
        this.output.writeAttribute("File", ts.storePath().toString());
        this.output.writeEndElement();

        this.output.writeEndElement();
      }
    }
  }

  private static String findTLSNS()
  {
    return CAServerConfigurationSchemas.tls1().namespace().toString();
  }

  private void serializeOpenTelemetryOpt(
    final Optional<CAServerOpenTelemetryConfiguration> c)
    throws XMLStreamException
  {
    if (c.isPresent()) {
      this.serializeOpenTelemetry(c.get());
    }
  }

  private void serializeOpenTelemetry(
    final CAServerOpenTelemetryConfiguration c)
    throws XMLStreamException
  {
    this.output.writeStartElement("OpenTelemetry");
    this.output.writeAttribute("LogicalServiceName", c.logicalServiceName());

    if (c.logs().isPresent()) {
      final var e = c.logs().get();
      this.output.writeStartElement("Logs");
      this.output.writeAttribute("Endpoint", e.endpoint().toString());
      this.output.writeAttribute("Protocol", e.protocol().toString());
      this.output.writeEndElement();
    }

    if (c.metrics().isPresent()) {
      final var e = c.metrics().get();
      this.output.writeStartElement("Metrics");
      this.output.writeAttribute("Endpoint", e.endpoint().toString());
      this.output.writeAttribute("Protocol", e.protocol().toString());
      this.output.writeEndElement();
    }

    if (c.traces().isPresent()) {
      final var e = c.traces().get();
      this.output.writeStartElement("Traces");
      this.output.writeAttribute("Endpoint", e.endpoint().toString());
      this.output.writeAttribute("Protocol", e.protocol().toString());
      this.output.writeEndElement();
    }

    this.output.writeEndElement();
  }

  private void serializeDatabase(
    final CAServerDatabaseConfiguration c)
    throws XMLStreamException
  {
    this.output.writeStartElement("Database");
    this.output.writeAttribute(
      "OwnerRoleName",
      c.ownerRoleName()
    );
    this.output.writeAttribute(
      "OwnerRolePassword",
      c.ownerRolePassword()
    );
    this.output.writeAttribute(
      "WorkerRolePassword",
      c.workerRolePassword()
    );

    if (c.readerRolePassword().isPresent()) {
      final var r = c.readerRolePassword().get();
      this.output.writeAttribute("ReaderRolePassword", r);
    }

    this.output.writeAttribute(
      "Kind",
      c.kind().name()
    );
    this.output.writeAttribute(
      "Name",
      c.databaseName()
    );
    this.output.writeAttribute(
      "Address",
      c.address()
    );
    this.output.writeAttribute(
      "Port",
      toUnsignedString(c.port())
    );
    this.output.writeAttribute(
      "Create",
      Boolean.toString(c.create())
    );
    this.output.writeAttribute(
      "Upgrade",
      Boolean.toString(c.upgrade())
    );
    this.output.writeEndElement();
  }

  @Override
  public void close()
    throws IOException
  {
    this.stream.close();
  }
}
