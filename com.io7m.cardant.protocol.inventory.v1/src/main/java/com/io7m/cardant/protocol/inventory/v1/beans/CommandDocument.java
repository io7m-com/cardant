/*
 * An XML document type.
 * Localname: Command
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one Command(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandDocument extends com.io7m.cardant.protocol.inventory.v1.beans.MessageDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "commandebeedoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Command" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandType getCommand();

    /**
     * Sets the "Command" element
     */
    void setCommand(com.io7m.cardant.protocol.inventory.v1.beans.CommandType command);

    /**
     * Appends and returns a new empty "Command" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandType addNewCommand();
}
