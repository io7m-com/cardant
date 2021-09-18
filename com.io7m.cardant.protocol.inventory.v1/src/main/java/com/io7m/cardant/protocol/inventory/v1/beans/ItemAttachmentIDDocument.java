/*
 * An XML document type.
 * Localname: ItemAttachmentID
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentIDDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ItemAttachmentID(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ItemAttachmentIDDocument extends IDDocument
{
  DocumentFactory<ItemAttachmentIDDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "itemattachmentid8cb6doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ItemAttachmentID" element
   */
  ItemAttachmentIDType getItemAttachmentID();

  /**
   * Sets the "ItemAttachmentID" element
   */
  void setItemAttachmentID(ItemAttachmentIDType itemAttachmentID);

  /**
   * Appends and returns a new empty "ItemAttachmentID" element
   */
  ItemAttachmentIDType addNewItemAttachmentID();
}
