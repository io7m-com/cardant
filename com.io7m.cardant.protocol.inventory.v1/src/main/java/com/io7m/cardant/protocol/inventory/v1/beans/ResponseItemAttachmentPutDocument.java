/*
 * An XML document type.
 * Localname: ResponseItemAttachmentPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ResponseItemAttachmentPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseItemAttachmentPutDocument extends ResponseDocument
{
  DocumentFactory<ResponseItemAttachmentPutDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "responseitemattachmentput4041doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ResponseItemAttachmentPut" element
   */
  ResponseItemAttachmentPutType getResponseItemAttachmentPut();

  /**
   * Sets the "ResponseItemAttachmentPut" element
   */
  void setResponseItemAttachmentPut(ResponseItemAttachmentPutType responseItemAttachmentPut);

  /**
   * Appends and returns a new empty "ResponseItemAttachmentPut" element
   */
  ResponseItemAttachmentPutType addNewResponseItemAttachmentPut();
}
