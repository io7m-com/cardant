/*
 * An XML document type.
 * Localname: ItemLocation
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ItemLocation(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ItemLocationDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s2F5B3CB3EEF95D40ACF30F098DD12ED2.TypeSystemHolder.typeSystem, "itemlocation2f5fdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ItemLocation" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType getItemLocation();

    /**
     * Sets the "ItemLocation" element
     */
    void setItemLocation(com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType itemLocation);

    /**
     * Appends and returns a new empty "ItemLocation" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType addNewItemLocation();
}
