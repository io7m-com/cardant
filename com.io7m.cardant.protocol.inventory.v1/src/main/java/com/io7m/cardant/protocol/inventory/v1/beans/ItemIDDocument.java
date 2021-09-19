/*
 * An XML document type.
 * Localname: ItemID
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemIDDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ItemID(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ItemIDDocument extends IDDocument
{
  DocumentFactory<ItemIDDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "itemidb9d9doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ItemID" element
   */
  ItemIDType getItemID();

  /**
   * Sets the "ItemID" element
   */
  void setItemID(ItemIDType itemID);

  /**
   * Appends and returns a new empty "ItemID" element
   */
  ItemIDType addNewItemID();
}
