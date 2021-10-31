/*
 * An XML document type.
 * Localname: ResponseTagsDelete
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsDeleteDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ResponseTagsDelete(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseTagsDeleteDocument extends com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsDeleteDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "responsetagsdeletec762doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ResponseTagsDelete" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsDeleteType getResponseTagsDelete();

    /**
     * Sets the "ResponseTagsDelete" element
     */
    void setResponseTagsDelete(com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsDeleteType responseTagsDelete);

    /**
     * Appends and returns a new empty "ResponseTagsDelete" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsDeleteType addNewResponseTagsDelete();
}
