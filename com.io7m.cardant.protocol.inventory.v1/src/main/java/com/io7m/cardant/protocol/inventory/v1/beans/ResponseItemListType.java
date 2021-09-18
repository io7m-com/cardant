/*
 * XML Type:  ResponseItemListType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemListType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

import java.util.List;


/**
 * An XML ResponseItemListType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ResponseItemListType extends ResponseType
{
  DocumentFactory<ResponseItemListType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "responseitemlisttypefccbtype");
  SchemaType type = Factory.getType();


  /**
   * Gets a List of "Item" elements
   */
  List<ItemType> getItemList();

  /**
   * Gets array of all "Item" elements
   */
  ItemType[] getItemArray();

  /**
   * Sets array of all "Item" element
   */
  void setItemArray(ItemType[] itemArray);

  /**
   * Gets ith "Item" element
   */
  ItemType getItemArray(int i);

  /**
   * Returns number of "Item" element
   */
  int sizeOfItemArray();

  /**
   * Sets ith "Item" element
   */
  void setItemArray(
    int i,
    ItemType item);

  /**
   * Inserts and returns a new empty value (as xml) as the ith "Item" element
   */
  ItemType insertNewItem(int i);

  /**
   * Appends and returns a new empty value (as xml) as the last "Item" element
   */
  ItemType addNewItem();

  /**
   * Removes the ith "Item" element
   */
  void removeItem(int i);
}
