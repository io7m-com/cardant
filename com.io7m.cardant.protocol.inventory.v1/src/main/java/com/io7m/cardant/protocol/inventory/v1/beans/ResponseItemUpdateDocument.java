/*
 * An XML document type.
 * Localname: ResponseItemUpdate
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemUpdateDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ResponseItemUpdate(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseItemUpdateDocument extends ResponseDocument
{
  DocumentFactory<ResponseItemUpdateDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "responseitemupdatea3cadoctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ResponseItemUpdate" element
   */
  ResponseItemUpdateType getResponseItemUpdate();

  /**
   * Sets the "ResponseItemUpdate" element
   */
  void setResponseItemUpdate(ResponseItemUpdateType responseItemUpdate);

  /**
   * Appends and returns a new empty "ResponseItemUpdate" element
   */
  ResponseItemUpdateType addNewResponseItemUpdate();
}
