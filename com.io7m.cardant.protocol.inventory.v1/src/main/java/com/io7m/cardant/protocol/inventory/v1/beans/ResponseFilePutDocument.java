/*
 * An XML document type.
 * Localname: ResponseFilePut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseFilePutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ResponseFilePut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseFilePutDocument extends com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseFilePutDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "responsefileputb6a7doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ResponseFilePut" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseFilePutType getResponseFilePut();

    /**
     * Sets the "ResponseFilePut" element
     */
    void setResponseFilePut(com.io7m.cardant.protocol.inventory.v1.beans.ResponseFilePutType responseFilePut);

    /**
     * Appends and returns a new empty "ResponseFilePut" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseFilePutType addNewResponseFilePut();
}
