/*
 * An XML document type.
 * Localname: ResponseItemGet
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemGetDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ResponseItemGet(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseItemGetDocument extends com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemGetDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder.typeSystem, "responseitemget8557doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ResponseItemGet" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemGetType getResponseItemGet();

    /**
     * Sets the "ResponseItemGet" element
     */
    void setResponseItemGet(com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemGetType responseItemGet);

    /**
     * Appends and returns a new empty "ResponseItemGet" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemGetType addNewResponseItemGet();
}
