/*
 * An XML document type.
 * Localname: ItemMetadatas
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ItemMetadatas(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ItemMetadatasDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s2F5B3CB3EEF95D40ACF30F098DD12ED2.TypeSystemHolder.typeSystem, "itemmetadatasf7e8doctype");
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
}
