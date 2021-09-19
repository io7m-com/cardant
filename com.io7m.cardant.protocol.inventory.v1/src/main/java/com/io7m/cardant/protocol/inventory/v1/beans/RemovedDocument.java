/*
 * An XML document type.
 * Localname: Removed
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.RemovedDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one Removed(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface RemovedDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.RemovedDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s76DE06BD1DB329CBFB2257F5CD3D6E75.TypeSystemHolder.typeSystem, "removede979doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Removed" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.RemovedType getRemoved();

    /**
     * Sets the "Removed" element
     */
    void setRemoved(com.io7m.cardant.protocol.inventory.v1.beans.RemovedType removed);

    /**
     * Appends and returns a new empty "Removed" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.RemovedType addNewRemoved();
}
