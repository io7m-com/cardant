/*
 * An XML document type.
 * Localname: CommandLocationPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one CommandLocationPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandLocationPutDocument extends com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationPutDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sB4E2B3A435FC84169BAD368044F7CCA6.TypeSystemHolder.typeSystem, "commandlocationput6418doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "CommandLocationPut" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationPutType getCommandLocationPut();

    /**
     * Sets the "CommandLocationPut" element
     */
    void setCommandLocationPut(com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationPutType commandLocationPut);

    /**
     * Appends and returns a new empty "CommandLocationPut" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationPutType addNewCommandLocationPut();
}
