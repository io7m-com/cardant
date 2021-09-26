/*
 * XML Type:  ItemLocationsType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationsType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ItemLocationsType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ItemLocationsType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationsType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE8AC4557B7260DDF20EBB7BA8F2F0FBA.TypeSystemHolder.typeSystem, "itemlocationstype69c4type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "ItemLocation" elements
     */
    java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType> getItemLocationList();

    /**
     * Gets array of all "ItemLocation" elements
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType[] getItemLocationArray();

    /**
     * Gets ith "ItemLocation" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType getItemLocationArray(int i);

    /**
     * Returns number of "ItemLocation" element
     */
    int sizeOfItemLocationArray();

    /**
     * Sets array of all "ItemLocation" element
     */
    void setItemLocationArray(com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType[] itemLocationArray);

    /**
     * Sets ith "ItemLocation" element
     */
    void setItemLocationArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType itemLocation);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "ItemLocation" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType insertNewItemLocation(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "ItemLocation" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType addNewItemLocation();

    /**
     * Removes the ith "ItemLocation" element
     */
    void removeItemLocation(int i);
}
