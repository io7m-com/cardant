/*
 * An XML document type.
 * Localname: Response
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one Response(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseDocument extends MessageDocument
{
  DocumentFactory<ResponseDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "response21a6doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "Response" element
   */
  ResponseType getResponse();

  /**
   * Sets the "Response" element
   */
  void setResponse(ResponseType response);

  /**
   * Appends and returns a new empty "Response" element
   */
  ResponseType addNewResponse();
}
