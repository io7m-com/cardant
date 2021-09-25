/*
 * An XML document type.
 * Localname: ResponseErrorAttribute
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ResponseErrorAttribute(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseErrorAttributeDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sB4E2B3A435FC84169BAD368044F7CCA6.TypeSystemHolder.typeSystem, "responseerrorattributeac12doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ResponseErrorAttribute" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType getResponseErrorAttribute();

    /**
     * Sets the "ResponseErrorAttribute" element
     */
    void setResponseErrorAttribute(com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType responseErrorAttribute);

    /**
     * Appends and returns a new empty "ResponseErrorAttribute" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType addNewResponseErrorAttribute();
}
