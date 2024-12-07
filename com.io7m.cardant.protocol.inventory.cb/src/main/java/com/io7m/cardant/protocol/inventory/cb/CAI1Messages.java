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


package com.io7m.cardant.protocol.inventory.cb;

import com.io7m.cardant.error_codes.CAErrorCode;
import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.api.CAProtocolMessagesType;
import com.io7m.cardant.protocol.inventory.CAICommandDebugInvalid;
import com.io7m.cardant.protocol.inventory.CAICommandDebugRandom;
import com.io7m.cardant.protocol.inventory.CAIMessageType;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.protocol.inventory.CAITransactionResponse;
import com.io7m.cedarbridge.runtime.api.CBProtocolMessageVersionedSerializerType;
import com.io7m.cedarbridge.runtime.api.CBSerializationContextType;
import com.io7m.cedarbridge.runtime.bssio.CBSerializationContextBSSIO;
import com.io7m.jbssio.api.BSSReaderProviderType;
import com.io7m.jbssio.api.BSSWriterProviderType;
import com.io7m.jbssio.vanilla.BSSReaders;
import com.io7m.jbssio.vanilla.BSSWriters;
import com.io7m.repetoir.core.RPServiceType;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorIo;
import static com.io7m.cardant.protocol.inventory.CAIResponseBlame.BLAME_CLIENT;

/**
 * The protocol messages for Inventory Cedarbridge.
 */

public final class CAI1Messages
  implements CAProtocolMessagesType<CAIMessageType>, RPServiceType
{
  private static final ProtocolCAI PROTOCOL = new ProtocolCAI();

  /**
   * The content type for the protocol.
   */

  public static final String CONTENT_TYPE =
    "application/cardant_inventory+cedarbridge";

  /**
   * The content type for the protocol.
   */

  public static final String CONTENT_TYPE_FOR_SEQUENCE =
    "application/cardant_inventory_sequence+cedarbridge";

  private final BSSReaderProviderType readers;
  private final BSSWriterProviderType writers;
  private final CAI1Validation validator;
  private final CBProtocolMessageVersionedSerializerType<ProtocolCAIType> serializer;

  /**
   * The protocol messages for Admin v1 Cedarbridge.
   *
   * @param inReaders The readers
   * @param inWriters The writers
   */

  public CAI1Messages(
    final BSSReaderProviderType inReaders,
    final BSSWriterProviderType inWriters)
  {
    this.readers =
      Objects.requireNonNull(inReaders, "readers");
    this.writers =
      Objects.requireNonNull(inWriters, "writers");

    this.validator = new CAI1Validation();
    this.serializer =
      PROTOCOL.serializerForProtocolVersion(1L)
        .orElseThrow(() -> {
          return new IllegalStateException("No support for version 1");
        });
  }

  /**
   * The protocol messages for Inventory v1 Cedarbridge.
   */

  public CAI1Messages()
  {
    this(new BSSReaders(), new BSSWriters());
  }

  /**
   * @return The content type
   */

  public static String contentType()
  {
    return CONTENT_TYPE;
  }

  /**
   * @return The content type for sequences
   */

  public static String contentTypeForSequence()
  {
    return CONTENT_TYPE_FOR_SEQUENCE;
  }

  /**
   * @return The protocol identifier
   */

  public static UUID protocolId()
  {
    return PROTOCOL.protocolId();
  }

  @Override
  public CAIMessageType parse(
    final byte[] data)
    throws CAProtocolException
  {
    final var context =
      CBSerializationContextBSSIO.createFromByteArray(this.readers, data);

    try {
      return this.validator.convertFromWire(
        (ProtocolCAIv1Type) this.serializer.deserialize(context)
      );
    } catch (final IOException e) {
      throw new CAProtocolException(
        e.getMessage(),
        e,
        errorIo(),
        Collections.emptySortedMap(),
        Optional.empty()
      );
    }
  }

  @Override
  public byte[] serialize(
    final CAIMessageType message)
  {
    try (var output = new ByteArrayOutputStream()) {
      final var context =
        CBSerializationContextBSSIO.createFromOutputStream(
          this.writers,
          output
        );

      switch (message) {
        case final CAICommandDebugInvalid ignored -> {
          this.serializeInvalid(context);
        }

        case final CAICommandDebugRandom ignored -> {
          serializeRandom(output);
        }

        case final CAITransactionResponse transactionResponse -> {
          this.serializeTransactionResponse(output, transactionResponse);
        }

        case null, default -> {
          this.serializer.serialize(
            context,
            this.validator.convertToWire(message)
          );
        }
      }

      return output.toByteArray();
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    } catch (final CAProtocolException | NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  private void serializeTransactionResponse(
    final OutputStream outputStream,
    final CAITransactionResponse transactionResponse)
    throws IOException, CAProtocolException
  {
    try (var dataOut = new DataOutputStream(outputStream)) {
      for (final CAIResponseType message : transactionResponse.responses()) {
        final var output =
          new ByteArrayOutputStream();
        final var context =
          CBSerializationContextBSSIO.createFromOutputStream(
            this.writers,
            output
          );

        this.serializer.serialize(
          context,
          this.validator.convertToWire(message)
        );

        final var data = output.toByteArray();
        dataOut.writeInt(data.length);
        dataOut.write(data);
      }

      dataOut.writeInt(0);
    }
  }

  private static void serializeRandom(
    final ByteArrayOutputStream outputStream)
    throws NoSuchAlgorithmException
  {
    final var random = SecureRandom.getInstanceStrong();
    final var data = new byte[1024];
    random.nextBytes(data);
    outputStream.writeBytes(data);
  }

  private void serializeInvalid(
    final CBSerializationContextType context)
    throws IOException, CAProtocolException
  {
    this.serializer.serialize(
      context,
      this.validator.convertToWire(new CAIResponseError(
        UUID.randomUUID(),
        "Invalid!",
        new CAErrorCode("error-invalid"),
        Map.of("X", "Y"),
        Optional.of("Avoid sending this."),
        Optional.empty(),
        BLAME_CLIENT,
        List.of()
      ))
    );
  }

  @Override
  public String description()
  {
    return "Inventory Cedarbridge message service.";
  }

  @Override
  public String toString()
  {
    return "[CAIMessages 0x%s]"
      .formatted(Long.toUnsignedString(this.hashCode(), 16));
  }
}
