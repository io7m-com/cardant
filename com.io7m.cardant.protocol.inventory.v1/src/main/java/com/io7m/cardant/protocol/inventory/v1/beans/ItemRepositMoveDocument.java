/*
 * An XML document type.
 * Localname: ItemRepositMove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositMoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ItemRepositMove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ItemRepositMoveDocument extends com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositMoveDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder.typeSystem, "itemrepositmovef50fdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ItemRepositMove" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositMoveType getItemRepositMove();

    /**
     * Sets the "ItemRepositMove" element
     */
    void setItemRepositMove(com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositMoveType itemRepositMove);

    /**
     * Appends and returns a new empty "ItemRepositMove" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositMoveType addNewItemRepositMove();
}
