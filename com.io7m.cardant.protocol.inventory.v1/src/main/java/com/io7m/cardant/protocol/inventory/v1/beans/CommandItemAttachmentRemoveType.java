/*
 * XML Type:  CommandItemAttachmentRemoveType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentRemoveType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML CommandItemAttachmentRemoveType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface CommandItemAttachmentRemoveType extends com.io7m.cardant.protocol.inventory.v1.beans.CommandType {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentRemoveType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sB4E2B3A435FC84169BAD368044F7CCA6.TypeSystemHolder.typeSystem, "commanditemattachmentremovetypeef2atype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "item" attribute
     */
    java.lang.String getItem();

    /**
     * Gets (as xml) the "item" attribute
     */
    com.io7m.cardant.protocol.inventory.v1.beans.UUIDType xgetItem();

    /**
     * Sets the "item" attribute
     */
    void setItem(java.lang.String item);

    /**
     * Sets (as xml) the "item" attribute
     */
    void xsetItem(com.io7m.cardant.protocol.inventory.v1.beans.UUIDType item);

    /**
     * Gets the "attachment" attribute
     */
    java.lang.String getAttachment();

    /**
     * Gets (as xml) the "attachment" attribute
     */
    com.io7m.cardant.protocol.inventory.v1.beans.UUIDType xgetAttachment();

    /**
     * Sets the "attachment" attribute
     */
    void setAttachment(java.lang.String attachment);

    /**
     * Sets (as xml) the "attachment" attribute
     */
    void xsetAttachment(com.io7m.cardant.protocol.inventory.v1.beans.UUIDType attachment);
}
