/*
 * An XML document type.
 * Localname: CommandTagsPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one CommandTagsPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandTagsPutDocument extends com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsPutDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s76DE06BD1DB329CBFB2257F5CD3D6E75.TypeSystemHolder.typeSystem, "commandtagsput3f7cdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "CommandTagsPut" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsPutType getCommandTagsPut();

    /**
     * Sets the "CommandTagsPut" element
     */
    void setCommandTagsPut(com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsPutType commandTagsPut);

    /**
     * Appends and returns a new empty "CommandTagsPut" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsPutType addNewCommandTagsPut();
}
