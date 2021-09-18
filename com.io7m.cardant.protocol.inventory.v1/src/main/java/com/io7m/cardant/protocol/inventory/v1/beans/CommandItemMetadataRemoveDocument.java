/*
 * An XML document type.
 * Localname: CommandItemMetadataRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one CommandItemMetadataRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandItemMetadataRemoveDocument extends com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s2F5B3CB3EEF95D40ACF30F098DD12ED2.TypeSystemHolder.typeSystem, "commanditemmetadataremove6288doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "CommandItemMetadataRemove" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveType getCommandItemMetadataRemove();

    /**
     * Sets the "CommandItemMetadataRemove" element
     */
    void setCommandItemMetadataRemove(com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveType commandItemMetadataRemove);

    /**
     * Appends and returns a new empty "CommandItemMetadataRemove" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveType addNewCommandItemMetadataRemove();
}
