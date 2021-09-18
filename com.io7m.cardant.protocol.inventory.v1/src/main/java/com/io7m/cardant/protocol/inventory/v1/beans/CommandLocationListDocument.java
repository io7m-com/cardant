/*
 * An XML document type.
 * Localname: CommandLocationList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one CommandLocationList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandLocationListDocument extends com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationListDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s2F5B3CB3EEF95D40ACF30F098DD12ED2.TypeSystemHolder.typeSystem, "commandlocationlistef3bdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "CommandLocationList" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationListType getCommandLocationList();

    /**
     * Sets the "CommandLocationList" element
     */
    void setCommandLocationList(com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationListType commandLocationList);

    /**
     * Appends and returns a new empty "CommandLocationList" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationListType addNewCommandLocationList();
}
