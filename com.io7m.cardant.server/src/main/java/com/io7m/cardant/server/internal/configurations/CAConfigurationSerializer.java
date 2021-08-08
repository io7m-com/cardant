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

package com.io7m.cardant.server.internal.configurations;

import com.io7m.cardant.server.api.CAServerConfiguration;
import com.io7m.cardant.server.api.CAServerDatabaseLocalConfiguration;
import com.io7m.cardant.server.api.CAServerDatabaseRemoteConfiguration;
import com.io7m.cardant.server.internal.CASchemas;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.OptionalLong;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * A configuration serializer.
 */

public final class CAConfigurationSerializer
{
  private static final String CONFIG_NAMESPACE =
    CASchemas.configuration1Namespace().toString();

  private static final byte[] XML_DECLARATION =
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>".getBytes(UTF_8);

  private final XMLStreamWriter writer;
  private final ByteArrayOutputStream bufferedOutput;
  private final OutputStream stream;
  private final Transformer transformer;
  private final CAServerConfiguration configuration;

  /**
   * Construct a serializer.
   *
   * @param inWriter         The XML writer
   * @param inBufferedOutput The output stream
   * @param inStream         The output stream
   * @param inTransformer    The transformer
   * @param inConfiguration  The configuration
   */

  public CAConfigurationSerializer(
    final XMLStreamWriter inWriter,
    final ByteArrayOutputStream inBufferedOutput,
    final OutputStream inStream,
    final Transformer inTransformer,
    final CAServerConfiguration inConfiguration)
  {
    this.writer =
      Objects.requireNonNull(inWriter, "writer");
    this.bufferedOutput =
      Objects.requireNonNull(inBufferedOutput, "bufferedOutput");
    this.stream =
      Objects.requireNonNull(inStream, "stream");
    this.transformer =
      Objects.requireNonNull(inTransformer, "transformer");
    this.configuration =
      Objects.requireNonNull(inConfiguration, "configuration");
  }

  /**
   * Write the value.
   *
   * @throws IOException On errors
   */

  public void write()
    throws IOException
  {
    try {
      this.start();
      this.serializeDatabase();
      this.serializeHTTP();
      this.serializeLimits();
      this.finish();
    } catch (final XMLStreamException | TransformerException e) {
      throw new IOException(e);
    }
  }

  private void serializeLimits()
    throws XMLStreamException
  {
    final var limits = this.configuration.limits();
    this.writer.writeStartElement(CONFIG_NAMESPACE, "Limits");
    this.writeLimitOptional(
      limits.itemAttachmentMaximumSizeOctets(),
      "itemAttachmentMaximumSizeOctets"
    );
    this.writer.writeEndElement();
  }

  private void writeLimitOptional(
    final OptionalLong option,
    final String name)
    throws XMLStreamException
  {
    if (option.isPresent()) {
      final var limit = option.getAsLong();
      this.writer.writeAttribute(name, Long.toUnsignedString(limit));
    }
  }

  private void serializeHTTP()
    throws XMLStreamException
  {
    final var http = this.configuration.http();
    this.writer.writeStartElement(CONFIG_NAMESPACE, "HTTP");
    this.writer.writeAttribute(
      "sessionDirectory",
      http.sessionDirectory().toString());
    this.writer.writeAttribute("port", String.valueOf(http.port()));
    this.writer.writeEndElement();
  }

  private void serializeDatabase()
    throws XMLStreamException
  {
    final var database = this.configuration.database();
    if (database instanceof CAServerDatabaseLocalConfiguration local) {
      this.writer.writeStartElement(CONFIG_NAMESPACE, "DatabaseLocal");
      this.writer.writeAttribute("file", local.file().toString());
      this.writer.writeAttribute("create", String.valueOf(local.create()));
      this.writer.writeEndElement();
      return;
    }

    if (database instanceof CAServerDatabaseRemoteConfiguration remote) {
      this.writer.writeStartElement(CONFIG_NAMESPACE, "DatabaseRemote");
      this.writer.writeAttribute("host", remote.host());
      this.writer.writeAttribute("port", String.valueOf(remote.port()));
      this.writer.writeEndElement();
      return;
    }
    throw new IllegalStateException();
  }

  private void start()
    throws XMLStreamException
  {
    this.writer.writeStartDocument("UTF-8", "1.0");
    this.writer.writeCharacters("\n");
    this.writer.setPrefix("ca", CONFIG_NAMESPACE);
    this.writer.writeStartElement(CONFIG_NAMESPACE, "Configuration");
    this.writer.writeNamespace("ca", CONFIG_NAMESPACE);
  }

  private void finish()
    throws XMLStreamException, IOException, TransformerException
  {
    this.writer.flush();
    this.writer.writeEndElement();
    this.writer.writeEndDocument();

    final var source =
      new StreamSource(
        new ByteArrayInputStream(this.bufferedOutput.toByteArray())
      );
    final var result =
      new StreamResult(this.stream);

    this.stream.write(XML_DECLARATION);
    this.stream.write(System.lineSeparator().getBytes(UTF_8));
    this.transformer.transform(source, result);
    this.stream.flush();
    this.stream.close();
  }
}
