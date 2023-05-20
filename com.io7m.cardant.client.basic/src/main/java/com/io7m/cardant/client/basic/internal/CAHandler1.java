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
import com.io7m.cardant.client.api.CAClientUnit;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.inventory.CAICommandLogin;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIEventType;
import com.io7m.cardant.protocol.inventory.CAIEventUpdated;
import com.io7m.cardant.protocol.inventory.CAIMessageType;
import com.io7m.cardant.protocol.inventory.CAIResponseBlame;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseLogin;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.protocol.inventory.cb.CAI1Messages;
import com.io7m.hibiscus.api.HBResultFailure;
import com.io7m.hibiscus.api.HBResultSuccess;
import com.io7m.hibiscus.api.HBResultType;
import com.io7m.hibiscus.basic.HBClientNewHandler;
import com.io7m.junreachable.UnreachableCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.io7m.cardant.client.api.CAClientUnit.UNIT;
import static com.io7m.cardant.client.basic.internal.CACompression.decompressResponse;
import static com.io7m.cardant.client.basic.internal.CAUUIDs.nullUUID;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorAuthentication;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorIo;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorProtocol;

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
  private final URI fileURI;
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
    this.fileURI =
      baseURI.resolve("file")
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
    final String version;
    final var pack = CAHandler1.class.getPackage();
    if (pack != null) {
      version = pack.getImplementationVersion();
    } else {
      version = "0.0.0";
    }
    return "com.io7m.cardant.client/%s".formatted(version);
  }

  private HBResultFailure<InputStream, CAIResponseError> fileDataServerError(
    final URI uri,
    final HttpResponse<InputStream> response)
  {
    final var attributes = new HashMap<String, String>();
    attributes.put(
      this.local("HTTP Response Code"),
      Integer.toString(response.statusCode())
    );
    attributes.put(
      this.local("URI"),
      uri.toString()
    );

    return new HBResultFailure<>(
      new CAIResponseError(
        nullUUID(),
        this.local("Received an error from the server."),
        errorIo(),
        attributes,
        Optional.empty(),
        Optional.empty(),
        CAIResponseBlame.BLAME_SERVER
      )
    );
  }

  private HBResultFailure<InputStream, CAIResponseError> fileDataErrorIO(
    final URI uri,
    final IOException e)
  {
    final var attributes = new HashMap<String, String>();
    attributes.put(
      this.local("URI"),
      uri.toString()
    );
    return new HBResultFailure<>(
      new CAIResponseError(
        nullUUID(),
        e.getMessage(),
        errorIo(),
        attributes,
        Optional.empty(),
        Optional.of(e),
        CAIResponseBlame.BLAME_SERVER
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
  sendCommand(final C command)
    throws InterruptedException
  {
    return this.send(1, this.commandURI, false, command);
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
          .send(request, HttpResponse.BodyHandlers.ofByteArray());

      LOG.debug("server: status {}", response.statusCode());

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
        return this.errorContentType(contentType, expectedContentType);
      }

      /*
       * Parse the response message, decompressing if necessary. If the
       * parsed message isn't a response... fail.
       */

      final var responseMessage =
        this.messages.parse(decompressResponse(response, responseHeaders));

      if (!(responseMessage instanceof final CAIResponseType responseActual)) {
        return this.errorUnexpectedResponseType(message, responseMessage);
      }

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
       * We know that the response is an error, but we don't know that the
       * response is of the expected type. Check that here, and fail if it
       * isn't.
       */

      if (!Objects.equals(responseActual.getClass(), message.responseClass())) {
        return this.errorUnexpectedResponseType(message, responseActual);
      }

      return new HBResultSuccess<>(
        message.responseClass().cast(responseMessage)
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
          CAIResponseBlame.BLAME_SERVER
        )
      );
    } catch (final IOException e) {
      LOG.debug("i/o exception: ", e);
      return new HBResultFailure<>(
        new CAIResponseError(
          nullUUID(),
          e.getMessage(),
          errorIo(),
          Map.of(),
          Optional.empty(),
          Optional.of(e),
          CAIResponseBlame.BLAME_CLIENT
        )
      );
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

  private <R extends CAIResponseType> HBResultFailure<R, CAIResponseError> errorContentType(
    final String contentType,
    final String expectedContentType)
  {
    final var attributes = new HashMap<String, String>();
    attributes.put(
      this.local("Expected Content Type"),
      expectedContentType
    );
    attributes.put(
      this.local("Received Content Type"),
      contentType
    );

    return new HBResultFailure<>(
      new CAIResponseError(
        nullUUID(),
        this.local("Received an unexpected content type."),
        errorProtocol(),
        attributes,
        Optional.empty(),
        Optional.empty(),
        CAIResponseBlame.BLAME_SERVER
      )
    );
  }

  private <R extends CAIResponseType, C extends CAICommandType<R>> HBResultFailure<R, CAIResponseError>
  errorUnexpectedResponseType(
    final C message,
    final CAIMessageType responseActual)
  {
    final var attributes = new HashMap<String, String>();
    attributes.put(
      this.local("Expected Response Type"),
      message.responseClass().getSimpleName()
    );
    attributes.put(
      this.local("Received Response Type"),
      responseActual.getClass().getSimpleName()
    );

    return new HBResultFailure<>(
      new CAIResponseError(
        nullUUID(),
        this.local("Received an unexpected response type."),
        errorProtocol(),
        attributes,
        Optional.empty(),
        Optional.empty(),
        CAIResponseBlame.BLAME_SERVER
      )
    );
  }

  private String local(
    final String id,
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
          .send(request, HttpResponse.BodyHandlers.ofByteArray());

      LOG.debug("server: status {}", response.statusCode());

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
        this.messages.parse(decompressResponse(response, responseHeaders));

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
  public HBResultType<InputStream, CAIResponseError> onExecuteFileData(
    final CAFileID fileID)
  {
    Objects.requireNonNull(fileID, "fileID");

    return new HBResultFailure<>(
      new CAIResponseError(
        nullUUID(),
        "File not available.",
        errorIo(),
        Map.of(),
        Optional.empty(),
        Optional.empty(),
        CAIResponseBlame.BLAME_CLIENT
      )
    );
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
          .send(request, HttpResponse.BodyHandlers.ofByteArray());

      LOG.debug("server: status {}", response.statusCode());

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
        return this.errorContentType(contentType, expectedContentType)
          .cast()
          .cast();
      }

      /*
       * Parse the response message, decompressing if necessary. If the
       * parsed message isn't a response... fail.
       */

      final var responseMessage =
        this.messages.parse(decompressResponse(response, responseHeaders));

      if (!(responseMessage instanceof final CAIResponseType responseActual)) {
        return new HBResultFailure<>(
          new CAIResponseError(
            nullUUID(),
            this.local("Received an unexpected response type."),
            errorProtocol(),
            Map.of(),
            Optional.empty(),
            Optional.empty(),
            CAIResponseBlame.BLAME_SERVER
          )
        );
      }

      if (responseActual instanceof final CAIResponseError error) {
        return new HBResultFailure<>(error);
      }

      return new HBResultSuccess<>(UNIT);
    } catch (final InterruptedException e) {
      throw e;
    } catch (final Exception e) {
      return new HBResultFailure<>(
        new CAIResponseError(
          nullUUID(),
          e.getMessage(),
          errorIo(),
          Map.of(),
          Optional.empty(),
          Optional.of(e),
          CAIResponseBlame.BLAME_CLIENT
        )
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
          CAIResponseBlame.BLAME_CLIENT
        ));

      final var request =
        HttpRequest.newBuilder(this.commandURI)
          .header("User-Agent", userAgent())
          .POST(HttpRequest.BodyPublishers.ofByteArray(data))
          .build();

      final var response =
        this.httpClient()
          .send(request, HttpResponse.BodyHandlers.ofByteArray());

      LOG.debug("server: status {}", response.statusCode());

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
        return this.errorContentType(contentType, expectedContentType)
          .cast()
          .cast();
      }

      /*
       * Parse the response message, decompressing if necessary. If the
       * parsed message isn't a response... fail.
       */

      final var responseMessage =
        this.messages.parse(decompressResponse(response, responseHeaders));

      if (!(responseMessage instanceof final CAIResponseType responseActual)) {
        return new HBResultFailure<>(
          new CAIResponseError(
            nullUUID(),
            this.local("Received an unexpected response type."),
            errorProtocol(),
            Map.of(),
            Optional.empty(),
            Optional.empty(),
            CAIResponseBlame.BLAME_SERVER
          )
        );
      }

      if (responseActual instanceof final CAIResponseError error) {
        return new HBResultFailure<>(error);
      }

      return new HBResultSuccess<>(UNIT);
    } catch (final InterruptedException e) {
      throw e;
    } catch (final Exception e) {
      return new HBResultFailure<>(
        new CAIResponseError(
          nullUUID(),
          e.getMessage(),
          errorIo(),
          Map.of(),
          Optional.empty(),
          Optional.of(e),
          CAIResponseBlame.BLAME_CLIENT
        )
      );
    }
  }
}
