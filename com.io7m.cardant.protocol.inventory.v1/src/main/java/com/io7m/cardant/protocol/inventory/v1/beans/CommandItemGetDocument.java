/*
 * An XML document type.
 * Localname: CommandItemGet
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemGetDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one CommandItemGet(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandItemGetDocument extends com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandItemGetDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE8AC4557B7260DDF20EBB7BA8F2F0FBA.TypeSystemHolder.typeSystem, "commanditemget1a0fdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "CommandItemGet" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandItemGetType getCommandItemGet();

    /**
     * Sets the "CommandItemGet" element
     */
    void setCommandItemGet(com.io7m.cardant.protocol.inventory.v1.beans.CommandItemGetType commandItemGet);

    /**
     * Appends and returns a new empty "CommandItemGet" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandItemGetType addNewCommandItemGet();
}
