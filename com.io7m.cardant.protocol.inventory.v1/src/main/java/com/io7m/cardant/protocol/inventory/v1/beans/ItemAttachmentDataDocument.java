/*
 * An XML document type.
 * Localname: ItemAttachmentData
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDataDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ItemAttachmentData(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ItemAttachmentDataDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDataDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s2F5B3CB3EEF95D40ACF30F098DD12ED2.TypeSystemHolder.typeSystem, "itemattachmentdata3ba7doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ItemAttachmentData" element
     */
    byte[] getItemAttachmentData();

    /**
     * Gets (as xml) the "ItemAttachmentData" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDataType xgetItemAttachmentData();

    /**
     * Sets the "ItemAttachmentData" element
     */
    void setItemAttachmentData(byte[] itemAttachmentData);

    /**
     * Sets (as xml) the "ItemAttachmentData" element
     */
    void xsetItemAttachmentData(com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDataType itemAttachmentData);
}
