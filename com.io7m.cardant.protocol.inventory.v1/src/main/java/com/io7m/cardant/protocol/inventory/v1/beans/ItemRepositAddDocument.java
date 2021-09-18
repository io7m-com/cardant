/*
 * An XML document type.
 * Localname: ItemRepositAdd
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositAddDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ItemRepositAdd(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ItemRepositAddDocument extends ItemRepositDocument
{
  DocumentFactory<ItemRepositAddDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "itemrepositadd093fdoctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ItemRepositAdd" element
   */
  ItemRepositAddType getItemRepositAdd();

  /**
   * Sets the "ItemRepositAdd" element
   */
  void setItemRepositAdd(ItemRepositAddType itemRepositAdd);

  /**
   * Appends and returns a new empty "ItemRepositAdd" element
   */
  ItemRepositAddType addNewItemRepositAdd();
}
