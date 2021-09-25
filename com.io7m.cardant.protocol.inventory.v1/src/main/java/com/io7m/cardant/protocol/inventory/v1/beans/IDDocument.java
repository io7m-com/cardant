/*
 * An XML document type.
 * Localname: ID
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.IDDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ID(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface IDDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.IDDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sB4E2B3A435FC84169BAD368044F7CCA6.TypeSystemHolder.typeSystem, "id996cdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ID" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.IDType getID();

    /**
     * Sets the "ID" element
     */
    void setID(com.io7m.cardant.protocol.inventory.v1.beans.IDType id);

    /**
     * Appends and returns a new empty "ID" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.IDType addNewID();
}
