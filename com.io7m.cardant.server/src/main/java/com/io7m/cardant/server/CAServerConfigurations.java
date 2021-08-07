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

package com.io7m.cardant.server;

import com.io7m.anethum.common.ParseException;
import com.io7m.anethum.common.ParseSeverity;
import com.io7m.anethum.common.ParseStatus;
import com.io7m.anethum.common.SerializeException;
import com.io7m.blackthorne.api.BTException;
import com.io7m.blackthorne.api.BTParseError;
import com.io7m.blackthorne.api.BTParseErrorType;
import com.io7m.blackthorne.jxe.BlackthorneJXE;
import com.io7m.cardant.server.api.CAServerConfiguration;
import com.io7m.cardant.server.api.CAServerConfigurationParserFactoryType;
import com.io7m.cardant.server.api.CAServerConfigurationParserType;
import com.io7m.cardant.server.api.CAServerConfigurationSerializerFactoryType;
import com.io7m.cardant.server.api.CAServerConfigurationSerializerType;
import com.io7m.cardant.server.internal.CAConfigurationParser;
import com.io7m.cardant.server.internal.CAConfigurationSerializer;
import com.io7m.cardant.server.internal.CASchemas;
import com.io7m.jxe.core.JXEXInclude;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.io7m.anethum.common.ParseSeverity.PARSE_ERROR;
import static com.io7m.anethum.common.ParseSeverity.PARSE_WARNING;
import static com.io7m.cardant.server.internal.CASchemas.element1;

/**
 * A provider of server configuration parsers and serializers.
 */

public final class CAServerConfigurations
  implements CAServerConfigurationParserFactoryType,
  CAServerConfigurationSerializerFactoryType
{
  private final XMLOutputFactory serializers;
  private final TransformerFactory transformers;

  /**
   * Construct a provider.
   */

  public CAServerConfigurations()
  {
    this.serializers = XMLOutputFactory.newFactory();
    this.transformers = TransformerFactory.newInstance();
    this.transformers.setAttribute("indent-number", Integer.valueOf(4));
  }

  private static ParseStatus mapError(
    final BTParseError error)
  {
    return ParseStatus.builder()
      .setErrorCode("parseError")
      .setLexical(error.lexical())
      .setMessage(error.message())
      .setSeverity(mapSeverity(error.severity()))
      .build();
  }

  private static ParseSeverity mapSeverity(
    final BTParseErrorType.Severity severity)
  {
    return switch (severity) {
      case WARNING -> PARSE_WARNING;
      case ERROR -> PARSE_ERROR;
    };
  }

  @Override
  public CAServerConfigurationParserType createParserWithContext(
    final FileSystem context,
    final URI source,
    final InputStream stream,
    final Consumer<ParseStatus> statusConsumer)
  {
    return new Parser(context, source, stream);
  }

  @Override
  public CAServerConfigurationSerializerType createSerializerWithContext(
    final FileSystem context,
    final URI target,
    final OutputStream stream)
  {
    return new Serializer(stream);
  }

  private final class Parser
    implements CAServerConfigurationParserType
  {
    private final FileSystem context;
    private final URI source;
    private final InputStream stream;

    Parser(
      final FileSystem inContext,
      final URI inSource,
      final InputStream inStream)
    {
      this.context =
        Objects.requireNonNull(inContext, "context");
      this.source =
        Objects.requireNonNull(inSource, "source");
      this.stream =
        Objects.requireNonNull(inStream, "stream");
    }

    @Override
    public CAServerConfiguration execute()
      throws ParseException
    {
      try {
        return BlackthorneJXE.parse(
          this.source,
          this.stream,
          Map.ofEntries(
            Map.entry(
              element1("Configuration"),
              c -> new CAConfigurationParser(this.context, c)
            )
          ),
          JXEXInclude.XINCLUDE_DISABLED,
          CASchemas.schemas()
        );
      } catch (final BTException e) {
        throw new ParseException(
          e.getMessage(),
          e.errors()
            .stream()
            .map(CAServerConfigurations::mapError)
            .collect(Collectors.toList())
        );
      }
    }

    @Override
    public void close()
      throws IOException
    {
      this.stream.close();
    }
  }

  private final class Serializer
    implements CAServerConfigurationSerializerType
  {
    private final OutputStream stream;

    Serializer(
      final OutputStream inStream)
    {
      this.stream =
        Objects.requireNonNull(inStream, "stream");
    }

    @Override
    public void execute(
      final CAServerConfiguration value)
      throws SerializeException
    {
      try {
        final var transformer =
          CAServerConfigurations.this.transformers.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(
          "{http://xml.apache.org/xslt}indent-amount",
          "4");

        final var bufferedOutput =
          new ByteArrayOutputStream();
        final var writer =
          CAServerConfigurations.this.serializers.createXMLStreamWriter(
            bufferedOutput, "UTF-8");

        final CAConfigurationSerializer serializer =
          new CAConfigurationSerializer(
            writer,
            bufferedOutput,
            this.stream,
            transformer,
            value
          );

        serializer.write();
      } catch (final XMLStreamException | TransformerConfigurationException | IOException e) {
        throw new SerializeException(e.getMessage(), e);
      }
    }

    @Override
    public void close()
      throws IOException
    {
      this.stream.close();
    }
  }
}
