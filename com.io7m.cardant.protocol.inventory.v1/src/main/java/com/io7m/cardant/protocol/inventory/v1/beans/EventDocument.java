/*
 * An XML document type.
 * Localname: Event
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.EventDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one Event(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface EventDocument extends com.io7m.cardant.protocol.inventory.v1.beans.MessageDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.EventDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "event50bfdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Event" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.EventType getEvent();

    /**
     * Sets the "Event" element
     */
    void setEvent(com.io7m.cardant.protocol.inventory.v1.beans.EventType event);

    /**
     * Appends and returns a new empty "Event" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.EventType addNewEvent();
}
