/*
 * XML Type:  EventUpdatedType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.EventUpdatedType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML EventUpdatedType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface EventUpdatedType extends com.io7m.cardant.protocol.inventory.v1.beans.EventType {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.EventUpdatedType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder.typeSystem, "eventupdatedtype6e9ctype");
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
