/*
 * An XML document type.
 * Localname: CommandItemsRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemsRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one CommandItemsRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandItemsRemoveDocument extends com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandItemsRemoveDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "commanditemsremovebc0edoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "CommandItemsRemove" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandItemsRemoveType getCommandItemsRemove();

    /**
     * Sets the "CommandItemsRemove" element
     */
    void setCommandItemsRemove(com.io7m.cardant.protocol.inventory.v1.beans.CommandItemsRemoveType commandItemsRemove);

    /**
     * Appends and returns a new empty "CommandItemsRemove" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandItemsRemoveType addNewCommandItemsRemove();
}
