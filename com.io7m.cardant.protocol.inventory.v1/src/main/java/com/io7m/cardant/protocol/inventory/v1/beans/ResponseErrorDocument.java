/*
 * An XML document type.
 * Localname: ResponseError
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ResponseError(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseErrorDocument extends ResponseDocument
{
  DocumentFactory<ResponseErrorDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "responseerror75b2doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ResponseError" element
   */
  ResponseErrorType getResponseError();

  /**
   * Sets the "ResponseError" element
   */
  void setResponseError(ResponseErrorType responseError);

  /**
   * Appends and returns a new empty "ResponseError" element
   */
  ResponseErrorType addNewResponseError();
}
