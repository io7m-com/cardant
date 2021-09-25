/*
 * XML Type:  ItemsType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemsType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ItemsType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ItemsType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ItemsType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sB4E2B3A435FC84169BAD368044F7CCA6.TypeSystemHolder.typeSystem, "itemstypec9aftype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "Item" elements
     */
    java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.ItemType> getItemList();

    /**
     * Gets array of all "Item" elements
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemType[] getItemArray();

    /**
     * Gets ith "Item" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemType getItemArray(int i);

    /**
     * Returns number of "Item" element
     */
    int sizeOfItemArray();

    /**
     * Sets array of all "Item" element
     */
    void setItemArray(com.io7m.cardant.protocol.inventory.v1.beans.ItemType[] itemArray);

    /**
     * Sets ith "Item" element
     */
    void setItemArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.ItemType item);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "Item" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemType insertNewItem(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "Item" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemType addNewItem();

    /**
     * Removes the ith "Item" element
     */
    void removeItem(int i);
}
