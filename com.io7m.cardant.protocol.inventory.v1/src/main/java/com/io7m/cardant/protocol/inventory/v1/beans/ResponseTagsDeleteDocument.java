/*
 * An XML document type.
 * Localname: ResponseTagsDelete
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsDeleteDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ResponseTagsDelete(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseTagsDeleteDocument extends ResponseDocument
{
  DocumentFactory<ResponseTagsDeleteDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "responsetagsdeletec762doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ResponseTagsDelete" element
   */
  ResponseTagsDeleteType getResponseTagsDelete();

  /**
   * Sets the "ResponseTagsDelete" element
   */
  void setResponseTagsDelete(ResponseTagsDeleteType responseTagsDelete);

  /**
   * Appends and returns a new empty "ResponseTagsDelete" element
   */
  ResponseTagsDeleteType addNewResponseTagsDelete();
}
