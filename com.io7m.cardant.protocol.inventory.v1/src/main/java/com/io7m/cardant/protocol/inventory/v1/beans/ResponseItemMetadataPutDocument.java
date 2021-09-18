/*
 * An XML document type.
 * Localname: ResponseItemMetadataPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ResponseItemMetadataPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseItemMetadataPutDocument extends ResponseDocument
{
  DocumentFactory<ResponseItemMetadataPutDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "responseitemmetadataput438ddoctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ResponseItemMetadataPut" element
   */
  ResponseItemMetadataPutType getResponseItemMetadataPut();

  /**
   * Sets the "ResponseItemMetadataPut" element
   */
  void setResponseItemMetadataPut(ResponseItemMetadataPutType responseItemMetadataPut);

  /**
   * Appends and returns a new empty "ResponseItemMetadataPut" element
   */
  ResponseItemMetadataPutType addNewResponseItemMetadataPut();
}
