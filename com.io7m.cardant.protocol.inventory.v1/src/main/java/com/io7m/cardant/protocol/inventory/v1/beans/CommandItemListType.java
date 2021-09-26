/*
 * XML Type:  CommandItemListType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemListType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML CommandItemListType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface CommandItemListType extends com.io7m.cardant.protocol.inventory.v1.beans.CommandType {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandItemListType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE8AC4557B7260DDF20EBB7BA8F2F0FBA.TypeSystemHolder.typeSystem, "commanditemlisttype30f3type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ListLocationsAll" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllType getListLocationsAll();

    /**
     * True if has "ListLocationsAll" element
     */
    boolean isSetListLocationsAll();

    /**
     * Sets the "ListLocationsAll" element
     */
    void setListLocationsAll(com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllType listLocationsAll);

    /**
     * Appends and returns a new empty "ListLocationsAll" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllType addNewListLocationsAll();

    /**
     * Unsets the "ListLocationsAll" element
     */
    void unsetListLocationsAll();

    /**
     * Gets the "ListLocationExact" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType getListLocationExact();

    /**
     * True if has "ListLocationExact" element
     */
    boolean isSetListLocationExact();

    /**
     * Sets the "ListLocationExact" element
     */
    void setListLocationExact(com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType listLocationExact);

    /**
     * Appends and returns a new empty "ListLocationExact" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType addNewListLocationExact();

    /**
     * Unsets the "ListLocationExact" element
     */
    void unsetListLocationExact();

    /**
     * Gets the "ListLocationWithDescendants" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsType getListLocationWithDescendants();

    /**
     * True if has "ListLocationWithDescendants" element
     */
    boolean isSetListLocationWithDescendants();

    /**
     * Sets the "ListLocationWithDescendants" element
     */
    void setListLocationWithDescendants(com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsType listLocationWithDescendants);

    /**
     * Appends and returns a new empty "ListLocationWithDescendants" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsType addNewListLocationWithDescendants();

    /**
     * Unsets the "ListLocationWithDescendants" element
     */
    void unsetListLocationWithDescendants();
}
