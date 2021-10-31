/*
 * An XML document type.
 * Localname: ItemRepositAdd
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositAddDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ItemRepositAdd(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ItemRepositAddDocument extends com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositAddDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "itemrepositadd093fdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ItemRepositAdd" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositAddType getItemRepositAdd();

    /**
     * Sets the "ItemRepositAdd" element
     */
    void setItemRepositAdd(com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositAddType itemRepositAdd);

    /**
     * Appends and returns a new empty "ItemRepositAdd" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositAddType addNewItemRepositAdd();
}
