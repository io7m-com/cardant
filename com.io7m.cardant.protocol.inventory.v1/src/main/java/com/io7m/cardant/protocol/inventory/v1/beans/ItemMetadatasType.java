/*
 * XML Type:  ItemMetadatasType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType
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
 * An XML ItemMetadatasType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ItemMetadatasType extends XmlObject
{
  DocumentFactory<ItemMetadatasType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "itemmetadatastyped9betype");
  SchemaType type = Factory.getType();


  /**
   * Gets a List of "ItemMetadata" elements
   */
  List<ItemMetadataType> getItemMetadataList();

  /**
   * Gets array of all "ItemMetadata" elements
   */
  ItemMetadataType[] getItemMetadataArray();

  /**
   * Sets array of all "ItemMetadata" element
   */
  void setItemMetadataArray(ItemMetadataType[] itemMetadataArray);

  /**
   * Gets ith "ItemMetadata" element
   */
  ItemMetadataType getItemMetadataArray(int i);

  /**
   * Returns number of "ItemMetadata" element
   */
  int sizeOfItemMetadataArray();

  /**
   * Sets ith "ItemMetadata" element
   */
  void setItemMetadataArray(
    int i,
    ItemMetadataType itemMetadata);

  /**
   * Inserts and returns a new empty value (as xml) as the ith "ItemMetadata" element
   */
  ItemMetadataType insertNewItemMetadata(int i);

  /**
   * Appends and returns a new empty value (as xml) as the last "ItemMetadata" element
   */
  ItemMetadataType addNewItemMetadata();

  /**
   * Removes the ith "ItemMetadata" element
   */
  void removeItemMetadata(int i);
}
