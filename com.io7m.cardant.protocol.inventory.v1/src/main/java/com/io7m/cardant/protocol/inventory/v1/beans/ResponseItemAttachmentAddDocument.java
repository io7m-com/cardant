/*
 * An XML document type.
 * Localname: ResponseItemAttachmentAdd
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentAddDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ResponseItemAttachmentAdd(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseItemAttachmentAddDocument extends com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentAddDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "responseitemattachmentaddf4efdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ResponseItemAttachmentAdd" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentAddType getResponseItemAttachmentAdd();

    /**
     * Sets the "ResponseItemAttachmentAdd" element
     */
    void setResponseItemAttachmentAdd(com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentAddType responseItemAttachmentAdd);

    /**
     * Appends and returns a new empty "ResponseItemAttachmentAdd" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentAddType addNewResponseItemAttachmentAdd();
}
