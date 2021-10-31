/*
 * An XML document type.
 * Localname: ResponseItemsRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemsRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ResponseItemsRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseItemsRemoveDocument extends com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemsRemoveDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "responseitemsremove4356doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ResponseItemsRemove" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemsRemoveType getResponseItemsRemove();

    /**
     * Sets the "ResponseItemsRemove" element
     */
    void setResponseItemsRemove(com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemsRemoveType responseItemsRemove);

    /**
     * Appends and returns a new empty "ResponseItemsRemove" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemsRemoveType addNewResponseItemsRemove();
}
