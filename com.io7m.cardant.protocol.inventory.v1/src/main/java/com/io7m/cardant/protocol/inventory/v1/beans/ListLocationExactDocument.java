/*
 * An XML document type.
 * Localname: ListLocationExact
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ListLocationExact(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ListLocationExactDocument extends com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsBehaviourDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder.typeSystem, "listlocationexact0fcddoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ListLocationExact" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType getListLocationExact();

    /**
     * Sets the "ListLocationExact" element
     */
    void setListLocationExact(com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType listLocationExact);

    /**
     * Appends and returns a new empty "ListLocationExact" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType addNewListLocationExact();
}
