/*
 * An XML document type.
 * Localname: ListLocationsAll
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ListLocationsAll(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ListLocationsAllDocument extends com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsBehaviourDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE8AC4557B7260DDF20EBB7BA8F2F0FBA.TypeSystemHolder.typeSystem, "listlocationsall4dc6doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ListLocationsAll" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllType getListLocationsAll();

    /**
     * Sets the "ListLocationsAll" element
     */
    void setListLocationsAll(com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllType listLocationsAll);

    /**
     * Appends and returns a new empty "ListLocationsAll" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllType addNewListLocationsAll();
}
