/*
 * An XML document type.
 * Localname: ItemMetadata
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ItemMetadata(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ItemMetadataDocument extends XmlObject
{
  DocumentFactory<ItemMetadataDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "itemmetadata2b25doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ItemMetadata" element
   */
  ItemMetadataType getItemMetadata();

  /**
   * Sets the "ItemMetadata" element
   */
  void setItemMetadata(ItemMetadataType itemMetadata);

  /**
   * Appends and returns a new empty "ItemMetadata" element
   */
  ItemMetadataType addNewItemMetadata();
}
