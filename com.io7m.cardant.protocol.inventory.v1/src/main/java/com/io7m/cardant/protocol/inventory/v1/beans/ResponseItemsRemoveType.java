/*
 * XML Type:  ResponseItemsRemoveType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemsRemoveType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ResponseItemsRemoveType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ResponseItemsRemoveType extends com.io7m.cardant.protocol.inventory.v1.beans.ResponseType {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemsRemoveType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE8AC4557B7260DDF20EBB7BA8F2F0FBA.TypeSystemHolder.typeSystem, "responseitemsremovetype8a2ctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "ItemID" elements
     */
    java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType> getItemIDList();

    /**
     * Gets array of all "ItemID" elements
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType[] getItemIDArray();

    /**
     * Gets ith "ItemID" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType getItemIDArray(int i);

    /**
     * Returns number of "ItemID" element
     */
    int sizeOfItemIDArray();

    /**
     * Sets array of all "ItemID" element
     */
    void setItemIDArray(com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType[] itemIDArray);

    /**
     * Sets ith "ItemID" element
     */
    void setItemIDArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType itemID);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "ItemID" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType insertNewItemID(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "ItemID" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType addNewItemID();

    /**
     * Removes the ith "ItemID" element
     */
    void removeItemID(int i);
}
