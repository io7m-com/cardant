/*
 * XML Type:  ResponseItemLocationsListType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemLocationsListType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ResponseItemLocationsListType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ResponseItemLocationsListType extends com.io7m.cardant.protocol.inventory.v1.beans.ResponseType {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemLocationsListType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "responseitemlocationslisttype1167type");
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
