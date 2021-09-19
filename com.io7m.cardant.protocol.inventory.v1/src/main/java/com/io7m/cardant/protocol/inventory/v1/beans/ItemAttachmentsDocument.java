/*
 * An XML document type.
 * Localname: ItemAttachments
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ItemAttachments(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ItemAttachmentsDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s76DE06BD1DB329CBFB2257F5CD3D6E75.TypeSystemHolder.typeSystem, "itemattachments75dcdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ItemAttachments" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType getItemAttachments();

    /**
     * Sets the "ItemAttachments" element
     */
    void setItemAttachments(com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType itemAttachments);

    /**
     * Appends and returns a new empty "ItemAttachments" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType addNewItemAttachments();
}
