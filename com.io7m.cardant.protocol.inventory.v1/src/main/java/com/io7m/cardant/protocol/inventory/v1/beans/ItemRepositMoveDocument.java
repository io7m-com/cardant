/*
 * An XML document type.
 * Localname: ItemRepositMove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositMoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ItemRepositMove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ItemRepositMoveDocument extends ItemRepositDocument
{
  DocumentFactory<ItemRepositMoveDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "itemrepositmovef50fdoctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ItemRepositMove" element
   */
  ItemRepositMoveType getItemRepositMove();

  /**
   * Sets the "ItemRepositMove" element
   */
  void setItemRepositMove(ItemRepositMoveType itemRepositMove);

  /**
   * Appends and returns a new empty "ItemRepositMove" element
   */
  ItemRepositMoveType addNewItemRepositMove();
}
