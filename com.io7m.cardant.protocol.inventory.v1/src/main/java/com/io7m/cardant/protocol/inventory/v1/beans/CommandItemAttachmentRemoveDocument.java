/*
 * An XML document type.
 * Localname: CommandItemAttachmentRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one CommandItemAttachmentRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandItemAttachmentRemoveDocument extends com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentRemoveDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE8AC4557B7260DDF20EBB7BA8F2F0FBA.TypeSystemHolder.typeSystem, "commanditemattachmentremovec754doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "CommandItemAttachmentRemove" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentRemoveType getCommandItemAttachmentRemove();

    /**
     * Sets the "CommandItemAttachmentRemove" element
     */
    void setCommandItemAttachmentRemove(com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentRemoveType commandItemAttachmentRemove);

    /**
     * Appends and returns a new empty "CommandItemAttachmentRemove" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentRemoveType addNewCommandItemAttachmentRemove();
}
