/*
 * An XML document type.
 * Localname: ResponseItemRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ResponseItemRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseItemRemoveDocument extends com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRemoveDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s2F5B3CB3EEF95D40ACF30F098DD12ED2.TypeSystemHolder.typeSystem, "responseitemremove076fdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ResponseItemRemove" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRemoveType getResponseItemRemove();

    /**
     * Sets the "ResponseItemRemove" element
     */
    void setResponseItemRemove(com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRemoveType responseItemRemove);

    /**
     * Appends and returns a new empty "ResponseItemRemove" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRemoveType addNewResponseItemRemove();
}
