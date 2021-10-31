/*
 * An XML document type.
 * Localname: CommandFileRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandFileRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one CommandFileRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandFileRemoveDocument extends com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandFileRemoveDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "commandfileremovea92edoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "CommandFileRemove" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandFileRemoveType getCommandFileRemove();

    /**
     * Sets the "CommandFileRemove" element
     */
    void setCommandFileRemove(com.io7m.cardant.protocol.inventory.v1.beans.CommandFileRemoveType commandFileRemove);

    /**
     * Appends and returns a new empty "CommandFileRemove" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandFileRemoveType addNewCommandFileRemove();
}
