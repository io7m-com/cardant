/*
 * An XML document type.
 * Localname: ItemReposit
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ItemReposit(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ItemRepositDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sB4E2B3A435FC84169BAD368044F7CCA6.TypeSystemHolder.typeSystem, "itemreposit72e0doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ItemReposit" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositType getItemReposit();

    /**
     * Sets the "ItemReposit" element
     */
    void setItemReposit(com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositType itemReposit);

    /**
     * Appends and returns a new empty "ItemReposit" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositType addNewItemReposit();
}
