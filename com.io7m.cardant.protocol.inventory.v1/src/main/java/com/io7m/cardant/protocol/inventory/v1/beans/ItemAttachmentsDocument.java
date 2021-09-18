/*
 * An XML document type.
 * Localname: ItemAttachments
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ItemAttachments(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ItemAttachmentsDocument extends XmlObject
{
  DocumentFactory<ItemAttachmentsDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "itemattachments75dcdoctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ItemAttachments" element
   */
  ItemAttachmentsType getItemAttachments();

  /**
   * Sets the "ItemAttachments" element
   */
  void setItemAttachments(ItemAttachmentsType itemAttachments);

  /**
   * Appends and returns a new empty "ItemAttachments" element
   */
  ItemAttachmentsType addNewItemAttachments();
}
