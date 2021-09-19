/*
 * An XML document type.
 * Localname: CommandItemMetadataPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one CommandItemMetadataPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandItemMetadataPutDocument extends com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s76DE06BD1DB329CBFB2257F5CD3D6E75.TypeSystemHolder.typeSystem, "commanditemmetadataputa045doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "CommandItemMetadataPut" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutType getCommandItemMetadataPut();

    /**
     * Sets the "CommandItemMetadataPut" element
     */
    void setCommandItemMetadataPut(com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutType commandItemMetadataPut);

    /**
     * Appends and returns a new empty "CommandItemMetadataPut" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutType addNewCommandItemMetadataPut();
}
