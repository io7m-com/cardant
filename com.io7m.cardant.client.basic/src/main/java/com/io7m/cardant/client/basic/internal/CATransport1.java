/*
 * Copyright © 2024 Mark Raynsford <code@io7m.com> https://www.io7m.com
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


package com.io7m.cardant.client.basic.internal;

import com.io7m.cardant.client.api.CAClientException;
import com.io7m.cardant.client.api.CAClientTransferStatistics;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.inventory.CAICommandLogin;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIMessageType;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.protocol.inventory.cb.CAI1Messages;
import com.io7m.cardant.strings.CAStringConstants;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.hibiscus.api.HBReadNothing;
import com.io7m.hibiscus.api.HBReadResponse;
import com.io7m.hibiscus.api.HBReadType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.channels.ClosedChannelException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.io7m.cardant.client.basic.internal.CACompression.decompressResponse;
import static com.io7m.cardant.client.basic.internal.CAUUIDs.nullUUID;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorIo;
import static com.io7m.cardant.protocol.inventory.CAIResponseBlame.BLAME_CLIENT;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_EXPECTED_COMMAND_TYPE;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_HASH_VALUE_MISMATCH;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_UNEXPECTED_CONTENT_TYPE;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_UNEXPECTED_RESPONSE_TYPE;
import static com.io7m.cardant.strings.CAStringConstants.EXPECTED_CONTENT_TYPE;
import static com.io7m.cardant.strings.CAStringConstants.EXPECTED_HASH;
import static com.io7m.cardant.strings.CAStringConstants.EXPECTED_RESPONSE_TYPE;
import static com.io7m.cardant.strings.CAStringConstants.HASH_ALGORITHM;
import static com.io7m.cardant.strings.CAStringConstants.RECEIVED_CONTENT_TYPE;
import static com.io7m.cardant.strings.CAStringConstants.RECEIVED_HASH;
import static com.io7m.cardant.strings.CAStringConstants.RECEIVED_RESPONSE_TYPE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

/**
 * The version 1 transport.
 */

public final class CATransport1
  implements CATransportType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CATransport1.class);

  private final CAI1Messages messages;
  private final CAStrings strings;
  private final Clock clock;
  private final HttpClient http;
  private final LinkedBlockingQueue<MessageAndResponse> inbox;
  private final URI commandURI;
  private final URI fileDownloadURI;
  private final URI fileUploadURI;
  private final URI loginURI;
  private final URI transactionURI;

  private record MessageAndResponse(
    CAIMessageType sent,
    CAIMessageType received)
  {

  }

  /**
   * The version 1 transport.
   *
   * @param inClock      The clock
   * @param inStrings    The string resources
   * @param inHttpClient The HTTP client
   * @param baseURI      The base URI
   */

  public CATransport1(
    final Clock inClock,
    final CAStrings inStrings,
    final HttpClient inHttpClient,
    final URI baseURI)
  {
    this.clock =
      Objects.requireNonNull(inClock, "inClock");
    this.http =
      Objects.requireNonNull(inHttpClient, "inHttpClient");
    this.strings =
      Objects.requireNonNull(inStrings, "inStrings");

    this.inbox =
      new LinkedBlockingQueue<>();

    this.messages =
      new CAI1Messages();
    this.loginURI =
      baseURI.resolve("login")
        .normalize();
    this.commandURI =
      baseURI.resolve("command")
        .normalize();
    this.fileUploadURI =
      baseURI.resolve("file-upload")
        .normalize();
    this.fileDownloadURI =
      baseURI.resolve("file-download")
        .normalize();
    this.transactionURI =
      baseURI.resolve("transaction")
        .normalize();
  }

  private CAClientException errorClosed()
  {
    return new CAClientException(
      this.strings.format(CAStringConstants.ERROR_CLOSED_CHANNEL),
      new ClosedChannelException(),
      CAStandardErrorCodes.errorApiMisuse(),
      Map.of(),
      Optional.empty(),
      Optional.empty()
    );
  }

  private MessageAndResponse sendMessage(
    final CAIMessageType message,
    final URI targetURI,
    final Optional<Duration> timeout)
    throws
    CAClientException,
    IOException,
    CAProtocolException,
    InterruptedException
  {
    if (message instanceof final CAICommandType<?> command) {
      return this.sendCommand(targetURI, command, timeout);
    } else {
      throw this.errorNotCommand(message);
    }
  }

  private MessageAndResponse sendCommand(
    final URI targetURI,
    final CAICommandType<?> command,
    final Optional<Duration> timeout)
    throws
    IOException,
    InterruptedException,
    CAClientException,
    CAProtocolException
  {
    final var data =
      this.messages.serialize(command);

    final var requestBuilder =
      HttpRequest.newBuilder()
        .uri(targetURI)
        .POST(HttpRequest.BodyPublishers.ofByteArray(data));

    timeout.ifPresent(requestBuilder::timeout);

    final var response =
      this.http.send(
        requestBuilder.build(),
        HttpResponse.BodyHandlers.ofByteArray()
      );

    LOG.debug("Write: Status {}", Integer.valueOf(response.statusCode()));

    final var responseHeaders =
      response.headers();

    /*
     * Check the content type. Fail if it's not what we expected.
     */

    final var contentType =
      responseHeaders.firstValue("content-type")
        .orElse("application/octet-stream");

    final var expectedContentType = CAI1Messages.contentType();
    if (!contentType.equals(expectedContentType)) {
      throw this.errorContentType(contentType, expectedContentType);
    }

    /*
     * Parse the response message, decompressing if necessary. If the
     * parsed message isn't a response... fail.
     */

    final var responseMessage =
      this.messages.parse(decompressResponse(response, responseHeaders));

    if (!(responseMessage instanceof CAIResponseType)) {
      throw this.errorUnexpectedResponseType(command, responseMessage);
    }

    /*
     * If the response is an error, accept it.
     */

    if (responseMessage instanceof final CAIResponseError error) {
      return new MessageAndResponse(command, error);
    }

    /*
     * Otherwise, reject the response if it isn't of the correct type.
     */

    if (!Objects.equals(command.responseClass(), responseMessage.getClass())) {
      throw this.errorUnexpectedResponseType(command, responseMessage);
    }

    return new MessageAndResponse(command, responseMessage);
  }

  @Override
  public boolean isClosed()
  {
    return this.http.isTerminated();
  }

  @Override
  public void close()
  {
    this.http.close();
  }

  private CAClientException errorNotCommand(
    final CAIMessageType message)
  {
    final var attributes = new HashMap<String, String>();
    attributes.put(
      this.strings.format(CAStringConstants.MESSAGE_TYPE),
      message.getClass().getSimpleName()
    );

    return new CAClientException(
      this.strings.format(ERROR_EXPECTED_COMMAND_TYPE),
      CAStandardErrorCodes.errorProtocol(),
      Map.copyOf(attributes),
      Optional.empty(),
      Optional.empty()
    );
  }

  private CAClientException errorContentType(
    final String contentType,
    final String expectedContentType)
  {
    final var attributes = new HashMap<String, String>();
    attributes.put(
      this.strings.format(EXPECTED_CONTENT_TYPE),
      expectedContentType
    );
    attributes.put(
      this.strings.format(RECEIVED_CONTENT_TYPE),
      contentType
    );

    return new CAClientException(
      this.strings.format(ERROR_UNEXPECTED_CONTENT_TYPE),
      CAStandardErrorCodes.errorProtocol(),
      Map.copyOf(attributes),
      Optional.empty(),
      Optional.empty()
    );
  }

  private CAClientException errorUnexpectedResponseType(
    final CAIMessageType message,
    final CAIMessageType responseActual)
  {
    final var attributes = new HashMap<String, String>();
    if (message instanceof final CAICommandType<?> cmd) {
      attributes.put(
        this.strings.format(EXPECTED_RESPONSE_TYPE),
        cmd.responseClass().getSimpleName()
      );
    }

    attributes.put(
      this.strings.format(RECEIVED_RESPONSE_TYPE),
      responseActual.getClass().getSimpleName()
    );

    return new CAClientException(
      this.strings.format(ERROR_UNEXPECTED_RESPONSE_TYPE),
      CAStandardErrorCodes.errorProtocol(),
      Map.copyOf(attributes),
      Optional.empty(),
      Optional.empty()
    );
  }

  @Override
  public HBReadType<CAIMessageType> receive(
    final Duration timeout)
    throws CAClientException, InterruptedException
  {
    Objects.requireNonNull(timeout, "timeout");

    if (this.isClosed()) {
      throw this.errorClosed();
    }

    final var r =
      this.inbox.poll(timeout.toNanos(), TimeUnit.NANOSECONDS);

    if (r == null) {
      return new HBReadNothing<>();
    }

    return new HBReadResponse<>(r.sent(), r.received());
  }

  @Override
  public void send(
    final CAIMessageType message)
    throws CAClientException, InterruptedException
  {
    if (this.isClosed()) {
      throw this.errorClosed();
    }

    try {
      this.inbox.put(
        switch (message) {
          case final CAICommandLogin m -> {
            yield this.sendMessage(
              message,
              this.loginURI,
              Optional.empty()
            );
          }
          default -> {
            yield this.sendMessage(
              message,
              this.commandURI,
              Optional.empty()
            );
          }
        }
      );
    } catch (final IOException | CAProtocolException e) {
      throw CAClientException.ofException(e);
    }
  }

  @Override
  public void sendAndForget(
    final CAIMessageType message)
    throws CAClientException, InterruptedException
  {
    if (this.isClosed()) {
      throw this.errorClosed();
    }

    try {
      final var targetURI =
        switch (message) {
          case final CAICommandLogin ignored -> this.loginURI;
          default -> this.commandURI;
        };

      final var data =
        this.messages.serialize(message);

      final var response =
        this.http.send(
          HttpRequest.newBuilder()
            .uri(targetURI)
            .POST(HttpRequest.BodyPublishers.ofByteArray(data))
            .build(),
          HttpResponse.BodyHandlers.discarding()
        );

      LOG.debug("Send: Status {}", Integer.valueOf(response.statusCode()));
    } catch (final IOException e) {
      throw CAClientException.ofException(e);
    }
  }

  @Override
  public CAIMessageType sendAndWait(
    final CAIMessageType message,
    final Duration timeout)
    throws CAClientException, InterruptedException
  {
    if (this.isClosed()) {
      throw this.errorClosed();
    }

    try {
      return (
        switch (message) {
          case final CAICommandLogin m -> {
            yield this.sendMessage(
              message,
              this.loginURI,
              Optional.of(timeout)
            );
          }
          default -> {
            yield this.sendMessage(
              message,
              this.commandURI,
              Optional.of(timeout)
            );
          }
        }
      ).received();
    } catch (final IOException | CAProtocolException e) {
      throw CAClientException.ofException(e);
    }
  }

  @Override
  public String toString()
  {
    return "[%s 0x%s]".formatted(
      this.getClass().getSimpleName(),
      Integer.toUnsignedString(this.hashCode(), 16)
    );
  }

  @Override
  public Path fileDownload(
    final CAFileID fileID,
    final Path file,
    final Path fileTmp,
    final long size,
    final String hashAlgorithm,
    final String hashValue,
    final Consumer<CAClientTransferStatistics> statistics)
    throws CAClientException
  {
    Objects.requireNonNull(fileID, "fileID");
    Objects.requireNonNull(file, "file");
    Objects.requireNonNull(fileTmp, "fileTmp");
    Objects.requireNonNull(hashAlgorithm, "hashAlgorithm");
    Objects.requireNonNull(hashValue, "hashValue");
    Objects.requireNonNull(statistics, "statistics");

    try {
      final var targetURI =
        URI.create(
          new StringBuilder(128)
            .append(this.fileDownloadURI)
            .append('?')
            .append("FileID=")
            .append(URLEncoder.encode(fileID.displayId(), UTF_8))
            .toString()
        );

      final var request =
        HttpRequest.newBuilder(targetURI)
          .GET()
          .build();

      final var response =
        this.http.send(request, HttpResponse.BodyHandlers.ofInputStream());

      if (response.statusCode() == 200) {
        try (var tracker = new CATransferStatisticsTracker(
          this.clock,
          size,
          statistics)) {
          this.downloadFileAndHash(
            tracker,
            response.body(),
            file,
            fileTmp,
            hashAlgorithm,
            hashValue
          );
          return file;
        }
      }

      final var responseMessage =
        this.messages.parse(
          decompressResponse(
            response.body().readAllBytes(),
            response.headers())
        );

      if (responseMessage instanceof final CAIResponseError error) {
        throw CAClientException.ofError(error);
      }

      throw new CAClientException(
        Integer.toString(response.statusCode()),
        errorIo(),
        Map.of(),
        Optional.empty(),
        Optional.empty()
      );
    } catch (final Exception e) {
      throw CAClientException.ofException(e);
    }
  }

  @Override
  public void fileUpload(
    final CAFileID fileID,
    final Path file,
    final String contentType,
    final String description,
    final Consumer<CAClientTransferStatistics> statistics)
    throws CAClientException
  {
    Objects.requireNonNull(fileID, "fileID");
    Objects.requireNonNull(file, "file");
    Objects.requireNonNull(contentType, "contentType");
    Objects.requireNonNull(description, "description");
    Objects.requireNonNull(statistics, "statistics");

    try {
      final var hash =
        hashOfFile(file);
      final var size =
        Files.size(file);

      final var targetURI =
        URI.create(
          new StringBuilder(128)
            .append(this.fileUploadURI)
            .append('?')
            .append("FileType=")
            .append(URLEncoder.encode(contentType, UTF_8))
            .append("&FileID=")
            .append(URLEncoder.encode(fileID.displayId(), UTF_8))
            .append("&FileDescription=")
            .append(URLEncoder.encode(description, UTF_8))
            .append("&HashAlgorithm=SHA-256")
            .append("&HashValue=")
            .append(URLEncoder.encode(hash, UTF_8))
            .toString()
        );

      try (var tracker =
             new CATransferStatisticsTracker(this.clock, size, statistics)) {
        final var mainPublisher =
          HttpRequest.BodyPublishers.ofFile(file);
        final var statisticsSubscriber =
          new CATransferStatisticsSubscriber(tracker);

        final var request =
          HttpRequest.newBuilder(targetURI)
            .POST(mainPublisher)
            .build();

        mainPublisher.subscribe(statisticsSubscriber);

        final var response =
          this.http.send(request, HttpResponse.BodyHandlers.ofByteArray());

        final var responseMessage =
          this.messages.parse(decompressResponse(response, response.headers()));

        if (responseMessage instanceof final CAIResponseError error) {
          throw CAClientException.ofError(error);
        }

        tracker.completed();
      }
    } catch (final Exception e) {
      throw CAClientException.ofException(e);
    }
  }

  @Override
  public List<CAIResponseType> transaction(
    final List<CAICommandType<?>> commands)
    throws InterruptedException, CAClientException
  {
    Objects.requireNonNull(commands, "commands");

    try (var out = new ByteArrayOutputStream();
         var dataOut = new DataOutputStream(out)) {

      for (final var command : commands) {
        final var data = this.messages.serialize(command);
        dataOut.writeInt(data.length);
        dataOut.write(data);
      }

      dataOut.writeInt(0);
      dataOut.flush();

      final var mainPublisher =
        HttpRequest.BodyPublishers.ofByteArray(out.toByteArray());

      final var request =
        HttpRequest.newBuilder(this.transactionURI)
          .POST(mainPublisher)
          .build();

      final var response =
        this.http.send(request, HttpResponse.BodyHandlers.ofByteArray());

      final var decompressed =
        decompressResponse(response, response.headers());

      final var results = new ArrayList<CAIResponseType>();
      try (var input = new ByteArrayInputStream(decompressed);
           var dataIn = new DataInputStream(input)) {
        while (true) {
          final var size =
            dataIn.readInt();

          if (size == 0) {
            return List.copyOf(results);
          }

          final var section =
            dataIn.readNBytes(size);
          final var message =
            this.messages.parse(section);

          results.add((CAIResponseType) message);
        }
      }
    } catch (final IOException | CAProtocolException e) {
      throw CAClientException.ofException(e);
    }
  }

  private void downloadFileAndHash(
    final CATransferStatisticsTracker tracker,
    final InputStream stream,
    final Path file,
    final Path fileTmp,
    final String hashAlgorithm,
    final String hashValue)
    throws NoSuchAlgorithmException, IOException, CAClientException
  {
    final var options =
      new OpenOption[]{WRITE, TRUNCATE_EXISTING, CREATE};

    final var digest =
      MessageDigest.getInstance(hashAlgorithm);

    try (var output = Files.newOutputStream(fileTmp, options)) {
      try (var hashOut = new DigestOutputStream(output, digest)) {
        final var buffer = new byte[8192];

        while (true) {
          final var r = stream.read(buffer);
          if (r == -1) {
            break;
          }

          tracker.add(Integer.toUnsignedLong(r));
          hashOut.write(buffer, 0, r);
        }

        final var hashResult =
          HexFormat.of()
            .formatHex(digest.digest());

        if (!Objects.equals(hashResult, hashValue)) {
          throw CAClientException.ofError(
            new CAIResponseError(
              nullUUID(),
              this.strings.format(ERROR_HASH_VALUE_MISMATCH),
              errorIo(),
              Map.ofEntries(
                Map.entry(this.strings.format(EXPECTED_HASH), hashValue),
                Map.entry(this.strings.format(RECEIVED_HASH), hashResult),
                Map.entry(this.strings.format(HASH_ALGORITHM), hashAlgorithm)
              ),
              Optional.empty(),
              Optional.empty(),
              BLAME_CLIENT,
              List.of()
            )
          );
        }

        tracker.completed();
        Files.move(fileTmp, file, REPLACE_EXISTING, ATOMIC_MOVE);
      }
    }
  }

  private static String hashOfFile(
    final Path file)
    throws Exception
  {
    final var digest =
      MessageDigest.getInstance("SHA-256");

    try (var nullOut = OutputStream.nullOutputStream()) {
      try (var outputStream = new DigestOutputStream(nullOut, digest)) {
        try (var inputStream = Files.newInputStream(file)) {
          inputStream.transferTo(outputStream);
          return HexFormat.of().formatHex(digest.digest());
        }
      }
    }
  }
}
