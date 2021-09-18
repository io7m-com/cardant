/*
 * An XML document type.
 * Localname: ResponseItemGet
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemGetDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ResponseItemGet(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseItemGetDocument extends ResponseDocument
{
  DocumentFactory<ResponseItemGetDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "responseitemget8557doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ResponseItemGet" element
   */
  ResponseItemGetType getResponseItemGet();

  /**
   * Sets the "ResponseItemGet" element
   */
  void setResponseItemGet(ResponseItemGetType responseItemGet);

  /**
   * Appends and returns a new empty "ResponseItemGet" element
   */
  ResponseItemGetType addNewResponseItemGet();
}
