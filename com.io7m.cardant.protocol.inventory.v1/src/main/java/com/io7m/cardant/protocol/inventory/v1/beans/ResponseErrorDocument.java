/*
 * An XML document type.
 * Localname: ResponseError
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ResponseError(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseErrorDocument extends com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s2F5B3CB3EEF95D40ACF30F098DD12ED2.TypeSystemHolder.typeSystem, "responseerror75b2doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ResponseError" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorType getResponseError();

    /**
     * Sets the "ResponseError" element
     */
    void setResponseError(com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorType responseError);

    /**
     * Appends and returns a new empty "ResponseError" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorType addNewResponseError();
}
