/*
 * XML Type:  ItemMetadatasType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ItemMetadatasType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ItemMetadatasType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s76DE06BD1DB329CBFB2257F5CD3D6E75.TypeSystemHolder.typeSystem, "itemmetadatastyped9betype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "ItemMetadata" elements
     */
    java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType> getItemMetadataList();

    /**
     * Gets array of all "ItemMetadata" elements
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType[] getItemMetadataArray();

    /**
     * Gets ith "ItemMetadata" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType getItemMetadataArray(int i);

    /**
     * Returns number of "ItemMetadata" element
     */
    int sizeOfItemMetadataArray();

    /**
     * Sets array of all "ItemMetadata" element
     */
    void setItemMetadataArray(com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType[] itemMetadataArray);

    /**
     * Sets ith "ItemMetadata" element
     */
    void setItemMetadataArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType itemMetadata);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "ItemMetadata" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType insertNewItemMetadata(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "ItemMetadata" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType addNewItemMetadata();

    /**
     * Removes the ith "ItemMetadata" element
     */
    void removeItemMetadata(int i);
}
