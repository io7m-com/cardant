/*
 * An XML document type.
 * Localname: CommandLoginUsernamePassword
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one CommandLoginUsernamePassword(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface CommandLoginUsernamePasswordDocument extends com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sB4E2B3A435FC84169BAD368044F7CCA6.TypeSystemHolder.typeSystem, "commandloginusernamepassword3d98doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "CommandLoginUsernamePassword" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordType getCommandLoginUsernamePassword();

    /**
     * Sets the "CommandLoginUsernamePassword" element
     */
    void setCommandLoginUsernamePassword(com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordType commandLoginUsernamePassword);

    /**
     * Appends and returns a new empty "CommandLoginUsernamePassword" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordType addNewCommandLoginUsernamePassword();
}
