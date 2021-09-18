/*
 * An XML document type.
 * Localname: EventUpdated
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.EventUpdatedDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one EventUpdated(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface EventUpdatedDocument extends com.io7m.cardant.protocol.inventory.v1.beans.EventDocument {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.EventUpdatedDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.s2F5B3CB3EEF95D40ACF30F098DD12ED2.TypeSystemHolder.typeSystem, "eventupdated65a6doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "EventUpdated" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.EventUpdatedType getEventUpdated();

    /**
     * Sets the "EventUpdated" element
     */
    void setEventUpdated(com.io7m.cardant.protocol.inventory.v1.beans.EventUpdatedType eventUpdated);

    /**
     * Appends and returns a new empty "EventUpdated" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.EventUpdatedType addNewEventUpdated();
}
