/*
 * An XML document type.
 * Localname: EventUpdated
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.EventUpdatedDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one EventUpdated(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class EventUpdatedDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.EventDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.EventUpdatedDocument {
    private static final long serialVersionUID = 1L;

    public EventUpdatedDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "EventUpdated"),
    };


    /**
     * Gets the "EventUpdated" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.EventUpdatedType getEventUpdated() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.EventUpdatedType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.EventUpdatedType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "EventUpdated" element
     */
    @Override
    public void setEventUpdated(com.io7m.cardant.protocol.inventory.v1.beans.EventUpdatedType eventUpdated) {
        generatedSetterHelperImpl(eventUpdated, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "EventUpdated" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.EventUpdatedType addNewEventUpdated() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.EventUpdatedType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.EventUpdatedType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
