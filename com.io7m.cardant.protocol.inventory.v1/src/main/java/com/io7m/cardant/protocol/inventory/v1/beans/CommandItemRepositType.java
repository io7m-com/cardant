/*
 * XML Type:  CommandItemRepositType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRepositType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML CommandItemRepositType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface CommandItemRepositType extends com.io7m.cardant.protocol.inventory.v1.beans.CommandType {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRepositType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder.typeSystem, "commanditemreposittypea8eftype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ItemReposit" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositType getItemReposit();

    /**
     * Sets the "ItemReposit" element
     */
    void setItemReposit(com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositType itemReposit);

    /**
     * Appends and returns a new empty "ItemReposit" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositType addNewItemReposit();
}
