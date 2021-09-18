/*
 * An XML document type.
 * Localname: ResponseItemCreate
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemCreateDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ResponseItemCreate(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseItemCreateDocument extends ResponseDocument
{
  DocumentFactory<ResponseItemCreateDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "responseitemcreate4df7doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ResponseItemCreate" element
   */
  ResponseItemCreateType getResponseItemCreate();

  /**
   * Sets the "ResponseItemCreate" element
   */
  void setResponseItemCreate(ResponseItemCreateType responseItemCreate);

  /**
   * Appends and returns a new empty "ResponseItemCreate" element
   */
  ResponseItemCreateType addNewResponseItemCreate();
}
