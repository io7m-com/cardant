/*
 * An XML document type.
 * Localname: Updated
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.UpdatedDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one Updated(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface UpdatedDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.UpdatedDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "updatedd87edoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Updated" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.UpdatedType getUpdated();

    /**
     * Sets the "Updated" element
     */
    void setUpdated(com.io7m.cardant.protocol.inventory.v1.beans.UpdatedType updated);

    /**
     * Appends and returns a new empty "Updated" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.UpdatedType addNewUpdated();
}
