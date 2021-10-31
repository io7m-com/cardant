/*
 * An XML document type.
 * Localname: ItemID
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemIDDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ItemID(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ItemIDDocument extends com.io7m.cardant.protocol.inventory.v1.beans.IDDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ItemIDDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "itemidb9d9doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ItemID" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType getItemID();

    /**
     * Sets the "ItemID" element
     */
    void setItemID(com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType itemID);

    /**
     * Appends and returns a new empty "ItemID" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType addNewItemID();
}
