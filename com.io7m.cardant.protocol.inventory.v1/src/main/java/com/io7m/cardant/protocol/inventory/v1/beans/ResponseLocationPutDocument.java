/*
 * An XML document type.
 * Localname: ResponseLocationPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ResponseLocationPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseLocationPutDocument extends com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationPutDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sB4E2B3A435FC84169BAD368044F7CCA6.TypeSystemHolder.typeSystem, "responselocationputeb60doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ResponseLocationPut" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationPutType getResponseLocationPut();

    /**
     * Sets the "ResponseLocationPut" element
     */
    void setResponseLocationPut(com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationPutType responseLocationPut);

    /**
     * Appends and returns a new empty "ResponseLocationPut" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationPutType addNewResponseLocationPut();
}
