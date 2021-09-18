/*
 * An XML document type.
 * Localname: ItemRepositRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ItemRepositRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ItemRepositRemoveDocument extends ItemRepositDocument
{
  DocumentFactory<ItemRepositRemoveDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "itemrepositremovebabcdoctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ItemRepositRemove" element
   */
  ItemRepositRemoveType getItemRepositRemove();

  /**
   * Sets the "ItemRepositRemove" element
   */
  void setItemRepositRemove(ItemRepositRemoveType itemRepositRemove);

  /**
   * Appends and returns a new empty "ItemRepositRemove" element
   */
  ItemRepositRemoveType addNewItemRepositRemove();
}
