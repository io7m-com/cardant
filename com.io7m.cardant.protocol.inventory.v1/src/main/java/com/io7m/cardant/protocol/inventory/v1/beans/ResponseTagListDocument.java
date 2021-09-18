/*
 * An XML document type.
 * Localname: ResponseTagList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ResponseTagList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseTagListDocument extends ResponseDocument
{
  DocumentFactory<ResponseTagListDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "responsetaglist73e2doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ResponseTagList" element
   */
  ResponseTagListType getResponseTagList();

  /**
   * Sets the "ResponseTagList" element
   */
  void setResponseTagList(ResponseTagListType responseTagList);

  /**
   * Appends and returns a new empty "ResponseTagList" element
   */
  ResponseTagListType addNewResponseTagList();
}
