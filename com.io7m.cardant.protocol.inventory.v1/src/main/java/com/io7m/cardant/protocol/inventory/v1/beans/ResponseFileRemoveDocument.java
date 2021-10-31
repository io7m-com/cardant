/*
 * An XML document type.
 * Localname: ResponseFileRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseFileRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ResponseFileRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseFileRemoveDocument extends com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseFileRemoveDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "responsefileremove18e6doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ResponseFileRemove" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseFileRemoveType getResponseFileRemove();

    /**
     * Sets the "ResponseFileRemove" element
     */
    void setResponseFileRemove(com.io7m.cardant.protocol.inventory.v1.beans.ResponseFileRemoveType responseFileRemove);

    /**
     * Appends and returns a new empty "ResponseFileRemove" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseFileRemoveType addNewResponseFileRemove();
}
