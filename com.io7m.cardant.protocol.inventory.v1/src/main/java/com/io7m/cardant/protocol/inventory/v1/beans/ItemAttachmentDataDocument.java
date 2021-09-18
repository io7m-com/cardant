/*
 * An XML document type.
 * Localname: ItemAttachmentData
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDataDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ItemAttachmentData(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ItemAttachmentDataDocument extends XmlObject
{
  DocumentFactory<ItemAttachmentDataDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "itemattachmentdata3ba7doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ItemAttachmentData" element
   */
  byte[] getItemAttachmentData();

  /**
   * Sets the "ItemAttachmentData" element
   */
  void setItemAttachmentData(byte[] itemAttachmentData);

  /**
   * Gets (as xml) the "ItemAttachmentData" element
   */
  ItemAttachmentDataType xgetItemAttachmentData();

  /**
   * Sets (as xml) the "ItemAttachmentData" element
   */
  void xsetItemAttachmentData(ItemAttachmentDataType itemAttachmentData);
}
