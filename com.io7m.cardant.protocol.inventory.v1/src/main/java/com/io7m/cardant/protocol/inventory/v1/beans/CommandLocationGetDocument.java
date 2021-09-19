/*
 * An XML document type.
 * Localname: CommandLocationGet
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationGetDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one CommandLocationGet(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandLocationGetDocument extends com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationGetDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s76DE06BD1DB329CBFB2257F5CD3D6E75.TypeSystemHolder.typeSystem, "commandlocationget6cb1doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "CommandLocationGet" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationGetType getCommandLocationGet();

    /**
     * Sets the "CommandLocationGet" element
     */
    void setCommandLocationGet(com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationGetType commandLocationGet);

    /**
     * Appends and returns a new empty "CommandLocationGet" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationGetType addNewCommandLocationGet();
}
