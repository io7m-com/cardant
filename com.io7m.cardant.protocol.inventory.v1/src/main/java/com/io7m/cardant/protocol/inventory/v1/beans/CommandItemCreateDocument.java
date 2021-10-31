/*
 * An XML document type.
 * Localname: CommandItemCreate
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemCreateDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one CommandItemCreate(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandItemCreateDocument extends com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandItemCreateDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "commanditemcreatede3fdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "CommandItemCreate" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandItemCreateType getCommandItemCreate();

    /**
     * Sets the "CommandItemCreate" element
     */
    void setCommandItemCreate(com.io7m.cardant.protocol.inventory.v1.beans.CommandItemCreateType commandItemCreate);

    /**
     * Appends and returns a new empty "CommandItemCreate" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandItemCreateType addNewCommandItemCreate();
}
