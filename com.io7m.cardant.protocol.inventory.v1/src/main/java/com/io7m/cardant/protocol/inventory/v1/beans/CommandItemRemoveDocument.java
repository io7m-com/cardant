/*
 * An XML document type.
 * Localname: CommandItemRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one CommandItemRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandItemRemoveDocument extends com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRemoveDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s76DE06BD1DB329CBFB2257F5CD3D6E75.TypeSystemHolder.typeSystem, "commanditemremove97b7doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "CommandItemRemove" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRemoveType getCommandItemRemove();

    /**
     * Sets the "CommandItemRemove" element
     */
    void setCommandItemRemove(com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRemoveType commandItemRemove);

    /**
     * Appends and returns a new empty "CommandItemRemove" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRemoveType addNewCommandItemRemove();
}
