/*
 * An XML document type.
 * Localname: ItemLocation
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ItemLocation(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ItemLocationDocument extends XmlObject
{
  DocumentFactory<ItemLocationDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "itemlocation2f5fdoctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ItemLocation" element
   */
  ItemLocationType getItemLocation();

  /**
   * Sets the "ItemLocation" element
   */
  void setItemLocation(ItemLocationType itemLocation);

  /**
   * Appends and returns a new empty "ItemLocation" element
   */
  ItemLocationType addNewItemLocation();
}
