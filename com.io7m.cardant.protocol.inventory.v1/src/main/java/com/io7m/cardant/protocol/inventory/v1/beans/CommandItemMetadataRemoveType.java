/*
 * XML Type:  CommandItemMetadataRemoveType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

import java.util.List;


/**
 * An XML CommandItemMetadataRemoveType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface CommandItemMetadataRemoveType extends CommandType
{
  DocumentFactory<CommandItemMetadataRemoveType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "commanditemmetadataremovetype8c5etype");
  SchemaType type = Factory.getType();


  /**
   * Gets a List of "ItemMetadataName" elements
   */
  List<String> getItemMetadataNameList();

  /**
   * Gets array of all "ItemMetadataName" elements
   */
  String[] getItemMetadataNameArray();

  /**
   * Sets array of all "ItemMetadataName" element
   */
  void setItemMetadataNameArray(String[] itemMetadataNameArray);

  /**
   * Gets ith "ItemMetadataName" element
   */
  String getItemMetadataNameArray(int i);

  /**
   * Gets (as xml) a List of "ItemMetadataName" elements
   */
  List<ItemMetadataNameType> xgetItemMetadataNameList();

  /**
   * Gets (as xml) array of all "ItemMetadataName" elements
   */
  ItemMetadataNameType[] xgetItemMetadataNameArray();

  /**
   * Gets (as xml) ith "ItemMetadataName" element
   */
  ItemMetadataNameType xgetItemMetadataNameArray(int i);

  /**
   * Returns number of "ItemMetadataName" element
   */
  int sizeOfItemMetadataNameArray();

  /**
   * Sets ith "ItemMetadataName" element
   */
  void setItemMetadataNameArray(
    int i,
    String itemMetadataName);

  /**
   * Sets (as xml) array of all "ItemMetadataName" element
   */
  void xsetItemMetadataNameArray(ItemMetadataNameType[] itemMetadataNameArray);

  /**
   * Sets (as xml) ith "ItemMetadataName" element
   */
  void xsetItemMetadataNameArray(
    int i,
    ItemMetadataNameType itemMetadataName);

  /**
   * Inserts the value as the ith "ItemMetadataName" element
   */
  void insertItemMetadataName(
    int i,
    String itemMetadataName);

  /**
   * Appends the value as the last "ItemMetadataName" element
   */
  void addItemMetadataName(String itemMetadataName);

  /**
   * Inserts and returns a new empty value (as xml) as the ith "ItemMetadataName" element
   */
  ItemMetadataNameType insertNewItemMetadataName(int i);

  /**
   * Appends and returns a new empty value (as xml) as the last "ItemMetadataName" element
   */
  ItemMetadataNameType addNewItemMetadataName();

  /**
   * Removes the ith "ItemMetadataName" element
   */
  void removeItemMetadataName(int i);

  /**
   * Gets the "item" attribute
   */
  String getItem();

  /**
   * Sets the "item" attribute
   */
  void setItem(String item);

  /**
   * Gets (as xml) the "item" attribute
   */
  UUIDType xgetItem();

  /**
   * Sets (as xml) the "item" attribute
   */
  void xsetItem(UUIDType item);
}
