/*
 * An XML document type.
 * Localname: LocationID
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.LocationIDDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one LocationID(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class LocationIDDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.IDDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.LocationIDDocument {
    private static final long serialVersionUID = 1L;

    public LocationIDDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "LocationID"),
    };


    /**
     * Gets the "LocationID" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.LocationIDType getLocationID() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.LocationIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.LocationIDType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "LocationID" element
     */
    @Override
    public void setLocationID(com.io7m.cardant.protocol.inventory.v1.beans.LocationIDType locationID) {
        generatedSetterHelperImpl(locationID, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "LocationID" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.LocationIDType addNewLocationID() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.LocationIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.LocationIDType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
