/*
 * An XML document type.
 * Localname: ItemRepositRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ItemRepositRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ItemRepositRemoveDocument extends com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositRemoveDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s76DE06BD1DB329CBFB2257F5CD3D6E75.TypeSystemHolder.typeSystem, "itemrepositremovebabcdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ItemRepositRemove" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositRemoveType getItemRepositRemove();

    /**
     * Sets the "ItemRepositRemove" element
     */
    void setItemRepositRemove(com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositRemoveType itemRepositRemove);

    /**
     * Appends and returns a new empty "ItemRepositRemove" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositRemoveType addNewItemRepositRemove();
}
