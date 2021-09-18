/*
 * An XML document type.
 * Localname: ResponseTagsPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ResponseTagsPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseTagsPutDocument extends com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsPutDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder.typeSystem, "responsetagsputaac4doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ResponseTagsPut" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsPutType getResponseTagsPut();

    /**
     * Sets the "ResponseTagsPut" element
     */
    void setResponseTagsPut(com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsPutType responseTagsPut);

    /**
     * Appends and returns a new empty "ResponseTagsPut" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsPutType addNewResponseTagsPut();
}
