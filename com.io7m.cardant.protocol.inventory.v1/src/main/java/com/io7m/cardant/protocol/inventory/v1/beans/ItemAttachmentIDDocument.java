/*
 * An XML document type.
 * Localname: ItemAttachmentID
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentIDDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ItemAttachmentID(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ItemAttachmentIDDocument extends com.io7m.cardant.protocol.inventory.v1.beans.IDDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentIDDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder.typeSystem, "itemattachmentid8cb6doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ItemAttachmentID" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentIDType getItemAttachmentID();

    /**
     * Sets the "ItemAttachmentID" element
     */
    void setItemAttachmentID(com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentIDType itemAttachmentID);

    /**
     * Appends and returns a new empty "ItemAttachmentID" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentIDType addNewItemAttachmentID();
}
