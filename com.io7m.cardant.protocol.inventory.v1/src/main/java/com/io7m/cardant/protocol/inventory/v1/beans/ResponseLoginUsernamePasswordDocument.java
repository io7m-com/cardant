/*
 * An XML document type.
 * Localname: ResponseLoginUsernamePassword
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseLoginUsernamePasswordDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ResponseLoginUsernamePassword(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseLoginUsernamePasswordDocument extends ResponseDocument
{
  DocumentFactory<ResponseLoginUsernamePasswordDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "responseloginusernamepassword4ae0doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ResponseLoginUsernamePassword" element
   */
  ResponseLoginUsernamePasswordType getResponseLoginUsernamePassword();

  /**
   * Sets the "ResponseLoginUsernamePassword" element
   */
  void setResponseLoginUsernamePassword(ResponseLoginUsernamePasswordType responseLoginUsernamePassword);

  /**
   * Appends and returns a new empty "ResponseLoginUsernamePassword" element
   */
  ResponseLoginUsernamePasswordType addNewResponseLoginUsernamePassword();
}
