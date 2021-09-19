/*
 * An XML document type.
 * Localname: Location
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.LocationDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one Location(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class LocationDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.LocationDocument {
    private static final long serialVersionUID = 1L;

    public LocationDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "Location"),
    };


    /**
     * Gets the "Location" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.LocationType getLocation() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.LocationType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.LocationType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "Location" element
     */
    @Override
    public void setLocation(com.io7m.cardant.protocol.inventory.v1.beans.LocationType location) {
        generatedSetterHelperImpl(location, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "Location" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.LocationType addNewLocation() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.LocationType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.LocationType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
