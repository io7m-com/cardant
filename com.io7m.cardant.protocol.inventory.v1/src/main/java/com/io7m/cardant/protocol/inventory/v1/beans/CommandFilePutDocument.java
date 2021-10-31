/*
 * An XML document type.
 * Localname: CommandFilePut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandFilePutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one CommandFilePut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandFilePutDocument extends com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandFilePutDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "commandfileput4b5fdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "CommandFilePut" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandFilePutType getCommandFilePut();

    /**
     * Sets the "CommandFilePut" element
     */
    void setCommandFilePut(com.io7m.cardant.protocol.inventory.v1.beans.CommandFilePutType commandFilePut);

    /**
     * Appends and returns a new empty "CommandFilePut" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandFilePutType addNewCommandFilePut();
}
