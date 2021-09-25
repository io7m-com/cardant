/*
 * An XML document type.
 * Localname: CommandItemLocationsList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationsListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one CommandItemLocationsList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandItemLocationsListDocument extends com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationsListDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sB4E2B3A435FC84169BAD368044F7CCA6.TypeSystemHolder.typeSystem, "commanditemlocationsliste2c9doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "CommandItemLocationsList" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationsListType getCommandItemLocationsList();

    /**
     * Sets the "CommandItemLocationsList" element
     */
    void setCommandItemLocationsList(com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationsListType commandItemLocationsList);

    /**
     * Appends and returns a new empty "CommandItemLocationsList" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationsListType addNewCommandItemLocationsList();
}
