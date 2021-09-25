/*
 * An XML document type.
 * Localname: Response
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one Response(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseDocument extends com.io7m.cardant.protocol.inventory.v1.beans.MessageDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sB4E2B3A435FC84169BAD368044F7CCA6.TypeSystemHolder.typeSystem, "response21a6doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Response" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseType getResponse();

    /**
     * Sets the "Response" element
     */
    void setResponse(com.io7m.cardant.protocol.inventory.v1.beans.ResponseType response);

    /**
     * Appends and returns a new empty "Response" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseType addNewResponse();
}
