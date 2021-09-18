/*
 * An XML document type.
 * Localname: ListLocationWithDescendants
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ListLocationWithDescendants(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ListLocationWithDescendantsDocument extends com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsBehaviourDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder.typeSystem, "listlocationwithdescendantsf37cdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ListLocationWithDescendants" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsType getListLocationWithDescendants();

    /**
     * Sets the "ListLocationWithDescendants" element
     */
    void setListLocationWithDescendants(com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsType listLocationWithDescendants);

    /**
     * Appends and returns a new empty "ListLocationWithDescendants" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsType addNewListLocationWithDescendants();
}
