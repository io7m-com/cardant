/*
 * XML Type:  IDType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.IDType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;


/**
 * An XML IDType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface IDType extends XmlObject
{
  AbstractDocumentFactory<IDType> Factory = new AbstractDocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "idtype2362type");
  SchemaType type = Factory.getType();


  /**
   * Gets the "value" attribute
   */
  String getValue();

  /**
   * Sets the "value" attribute
   */
  void setValue(String value);

  /**
   * Gets (as xml) the "value" attribute
   */
  UUIDType xgetValue();

  /**
   * Sets (as xml) the "value" attribute
   */
  void xsetValue(UUIDType value);
}
