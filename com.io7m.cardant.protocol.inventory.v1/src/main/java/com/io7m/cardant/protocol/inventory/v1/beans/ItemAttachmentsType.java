/*
 * XML Type:  ItemAttachmentsType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

import java.util.List;


/**
 * An XML ItemAttachmentsType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ItemAttachmentsType extends XmlObject
{
  DocumentFactory<ItemAttachmentsType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "itemattachmentstype81b2type");
  SchemaType type = Factory.getType();


  /**
   * Gets a List of "ItemAttachment" elements
   */
  List<ItemAttachmentType> getItemAttachmentList();

  /**
   * Gets array of all "ItemAttachment" elements
   */
  ItemAttachmentType[] getItemAttachmentArray();

  /**
   * Sets array of all "ItemAttachment" element
   */
  void setItemAttachmentArray(ItemAttachmentType[] itemAttachmentArray);

  /**
   * Gets ith "ItemAttachment" element
   */
  ItemAttachmentType getItemAttachmentArray(int i);

  /**
   * Returns number of "ItemAttachment" element
   */
  int sizeOfItemAttachmentArray();

  /**
   * Sets ith "ItemAttachment" element
   */
  void setItemAttachmentArray(
    int i,
    ItemAttachmentType itemAttachment);

  /**
   * Inserts and returns a new empty value (as xml) as the ith "ItemAttachment" element
   */
  ItemAttachmentType insertNewItemAttachment(int i);

  /**
   * Appends and returns a new empty value (as xml) as the last "ItemAttachment" element
   */
  ItemAttachmentType addNewItemAttachment();

  /**
   * Removes the ith "ItemAttachment" element
   */
  void removeItemAttachment(int i);
}
