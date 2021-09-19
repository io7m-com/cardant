/*
 * An XML document type.
 * Localname: ResponseLocationList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ResponseLocationList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseLocationListDocument extends ResponseDocument
{
  DocumentFactory<ResponseLocationListDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "responselocationlist50f3doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ResponseLocationList" element
   */
  ResponseLocationListType getResponseLocationList();

  /**
   * Sets the "ResponseLocationList" element
   */
  void setResponseLocationList(ResponseLocationListType responseLocationList);

  /**
   * Appends and returns a new empty "ResponseLocationList" element
   */
  ResponseLocationListType addNewResponseLocationList();
}
