/*
 * An XML document type.
 * Localname: CommandItemAttachmentAdd
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentAddDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one CommandItemAttachmentAdd(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandItemAttachmentAddDocument extends com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentAddDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "commanditemattachmentadd03a7doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "CommandItemAttachmentAdd" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentAddType getCommandItemAttachmentAdd();

    /**
     * Sets the "CommandItemAttachmentAdd" element
     */
    void setCommandItemAttachmentAdd(com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentAddType commandItemAttachmentAdd);

    /**
     * Appends and returns a new empty "CommandItemAttachmentAdd" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentAddType addNewCommandItemAttachmentAdd();
}
