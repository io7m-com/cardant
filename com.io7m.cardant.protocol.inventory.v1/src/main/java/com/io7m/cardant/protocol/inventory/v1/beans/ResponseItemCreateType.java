/*
 * XML Type:  ResponseItemCreateType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemCreateType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ResponseItemCreateType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface ResponseItemCreateType extends com.io7m.cardant.protocol.inventory.v1.beans.ResponseType {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemCreateType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE8AC4557B7260DDF20EBB7BA8F2F0FBA.TypeSystemHolder.typeSystem, "responseitemcreatetype7a6dtype");
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
