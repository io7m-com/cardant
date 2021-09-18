/*
 * XML Type:  ItemLocationsType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationsType
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
 * An XML ItemLocationsType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ItemLocationsType extends XmlObject
{
  DocumentFactory<ItemLocationsType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "itemlocationstype69c4type");
  SchemaType type = Factory.getType();


  /**
   * Gets a List of "ItemLocation" elements
   */
  List<ItemLocationType> getItemLocationList();

  /**
   * Gets array of all "ItemLocation" elements
   */
  ItemLocationType[] getItemLocationArray();

  /**
   * Sets array of all "ItemLocation" element
   */
  void setItemLocationArray(ItemLocationType[] itemLocationArray);

  /**
   * Gets ith "ItemLocation" element
   */
  ItemLocationType getItemLocationArray(int i);

  /**
   * Returns number of "ItemLocation" element
   */
  int sizeOfItemLocationArray();

  /**
   * Sets ith "ItemLocation" element
   */
  void setItemLocationArray(
    int i,
    ItemLocationType itemLocation);

  /**
   * Inserts and returns a new empty value (as xml) as the ith "ItemLocation" element
   */
  ItemLocationType insertNewItemLocation(int i);

  /**
   * Appends and returns a new empty value (as xml) as the last "ItemLocation" element
   */
  ItemLocationType addNewItemLocation();

  /**
   * Removes the ith "ItemLocation" element
   */
  void removeItemLocation(int i);
}
