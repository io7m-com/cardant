/*
 * An XML document type.
 * Localname: ResponseItemAttachmentPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ResponseItemAttachmentPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseItemAttachmentPutDocument extends com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentPutDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s76DE06BD1DB329CBFB2257F5CD3D6E75.TypeSystemHolder.typeSystem, "responseitemattachmentput4041doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ResponseItemAttachmentPut" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentPutType getResponseItemAttachmentPut();

    /**
     * Sets the "ResponseItemAttachmentPut" element
     */
    void setResponseItemAttachmentPut(com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentPutType responseItemAttachmentPut);

    /**
     * Appends and returns a new empty "ResponseItemAttachmentPut" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentPutType addNewResponseItemAttachmentPut();
}
