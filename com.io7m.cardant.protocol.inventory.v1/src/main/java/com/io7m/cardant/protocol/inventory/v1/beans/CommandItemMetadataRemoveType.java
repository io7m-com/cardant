/*
 * XML Type:  CommandItemMetadataRemoveType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML CommandItemMetadataRemoveType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface CommandItemMetadataRemoveType extends com.io7m.cardant.protocol.inventory.v1.beans.CommandType {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s2F5B3CB3EEF95D40ACF30F098DD12ED2.TypeSystemHolder.typeSystem, "commanditemmetadataremovetype8c5etype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "ItemMetadataName" elements
     */
    java.util.List<java.lang.String> getItemMetadataNameList();

    /**
     * Gets array of all "ItemMetadataName" elements
     */
    java.lang.String[] getItemMetadataNameArray();

    /**
     * Gets ith "ItemMetadataName" element
     */
    java.lang.String getItemMetadataNameArray(int i);

    /**
     * Gets (as xml) a List of "ItemMetadataName" elements
     */
    java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType> xgetItemMetadataNameList();

    /**
     * Gets (as xml) array of all "ItemMetadataName" elements
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType[] xgetItemMetadataNameArray();

    /**
     * Gets (as xml) ith "ItemMetadataName" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType xgetItemMetadataNameArray(int i);

    /**
     * Returns number of "ItemMetadataName" element
     */
    int sizeOfItemMetadataNameArray();

    /**
     * Sets array of all "ItemMetadataName" element
     */
    void setItemMetadataNameArray(java.lang.String[] itemMetadataNameArray);

    /**
     * Sets ith "ItemMetadataName" element
     */
    void setItemMetadataNameArray(int i, java.lang.String itemMetadataName);

    /**
     * Sets (as xml) array of all "ItemMetadataName" element
     */
    void xsetItemMetadataNameArray(com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType[] itemMetadataNameArray);

    /**
     * Sets (as xml) ith "ItemMetadataName" element
     */
    void xsetItemMetadataNameArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType itemMetadataName);

    /**
     * Inserts the value as the ith "ItemMetadataName" element
     */
    void insertItemMetadataName(int i, java.lang.String itemMetadataName);

    /**
     * Appends the value as the last "ItemMetadataName" element
     */
    void addItemMetadataName(java.lang.String itemMetadataName);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "ItemMetadataName" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType insertNewItemMetadataName(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "ItemMetadataName" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType addNewItemMetadataName();

    /**
     * Removes the ith "ItemMetadataName" element
     */
    void removeItemMetadataName(int i);

    /**
     * Gets the "item" attribute
     */
    java.lang.String getItem();

    /**
     * Gets (as xml) the "item" attribute
     */
    com.io7m.cardant.protocol.inventory.v1.beans.UUIDType xgetItem();

    /**
     * Sets the "item" attribute
     */
    void setItem(java.lang.String item);

    /**
     * Sets (as xml) the "item" attribute
     */
    void xsetItem(com.io7m.cardant.protocol.inventory.v1.beans.UUIDType item);
}
