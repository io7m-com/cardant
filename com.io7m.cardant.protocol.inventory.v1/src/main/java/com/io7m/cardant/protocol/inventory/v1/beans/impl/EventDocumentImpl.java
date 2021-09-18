/*
 * An XML document type.
 * Localname: Event
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.EventDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one Event(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class EventDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.MessageDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.EventDocument {
    private static final long serialVersionUID = 1L;

    public EventDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "Event"),
    };

    private static final QNameSet[] PROPERTY_QSET = {
    QNameSet.forArray( new QName[] { 
        new QName("urn:com.io7m.cardant.inventory:1", "Event"),
        new QName("urn:com.io7m.cardant.inventory:1", "EventUpdated"),
    }),
    };

    /**
     * Gets the "Event" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.EventType getEvent() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.EventType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.EventType)get_store().find_element_user(PROPERTY_QSET[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "Event" element
     */
    @Override
    public void setEvent(com.io7m.cardant.protocol.inventory.v1.beans.EventType event) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.EventType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.EventType)get_store().find_element_user(PROPERTY_QSET[0], 0);
            if (target == null) {
                target = (com.io7m.cardant.protocol.inventory.v1.beans.EventType)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.set(event);
        }
    }

    /**
     * Appends and returns a new empty "Event" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.EventType addNewEvent() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.EventType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.EventType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
