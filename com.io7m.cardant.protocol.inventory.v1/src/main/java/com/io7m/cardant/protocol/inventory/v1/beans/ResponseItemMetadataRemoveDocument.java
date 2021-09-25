/*
 * An XML document type.
 * Localname: ResponseItemMetadataRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ResponseItemMetadataRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseItemMetadataRemoveDocument extends com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataRemoveDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sB4E2B3A435FC84169BAD368044F7CCA6.TypeSystemHolder.typeSystem, "responseitemmetadataremove9a40doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ResponseItemMetadataRemove" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataRemoveType getResponseItemMetadataRemove();

    /**
     * Sets the "ResponseItemMetadataRemove" element
     */
    void setResponseItemMetadataRemove(com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataRemoveType responseItemMetadataRemove);

    /**
     * Appends and returns a new empty "ResponseItemMetadataRemove" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataRemoveType addNewResponseItemMetadataRemove();
}
