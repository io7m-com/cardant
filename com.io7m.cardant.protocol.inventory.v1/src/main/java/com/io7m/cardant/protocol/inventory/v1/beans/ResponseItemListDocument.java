/*
 * An XML document type.
 * Localname: ResponseItemList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ResponseItemList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseItemListDocument extends ResponseDocument
{
  DocumentFactory<ResponseItemListDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "responseitemlisteb55doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ResponseItemList" element
   */
  ResponseItemListType getResponseItemList();

  /**
   * Sets the "ResponseItemList" element
   */
  void setResponseItemList(ResponseItemListType responseItemList);

  /**
   * Appends and returns a new empty "ResponseItemList" element
   */
  ResponseItemListType addNewResponseItemList();
}
