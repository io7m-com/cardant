/*
 * An XML document type.
 * Localname: ItemAttachment
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ItemAttachment(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ItemAttachmentDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "itemattachment1eb1doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ItemAttachment" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType getItemAttachment();

    /**
     * Sets the "ItemAttachment" element
     */
    void setItemAttachment(com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType itemAttachment);

    /**
     * Appends and returns a new empty "ItemAttachment" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType addNewItemAttachment();
}
