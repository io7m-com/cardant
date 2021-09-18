/*
 * XML Type:  CommandItemMetadataPutType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML CommandItemMetadataPutType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface CommandItemMetadataPutType extends com.io7m.cardant.protocol.inventory.v1.beans.CommandType {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s2F5B3CB3EEF95D40ACF30F098DD12ED2.TypeSystemHolder.typeSystem, "commanditemmetadataputtype6dbbtype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ItemMetadatas" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType getItemMetadatas();

    /**
     * Sets the "ItemMetadatas" element
     */
    void setItemMetadatas(com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType itemMetadatas);

    /**
     * Appends and returns a new empty "ItemMetadatas" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType addNewItemMetadatas();

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
