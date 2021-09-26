/*
 * An XML document type.
 * Localname: CommandItemAttachmentPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one CommandItemAttachmentPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandItemAttachmentPutDocument extends com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentPutDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE8AC4557B7260DDF20EBB7BA8F2F0FBA.TypeSystemHolder.typeSystem, "commanditemattachmentput4ef9doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "CommandItemAttachmentPut" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentPutType getCommandItemAttachmentPut();

    /**
     * Sets the "CommandItemAttachmentPut" element
     */
    void setCommandItemAttachmentPut(com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentPutType commandItemAttachmentPut);

    /**
     * Appends and returns a new empty "CommandItemAttachmentPut" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentPutType addNewCommandItemAttachmentPut();
}
