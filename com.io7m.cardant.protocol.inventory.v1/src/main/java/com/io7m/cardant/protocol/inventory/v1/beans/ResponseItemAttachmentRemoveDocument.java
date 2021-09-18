/*
 * An XML document type.
 * Localname: ResponseItemAttachmentRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ResponseItemAttachmentRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseItemAttachmentRemoveDocument extends com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentRemoveDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder.typeSystem, "responseitemattachmentremovef10cdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ResponseItemAttachmentRemove" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentRemoveType getResponseItemAttachmentRemove();

    /**
     * Sets the "ResponseItemAttachmentRemove" element
     */
    void setResponseItemAttachmentRemove(com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentRemoveType responseItemAttachmentRemove);

    /**
     * Appends and returns a new empty "ResponseItemAttachmentRemove" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentRemoveType addNewResponseItemAttachmentRemove();
}
