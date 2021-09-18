/*
 * An XML document type.
 * Localname: ItemAttachment
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ItemAttachment(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ItemAttachmentDocument extends XmlObject
{
  DocumentFactory<ItemAttachmentDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "itemattachment1eb1doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ItemAttachment" element
   */
  ItemAttachmentType getItemAttachment();

  /**
   * Sets the "ItemAttachment" element
   */
  void setItemAttachment(ItemAttachmentType itemAttachment);

  /**
   * Appends and returns a new empty "ItemAttachment" element
   */
  ItemAttachmentType addNewItemAttachment();
}
