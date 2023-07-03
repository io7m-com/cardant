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


package com.io7m.cardant.client.basic.internal;

import com.io7m.cardant.client.api.CAClientConfiguration;
import com.io7m.cardant.client.api.CAClientCredentials;
import com.io7m.cardant.client.api.CAClientEventDataUpdated;
import com.io7m.cardant.client.api.CAClientEventType;
import com.io7m.cardant.client.api.CAClientException;
import com.io7m.cardant.client.api.CAClientTransferStatistics;
import com.io7m.cardant.client.api.CAClientUnit;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAVersion;
import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.inventory.CAICommandLogin;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIEventType;
import com.io7m.cardant.protocol.inventory.CAIEventUpdated;
import com.io7m.cardant.protocol.inventory.CAIMessageType;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseLogin;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.protocol.inventory.cb.CAI1Messages;
import com.io7m.cardant.strings.CAStringConstantType;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.hibiscus.api.HBResultFailure;
import com.io7m.hibiscus.api.HBResultSuccess;
import com.io7m.hibiscus.api.HBResultType;
import com.io7m.hibiscus.basic.HBClientNewHandler;
import com.io7m.junreachable.UnreachableCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static com.io7m.cardant.client.api.CAClientUnit.UNIT;
import static com.io7m.cardant.client.basic.internal.CACompression.decompressResponse;
import static com.io7m.cardant.client.basic.internal.CAUUIDs.nullUUID;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorAuthentication;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorIo;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorProtocol;
import static com.io7m.cardant.protocol.inventory.CAIResponseBlame.BLAME_CLIENT;
import static com.io7m.cardant.protocol.inventory.CAIResponseBlame.BLAME_SERVER;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_HASH_VALUE_MISMATCH;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_RECEIVED_UNEXPECTED_CONTENT_TYPE;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_RECEIVED_UNEXPECTED_RESPONSE_TYPE;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_SERVER;
import static com.io7m.cardant.strings.CAStringConstants.EXPECTED_CONTENT_TYPE;
import static com.io7m.cardant.strings.CAStringConstants.EXPECTED_HASH;
import static com.io7m.cardant.strings.CAStringConstants.EXPECTED_RESPONSE_TYPE;
import static com.io7m.cardant.strings.CAStringConstants.HASH_ALGORITHM;
import static com.io7m.cardant.strings.CAStringConstants.HTTP_RESPONSE_CODE;
import static com.io7m.cardant.strings.CAStringConstants.RECEIVED_CONTENT_TYPE;
import static com.io7m.cardant.strings.CAStringConstants.RECEIVED_HASH;
import static com.io7m.cardant.strings.CAStringConstants.RECEIVED_RESPONSE_TYPE;
import static com.io7m.cardant.strings.CAStringConstants.URI;
import static java.lang.Integer.toUnsignedString;
import static java.net.http.HttpResponse.BodyHandlers.ofByteArray;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.Objects.requireNonNullElse;

/**
 * The version 1 protocol handler.
 */

public final class CAHandler1 extends CAHandlerAbstract
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAHandler1.class);

  private final CAI1Messages messages;
  private final URI loginURI;
  private final URI commandURI;
  private final URI fileUploadURI;
  private final URI fileDownloadURI;
  private final URI eventsURI;
  private final LinkedList<CAClientEventType> events;
  private CAICommandLogin mostRecentLogin;

  /**
   * The protocol 1 handler.
   *
   * @param inConfiguration The client configuration
   * @param inStrings       String resources
   * @param inHttpClient    The HTTP client
   * @param baseURI         The base URI returned by the server during version
   *                        negotiation
   */

  CAHandler1(
    final CAClientConfiguration inConfiguration,
    final CAStrings inStrings,
    final HttpClient inHttpClient,
    final URI baseURI)
  {
    super(inConfiguration, inStrings, inHttpClient);

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
    this.eventsURI =
      baseURI.resolve("events")
        .normalize();

    this.events = new LinkedList<>();
  }

  private static boolean isAuthenticationError(
    final CAIResponseError error)
  {
    return Objects.equals(error.errorCode(), errorAuthentication());
  }

  private static String userAgent()
  {
    return "com.io7m.cardant.client/%s".formatted(CAVersion.MAIN_VERSION);
  }

  private HBResultFailure<InputStream, CAIResponseError> fileDataServerError(
    final URI uri,
    final HttpResponse<InputStream> response)
  {
    final var attributes = new HashMap<String, String>();
    attributes.put(
      this.local(HTTP_RESPONSE_CODE),
      Integer.toString(response.statusCode())
    );
    attributes.put(this.local(URI), uri.toString());

    return new HBResultFailure<>(
      new CAIResponseError(
        nullUUID(),
        this.local(ERROR_SERVER),
        errorIo(),
        attributes,
        Optional.empty(),
        Optional.empty(),
        BLAME_SERVER
      )
    );
  }

  private HBResultFailure<InputStream, CAIResponseError> fileDataErrorIO(
    final URI uri,
    final IOException e)
  {
    final var attributes = new HashMap<String, String>();
    attributes.put(this.local(URI), uri.toString());

    return new HBResultFailure<>(
      new CAIResponseError(
        nullUUID(),
        e.getMessage(),
        errorIo(),
        attributes,
        Optional.empty(),
        Optional.of(e),
        BLAME_SERVER
      )
    );
  }

  private HBResultType<CAIResponseLogin, CAIResponseError> sendLogin(
    final CAICommandLogin message)
    throws InterruptedException
  {
    return this.send(1, this.loginURI, true, message);
  }

  private <R extends CAIResponseType, C extends CAICommandType<R>>
  HBResultType<R, CAIResponseError>
  send(
    final int attempt,
    final URI uri,
    final boolean isLoggingIn,
    final C message)
    throws InterruptedException
  {
    try {
      final var commandType = message.getClass().getSimpleName();
      LOG.debug("sending {} to {}", commandType, uri);

      final var sendBytes =
        this.messages.serialize(message);

      final var request =
        HttpRequest.newBuilder(uri)
          .header("User-Agent", userAgent())
          .POST(HttpRequest.BodyPublishers.ofByteArray(sendBytes))
          .build();

      final var response =
        this.httpClient()
          .send(request, ofByteArray());

      final var responseActual =
        this.readResponseAsByteArray(response);

      /*
       * If the response is an error, then perhaps retry. We only attempt
       * to retry if the response indicates an authentication error; if this
       * happens, we try to log in again and then re-send the original message.
       *
       * We don't try to blanket re-send any message that "failed" because
       * messages might have side effects on the server.
       */

      if (responseActual instanceof final CAIResponseError error) {
        if (attempt < 3) {
          if (isAuthenticationError(error) && !isLoggingIn) {
            return this.reLoginAndSend(attempt, uri, message);
          }
        }
        return new HBResultFailure<>(error);
      }

      /*
       * We know that the response is not an error, but we don't know that the
       * response is of the expected type. Check that here, and fail if it
       * isn't.
       */

      final var expectedResponse = message.responseClass();
      if (!Objects.equals(responseActual.getClass(), expectedResponse)) {
        throw this.errorUnexpectedResponseType(
          expectedResponse,
          responseActual);
      }

      return new HBResultSuccess<>(
        expectedResponse.cast(responseActual)
      );

    } catch (final CAProtocolException e) {
      LOG.debug("protocol exception: ", e);
      return new HBResultFailure<>(
        new CAIResponseError(
          nullUUID(),
          e.message(),
          e.errorCode(),
          e.attributes(),
          e.remediatingAction(),
          Optional.of(e),
          BLAME_SERVER
        )
      );
    } catch (final IOException e) {
      LOG.debug("i/o exception: ", e);
      return new HBResultFailure<>(errorOfPlainException(e));
    } catch (final CAKnownErrorException e) {
      return new HBResultFailure<>(e.error);
    }
  }

  private CAIResponseType readResponseAsByteArray(
    final HttpResponse<byte[]> response)
    throws CAKnownErrorException, IOException, CAProtocolException
  {
    LOG.debug("server: status {}", Integer.valueOf(response.statusCode()));

    final var responseHeaders = response.headers();
    this.readResponseCheckContentType(responseHeaders);

    final byte[] data =
      decompressResponse(
        response.body(),
        responseHeaders
      );

    return this.readResponseParse(data);
  }

  private CAIResponseType readResponseAsInputStream(
    final HttpResponse<InputStream> response)
    throws CAKnownErrorException, IOException, CAProtocolException
  {
    LOG.debug("server: status {}", Integer.valueOf(response.statusCode()));

    final var responseHeaders = response.headers();
    this.readResponseCheckContentType(responseHeaders);

    final byte[] data =
      decompressResponse(
        response.body().readAllBytes(),
        responseHeaders
      );

    return this.readResponseParse(data);
  }

  /**
   * Parse the response message, decompressing if necessary. If the
   * parsed message isn't a response... fail.
   */

  private CAIResponseType readResponseParse(final byte[] data)
    throws CAProtocolException, CAKnownErrorException
  {
    final var responseMessage = this.messages.parse(data);
    if (!(responseMessage instanceof final CAIResponseType responseActual)) {
      throw this.errorUnexpectedResponseType(
        CAIResponseType.class,
        responseMessage
      );
    }
    return responseActual;
  }

  /**
   * Check the content type. Fail if it's not what we expected.
   */

  private void readResponseCheckContentType(
    final HttpHeaders responseHeaders)
    throws CAKnownErrorException
  {
    final var contentType =
      responseHeaders.firstValue("content-type")
        .orElse("application/octet-stream");

    final var expectedContentType = CAI1Messages.contentType();
    if (!contentType.equals(expectedContentType)) {
      throw this.errorContentType(contentType, expectedContentType);
    }
  }

  private <R extends CAIResponseType, C extends CAICommandType<R>> HBResultType<R, CAIResponseError>
  reLoginAndSend(
    final int attempt,
    final URI uri,
    final C message)
    throws InterruptedException
  {
    LOG.debug("attempting re-login");
    final var loginResponse =
      this.sendLogin(this.mostRecentLogin);

    if (loginResponse instanceof HBResultSuccess<CAIResponseLogin, CAIResponseError>) {
      return this.send(
        attempt + 1,
        uri,
        false,
        message
      );
    }
    if (loginResponse instanceof final HBResultFailure<CAIResponseLogin, CAIResponseError> failure) {
      return failure.cast();
    }

    throw new UnreachableCodeException();
  }

  private CAKnownErrorException errorContentType(
    final String contentType,
    final String expectedContentType)
  {
    final var attributes = new HashMap<String, String>();
    attributes.put(
      this.local(EXPECTED_CONTENT_TYPE),
      expectedContentType
    );
    attributes.put(
      this.local(RECEIVED_CONTENT_TYPE),
      contentType
    );

    return new CAKnownErrorException(
      new CAIResponseError(
        nullUUID(),
        this.local(ERROR_RECEIVED_UNEXPECTED_CONTENT_TYPE),
        errorProtocol(),
        attributes,
        Optional.empty(),
        Optional.empty(),
        BLAME_SERVER
      )
    );
  }

  private CAKnownErrorException
  errorUnexpectedResponseType(
    final Class<? extends CAIResponseType> expectedType,
    final CAIMessageType responseActual)
  {
    final var attributes = new HashMap<String, String>();
    attributes.put(
      this.local(EXPECTED_RESPONSE_TYPE),
      expectedType.getSimpleName()
    );
    attributes.put(
      this.local(RECEIVED_RESPONSE_TYPE),
      responseActual.getClass().getSimpleName()
    );

    return new CAKnownErrorException(
      new CAIResponseError(
        nullUUID(),
        this.local(ERROR_RECEIVED_UNEXPECTED_RESPONSE_TYPE),
        errorProtocol(),
        attributes,
        Optional.empty(),
        Optional.empty(),
        BLAME_SERVER
      )
    );
  }

  private static final class CAKnownErrorException extends Exception
  {
    private final CAIResponseError error;

    private CAKnownErrorException(
      final CAIResponseError inError)
    {
      this.error = Objects.requireNonNull(inError, "error");
    }

    public CAIResponseError error()
    {
      return this.error;
    }
  }

  private String local(
    final CAStringConstantType id,
    final Object... args)
  {
    return this.strings().format(id, args);
  }

  @Override
  public boolean onIsConnected()
  {
    return true;
  }

  @Override
  public List<CAClientEventType> onPollEvents()
    throws InterruptedException
  {
    try {
      final var request =
        HttpRequest.newBuilder(this.eventsURI)
          .header("User-Agent", userAgent())
          .GET()
          .build();

      final var response =
        this.httpClient()
          .send(request, ofByteArray());

      final var responseHeaders =
        response.headers();

      /*
       * Check the content type.
       */

      final var contentType =
        responseHeaders.firstValue("content-type")
          .orElse("application/octet-stream");

      final var expectedContentType = CAI1Messages.contentType();
      if (!contentType.equals(expectedContentType)) {
        LOG.debug(
          "unexpected content type: {} (expected {})",
          contentType,
          expectedContentType
        );
        return this.allBufferedEvents();
      }

      /*
       * Parse the response message, decompressing if necessary.
       */

      final var responseMessage =
        this.messages.parse(
          decompressResponse(response.body(), responseHeaders)
        );

      if (responseMessage instanceof final CAIEventType event) {
        if (event instanceof final CAIEventUpdated updated) {
          this.events.add(
            new CAClientEventDataUpdated(updated.updated(), updated.removed())
          );
        }
        return this.allBufferedEvents();
      }

      LOG.debug(
        "unexpected message type: {} (expected {})",
        responseMessage.getClass().getCanonicalName(),
        CAIEventType.class.getCanonicalName()
      );
      return this.allBufferedEvents();
    } catch (final CAProtocolException e) {
      LOG.debug("protocol exception: ", e);
      return this.allBufferedEvents();
    } catch (final IOException e) {
      LOG.debug("i/o exception: ", e);
      return this.allBufferedEvents();
    }
  }

  private List<CAClientEventType> allBufferedEvents()
  {
    final var eventsCopy =
      List.copyOf(this.events);
    this.events.clear();
    return eventsCopy;
  }

  @Override
  public HBResultType<
    HBClientNewHandler<
      CAClientException,
      CAICommandType<?>,
      CAIResponseType,
      CAIResponseType,
      CAIResponseError,
      CAClientEventType,
      CAClientCredentials>,
    CAIResponseError>
  onExecuteLogin(
    final CAClientCredentials credentials)
    throws InterruptedException
  {
    LOG.debug("login: {}", credentials.baseURI());

    this.mostRecentLogin =
      new CAICommandLogin(
        credentials.username(),
        credentials.password(),
        credentials.metadata()
      );

    final var response =
      this.sendLogin(this.mostRecentLogin);

    if (response instanceof final HBResultSuccess<CAIResponseLogin, CAIResponseError> success) {
      LOG.debug("login: succeeded");
      return new HBResultSuccess<>(
        new HBClientNewHandler<>(this, success.result())
      );
    }
    if (response instanceof final HBResultFailure<CAIResponseLogin, CAIResponseError> failure) {
      LOG.debug("login: failed ({})", failure.result().message());
      return failure.cast();
    }

    throw new UnreachableCodeException();
  }

  @Override
  public HBResultType<CAIResponseType, CAIResponseError> onExecuteCommand(
    final CAICommandType<?> command)
    throws InterruptedException
  {
    return this.send(1, this.commandURI, false, command)
      .map(x -> x);
  }

  @Override
  public void onDisconnect()
  {

  }

  @Override
  public HBResultType<Path, CAIResponseError> onExecuteFileDownload(
    final CAFileID fileID,
    final Path file,
    final Path fileTmp,
    final long size,
    final String hashAlgorithm,
    final String hashValue,
    final Consumer<CAClientTransferStatistics> statistics)
  {
    Objects.requireNonNull(fileID, "fileID");
    Objects.requireNonNull(file, "file");
    Objects.requireNonNull(fileTmp, "fileTmp");
    Objects.requireNonNull(statistics, "statistics");

    try {
      final var targetURI =
        java.net.URI.create(
          new StringBuilder(128)
            .append(this.fileDownloadURI)
            .append('?')
            .append("FileID=")
            .append(URLEncoder.encode(fileID.displayId(), UTF_8))
            .toString()
        );

      final var request =
        HttpRequest.newBuilder(targetURI)
          .header("User-Agent", userAgent())
          .GET()
          .build();

      final var response =
        this.httpClient()
          .send(request, HttpResponse.BodyHandlers.ofInputStream());

      if (response.statusCode() == 200) {
        final var clock = this.configuration().clock();
        try (var tracker =
               new CATransferStatisticsTracker(clock, size, statistics)) {
          this.downloadFileAndHash(
            tracker,
            response.body(),
            file,
            fileTmp,
            hashAlgorithm,
            hashValue
          );
        }

        return new HBResultSuccess<>(file);
      }

      final var responseActual =
        this.readResponseAsInputStream(response);

      return new HBResultFailure<>(
        (CAIResponseError) responseActual
      );
    } catch (final CAKnownErrorException e) {
      return new HBResultFailure<>(e.error);
    } catch (final Exception e) {
      return new HBResultFailure<>(errorOfPlainException(e));
    }
  }

  private void downloadFileAndHash(
    final CATransferStatisticsTracker tracker,
    final InputStream stream,
    final Path file,
    final Path fileTmp,
    final String hashAlgorithm,
    final String hashValue)
    throws CAKnownErrorException, NoSuchAlgorithmException, IOException
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
          throw new CAKnownErrorException(
            new CAIResponseError(
              nullUUID(),
              this.local(ERROR_HASH_VALUE_MISMATCH),
              errorIo(),
              Map.ofEntries(
                Map.entry(this.local(EXPECTED_HASH), hashValue),
                Map.entry(this.local(RECEIVED_HASH), hashResult),
                Map.entry(this.local(HASH_ALGORITHM), hashAlgorithm)
              ),
              Optional.empty(),
              Optional.empty(),
              BLAME_CLIENT
            )
          );
        }

        tracker.completed();
        Files.move(fileTmp, file, REPLACE_EXISTING, ATOMIC_MOVE);
      }
    }
  }

  @Override
  public HBResultType<CAFileID, CAIResponseError> onExecuteFileUpload(
    final CAFileID fileID,
    final Path file,
    final String contentType,
    final String description,
    final Consumer<CAClientTransferStatistics> statistics)
    throws InterruptedException
  {
    try {
      final var hash =
        hashOfFile(file);
      final var size =
        Files.size(file);

      final var targetURI =
        java.net.URI.create(
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

      final var clock = this.configuration().clock();
      try (var tracker =
             new CATransferStatisticsTracker(clock, size, statistics)) {
        final var mainPublisher =
          HttpRequest.BodyPublishers.ofFile(file);
        final var statisticsSubscriber =
          new CATransferStatisticsSubscriber(tracker);

        final var request =
          HttpRequest.newBuilder(targetURI)
            .header("User-Agent", userAgent())
            .POST(mainPublisher)
            .build();

        mainPublisher.subscribe(statisticsSubscriber);
        final HttpResponse<byte[]> response =
          this.httpClient().send(request, ofByteArray());

        final var responseActual =
          this.readResponseAsByteArray(response);
        if (responseActual instanceof final CAIResponseError error) {
          return new HBResultFailure<>(error);
        }

        tracker.completed();
        return new HBResultSuccess<>(fileID);
      }
    } catch (final CAKnownErrorException e) {
      return new HBResultFailure<>(e.error);
    } catch (final Exception e) {
      return new HBResultFailure<>(errorOfPlainException(e));
    }
  }

  private static CAIResponseError errorOfPlainException(
    final Exception e)
  {
    return new CAIResponseError(
      nullUUID(),
      requireNonNullElse(e.getMessage(), e.getClass().getSimpleName()),
      errorIo(),
      Map.of(),
      Optional.empty(),
      Optional.of(e),
      BLAME_CLIENT
    );
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

  @Override
  public HBResultType<CAClientUnit, CAIResponseError> onExecuteGarbage()
    throws InterruptedException
  {
    try {
      final var data = new byte[128];
      SecureRandom.getInstanceStrong().nextBytes(data);

      final var request =
        HttpRequest.newBuilder(this.commandURI)
          .header("User-Agent", userAgent())
          .POST(HttpRequest.BodyPublishers.ofByteArray(data))
          .build();

      final var response =
        this.httpClient()
          .send(request, ofByteArray());

      final var responseActual = this.readResponseAsByteArray(response);
      if (responseActual instanceof final CAIResponseError error) {
        return new HBResultFailure<>(error);
      }

      return new HBResultSuccess<>(UNIT);
    } catch (final InterruptedException e) {
      throw e;
    } catch (final CAKnownErrorException e) {
      return new HBResultFailure<>(e.error);
    } catch (final Exception e) {
      return new HBResultFailure<>(
        errorOfPlainException(e)
      );
    }
  }

  @Override
  public HBResultType<CAClientUnit, CAIResponseError> onExecuteInvalid()
    throws InterruptedException
  {
    try {
      final var data =
        this.messages.serialize(new CAIResponseError(
          UUID.randomUUID(),
          "Summary",
          errorIo(),
          Map.of("x", "y"),
          Optional.empty(),
          Optional.empty(),
          BLAME_CLIENT
        ));

      final var request =
        HttpRequest.newBuilder(this.commandURI)
          .header("User-Agent", userAgent())
          .POST(HttpRequest.BodyPublishers.ofByteArray(data))
          .build();

      final var response =
        this.httpClient()
          .send(request, ofByteArray());

      final var responseActual = this.readResponseAsByteArray(response);
      if (responseActual instanceof final CAIResponseError error) {
        return new HBResultFailure<>(error);
      }

      return new HBResultSuccess<>(UNIT);
    } catch (final InterruptedException e) {
      throw e;
    } catch (final CAKnownErrorException e) {
      return new HBResultFailure<>(e.error);
    } catch (final Exception e) {
      return new HBResultFailure<>(
        errorOfPlainException(e)
      );
    }
  }

  @Override
  public String toString()
  {
    return "[CAHandler1 0x%s]"
      .formatted(toUnsignedString(this.hashCode(), 16));
  }
}
