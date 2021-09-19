/*
 * An XML document type.
 * Localname: ResponseLocationList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ResponseLocationList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ResponseLocationListDocument extends com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationListDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s76DE06BD1DB329CBFB2257F5CD3D6E75.TypeSystemHolder.typeSystem, "responselocationlist50f3doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ResponseLocationList" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationListType getResponseLocationList();

    /**
     * Sets the "ResponseLocationList" element
     */
    void setResponseLocationList(com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationListType responseLocationList);

    /**
     * Appends and returns a new empty "ResponseLocationList" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationListType addNewResponseLocationList();
}
