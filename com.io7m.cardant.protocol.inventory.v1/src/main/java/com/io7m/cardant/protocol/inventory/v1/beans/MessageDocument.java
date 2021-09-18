/*
 * An XML document type.
 * Localname: Message
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.MessageDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one Message(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface MessageDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.MessageDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s2F5B3CB3EEF95D40ACF30F098DD12ED2.TypeSystemHolder.typeSystem, "messaged2b2doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Message" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.MessageType getMessage();

    /**
     * Sets the "Message" element
     */
    void setMessage(com.io7m.cardant.protocol.inventory.v1.beans.MessageType message);

    /**
     * Appends and returns a new empty "Message" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.MessageType addNewMessage();
}
