/*
 * An XML document type.
 * Localname: Removed
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.RemovedDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one Removed(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface RemovedDocument extends XmlObject
{
  DocumentFactory<RemovedDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "removede979doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "Removed" element
   */
  RemovedType getRemoved();

  /**
   * Sets the "Removed" element
   */
  void setRemoved(RemovedType removed);

  /**
   * Appends and returns a new empty "Removed" element
   */
  RemovedType addNewRemoved();
}
