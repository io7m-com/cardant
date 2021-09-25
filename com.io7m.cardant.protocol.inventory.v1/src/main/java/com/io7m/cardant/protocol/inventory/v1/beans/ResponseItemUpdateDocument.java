/*
 * An XML document type.
 * Localname: ResponseItemUpdate
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemUpdateDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ResponseItemUpdate(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseItemUpdateDocument extends com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemUpdateDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sB4E2B3A435FC84169BAD368044F7CCA6.TypeSystemHolder.typeSystem, "responseitemupdatea3cadoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ResponseItemUpdate" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemUpdateType getResponseItemUpdate();

    /**
     * Sets the "ResponseItemUpdate" element
     */
    void setResponseItemUpdate(com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemUpdateType responseItemUpdate);

    /**
     * Appends and returns a new empty "ResponseItemUpdate" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemUpdateType addNewResponseItemUpdate();
}
