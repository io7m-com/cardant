/*
 * An XML document type.
 * Localname: CommandItemLocationList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one CommandItemLocationList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandItemLocationListDocument extends com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationListDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s76DE06BD1DB329CBFB2257F5CD3D6E75.TypeSystemHolder.typeSystem, "commanditemlocationlist8fe8doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "CommandItemLocationList" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationListType getCommandItemLocationList();

    /**
     * Sets the "CommandItemLocationList" element
     */
    void setCommandItemLocationList(com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationListType commandItemLocationList);

    /**
     * Appends and returns a new empty "CommandItemLocationList" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationListType addNewCommandItemLocationList();
}
