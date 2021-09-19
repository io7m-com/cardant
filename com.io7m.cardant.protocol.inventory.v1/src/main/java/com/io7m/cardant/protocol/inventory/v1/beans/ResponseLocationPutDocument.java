/*
 * An XML document type.
 * Localname: ResponseLocationPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ResponseLocationPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseLocationPutDocument extends ResponseDocument
{
  DocumentFactory<ResponseLocationPutDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "responselocationputeb60doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ResponseLocationPut" element
   */
  ResponseLocationPutType getResponseLocationPut();

  /**
   * Sets the "ResponseLocationPut" element
   */
  void setResponseLocationPut(ResponseLocationPutType responseLocationPut);

  /**
   * Appends and returns a new empty "ResponseLocationPut" element
   */
  ResponseLocationPutType addNewResponseLocationPut();
}
