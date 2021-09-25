/*
 * An XML document type.
 * Localname: ResponseErrorDetails
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ResponseErrorDetails(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseErrorDetailsDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sB4E2B3A435FC84169BAD368044F7CCA6.TypeSystemHolder.typeSystem, "responseerrordetailsd4acdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ResponseErrorDetails" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsType getResponseErrorDetails();

    /**
     * Sets the "ResponseErrorDetails" element
     */
    void setResponseErrorDetails(com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsType responseErrorDetails);

    /**
     * Appends and returns a new empty "ResponseErrorDetails" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsType addNewResponseErrorDetails();
}
