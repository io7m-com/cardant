/*
 * An XML document type.
 * Localname: ResponseItemReposit
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRepositDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ResponseItemReposit(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseItemRepositDocument extends com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRepositDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sB4E2B3A435FC84169BAD368044F7CCA6.TypeSystemHolder.typeSystem, "responseitemreposit94c1doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ResponseItemReposit" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRepositType getResponseItemReposit();

    /**
     * Sets the "ResponseItemReposit" element
     */
    void setResponseItemReposit(com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRepositType responseItemReposit);

    /**
     * Appends and returns a new empty "ResponseItemReposit" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRepositType addNewResponseItemReposit();
}
