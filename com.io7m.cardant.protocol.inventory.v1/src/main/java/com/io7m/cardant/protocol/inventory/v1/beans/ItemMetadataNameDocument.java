/*
 * An XML document type.
 * Localname: ItemMetadataName
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ItemMetadataName(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ItemMetadataNameDocument extends XmlObject
{
  DocumentFactory<ItemMetadataNameDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "itemmetadataname035adoctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ItemMetadataName" element
   */
  String getItemMetadataName();

  /**
   * Sets the "ItemMetadataName" element
   */
  void setItemMetadataName(String itemMetadataName);

  /**
   * Gets (as xml) the "ItemMetadataName" element
   */
  ItemMetadataNameType xgetItemMetadataName();

  /**
   * Sets (as xml) the "ItemMetadataName" element
   */
  void xsetItemMetadataName(ItemMetadataNameType itemMetadataName);
}
