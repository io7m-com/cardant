/*
 * An XML document type.
 * Localname: ResponseErrorAttributes
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ResponseErrorAttributes(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseErrorAttributesDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sB4E2B3A435FC84169BAD368044F7CCA6.TypeSystemHolder.typeSystem, "responseerrorattributes949bdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ResponseErrorAttributes" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesType getResponseErrorAttributes();

    /**
     * Sets the "ResponseErrorAttributes" element
     */
    void setResponseErrorAttributes(com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesType responseErrorAttributes);

    /**
     * Appends and returns a new empty "ResponseErrorAttributes" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesType addNewResponseErrorAttributes();
}
