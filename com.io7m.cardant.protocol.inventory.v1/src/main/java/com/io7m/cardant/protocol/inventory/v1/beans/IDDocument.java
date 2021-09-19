/*
 * An XML document type.
 * Localname: ID
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.IDDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ID(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface IDDocument extends XmlObject
{
  DocumentFactory<IDDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "id996cdoctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ID" element
   */
  IDType getID();

  /**
   * Sets the "ID" element
   */
  void setID(IDType id);

  /**
   * Appends and returns a new empty "ID" element
   */
  IDType addNewID();
}
