/*
 * An XML document type.
 * Localname: ResponseErrorDetail
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ResponseErrorDetail(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseErrorDetailDocument extends XmlObject
{
  DocumentFactory<ResponseErrorDetailDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "responseerrordetaila5e1doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ResponseErrorDetail" element
   */
  ResponseErrorDetailType getResponseErrorDetail();

  /**
   * Sets the "ResponseErrorDetail" element
   */
  void setResponseErrorDetail(ResponseErrorDetailType responseErrorDetail);

  /**
   * Appends and returns a new empty "ResponseErrorDetail" element
   */
  ResponseErrorDetailType addNewResponseErrorDetail();
}
