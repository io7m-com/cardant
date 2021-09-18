/*
 * An XML document type.
 * Localname: ResponseTagsPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ResponseTagsPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseTagsPutDocument extends ResponseDocument
{
  DocumentFactory<ResponseTagsPutDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "responsetagsputaac4doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ResponseTagsPut" element
   */
  ResponseTagsPutType getResponseTagsPut();

  /**
   * Sets the "ResponseTagsPut" element
   */
  void setResponseTagsPut(ResponseTagsPutType responseTagsPut);

  /**
   * Appends and returns a new empty "ResponseTagsPut" element
   */
  ResponseTagsPutType addNewResponseTagsPut();
}
