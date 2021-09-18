/*
 * An XML document type.
 * Localname: ResponseItemMetadataRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ResponseItemMetadataRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseItemMetadataRemoveDocument extends ResponseDocument
{
  DocumentFactory<ResponseItemMetadataRemoveDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "responseitemmetadataremove9a40doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ResponseItemMetadataRemove" element
   */
  ResponseItemMetadataRemoveType getResponseItemMetadataRemove();

  /**
   * Sets the "ResponseItemMetadataRemove" element
   */
  void setResponseItemMetadataRemove(ResponseItemMetadataRemoveType responseItemMetadataRemove);

  /**
   * Appends and returns a new empty "ResponseItemMetadataRemove" element
   */
  ResponseItemMetadataRemoveType addNewResponseItemMetadataRemove();
}
