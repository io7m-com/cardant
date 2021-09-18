/*
 * XML Type:  ItemAttachmentsType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ItemAttachmentsType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ItemAttachmentsType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder.typeSystem, "itemattachmentstype81b2type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "ItemAttachment" elements
     */
    java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType> getItemAttachmentList();

    /**
     * Gets array of all "ItemAttachment" elements
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType[] getItemAttachmentArray();

    /**
     * Gets ith "ItemAttachment" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType getItemAttachmentArray(int i);

    /**
     * Returns number of "ItemAttachment" element
     */
    int sizeOfItemAttachmentArray();

    /**
     * Sets array of all "ItemAttachment" element
     */
    void setItemAttachmentArray(com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType[] itemAttachmentArray);

    /**
     * Sets ith "ItemAttachment" element
     */
    void setItemAttachmentArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType itemAttachment);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "ItemAttachment" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType insertNewItemAttachment(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "ItemAttachment" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType addNewItemAttachment();

    /**
     * Removes the ith "ItemAttachment" element
     */
    void removeItemAttachment(int i);
}
