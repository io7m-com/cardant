/*
 * An XML document type.
 * Localname: ResponseItemCreate
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemCreateDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ResponseItemCreate(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseItemCreateDocument extends com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemCreateDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sB4E2B3A435FC84169BAD368044F7CCA6.TypeSystemHolder.typeSystem, "responseitemcreate4df7doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ResponseItemCreate" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemCreateType getResponseItemCreate();

    /**
     * Sets the "ResponseItemCreate" element
     */
    void setResponseItemCreate(com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemCreateType responseItemCreate);

    /**
     * Appends and returns a new empty "ResponseItemCreate" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemCreateType addNewResponseItemCreate();
}
