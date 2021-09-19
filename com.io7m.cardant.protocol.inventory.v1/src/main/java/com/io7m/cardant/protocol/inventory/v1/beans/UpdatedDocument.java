/*
 * An XML document type.
 * Localname: Updated
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.UpdatedDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one Updated(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface UpdatedDocument extends XmlObject
{
  DocumentFactory<UpdatedDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "updatedd87edoctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "Updated" element
   */
  UpdatedType getUpdated();

  /**
   * Sets the "Updated" element
   */
  void setUpdated(UpdatedType updated);

  /**
   * Appends and returns a new empty "Updated" element
   */
  UpdatedType addNewUpdated();
}
