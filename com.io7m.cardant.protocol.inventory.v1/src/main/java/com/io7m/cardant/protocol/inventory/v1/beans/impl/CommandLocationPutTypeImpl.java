/*
 * XML Type:  CommandLocationPutType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationPutType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML CommandLocationPutType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class CommandLocationPutTypeImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandTypeImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationPutType {
    private static final long serialVersionUID = 1L;

    public CommandLocationPutTypeImpl(org.apache.xmlbeans.SchemaType sType) {
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
