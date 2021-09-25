/*
 * XML Type:  ResponseItemGetType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemGetType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ResponseItemGetType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ResponseItemGetType extends com.io7m.cardant.protocol.inventory.v1.beans.ResponseType {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemGetType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sB4E2B3A435FC84169BAD368044F7CCA6.TypeSystemHolder.typeSystem, "responseitemgettype5badtype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Item" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemType getItem();

    /**
     * Sets the "Item" element
     */
    void setItem(com.io7m.cardant.protocol.inventory.v1.beans.ItemType item);

    /**
     * Appends and returns a new empty "Item" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemType addNewItem();
}
