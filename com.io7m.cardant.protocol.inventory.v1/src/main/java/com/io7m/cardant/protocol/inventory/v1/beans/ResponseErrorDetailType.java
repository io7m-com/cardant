/*
 * XML Type:  ResponseErrorDetailType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * An XML ResponseErrorDetailType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ResponseErrorDetailType extends XmlObject
{
  DocumentFactory<ResponseErrorDetailType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "responseerrordetailtypeaf37type");
  SchemaType type = Factory.getType();


  /**
   * Gets the "message" attribute
   */
  String getMessage();

  /**
   * Sets the "message" attribute
   */
  void setMessage(String message);

  /**
   * Gets (as xml) the "message" attribute
   */
  XmlString xgetMessage();

  /**
   * Sets (as xml) the "message" attribute
   */
  void xsetMessage(XmlString message);
}
