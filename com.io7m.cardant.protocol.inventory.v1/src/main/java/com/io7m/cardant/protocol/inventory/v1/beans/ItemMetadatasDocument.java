/*
 * An XML document type.
 * Localname: ItemMetadatas
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ItemMetadatas(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ItemMetadatasDocument extends XmlObject
{
  DocumentFactory<ItemMetadatasDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "itemmetadatasf7e8doctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ItemMetadatas" element
   */
  ItemMetadatasType getItemMetadatas();

  /**
   * Sets the "ItemMetadatas" element
   */
  void setItemMetadatas(ItemMetadatasType itemMetadatas);

  /**
   * Appends and returns a new empty "ItemMetadatas" element
   */
  ItemMetadatasType addNewItemMetadatas();
}
