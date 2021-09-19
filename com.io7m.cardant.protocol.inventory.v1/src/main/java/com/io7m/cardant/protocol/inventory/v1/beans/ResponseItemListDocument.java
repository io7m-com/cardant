/*
 * An XML document type.
 * Localname: ResponseItemList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ResponseItemList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseItemListDocument extends com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemListDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s76DE06BD1DB329CBFB2257F5CD3D6E75.TypeSystemHolder.typeSystem, "responseitemlisteb55doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ResponseItemList" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemListType getResponseItemList();

    /**
     * Sets the "ResponseItemList" element
     */
    void setResponseItemList(com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemListType responseItemList);

    /**
     * Appends and returns a new empty "ResponseItemList" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemListType addNewResponseItemList();
}
