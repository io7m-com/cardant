/*
 * XML Type:  ListLocationExactType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML ListLocationExactType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ListLocationExactTypeImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ListLocationsBehaviourTypeImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType {
    private static final long serialVersionUID = 1L;

    public ListLocationExactTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("", "location"),
    };


    /**
     * Gets the "location" attribute
     */
    @Override
    public java.lang.String getLocation() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "location" attribute
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.UUIDType xgetLocation() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.UUIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.UUIDType)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Sets the "location" attribute
     */
    @Override
    public void setLocation(java.lang.String location) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[0]);
            }
            target.setStringValue(location);
        }
    }

    /**
     * Sets (as xml) the "location" attribute
     */
    @Override
    public void xsetLocation(com.io7m.cardant.protocol.inventory.v1.beans.UUIDType location) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.UUIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.UUIDType)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (com.io7m.cardant.protocol.inventory.v1.beans.UUIDType)get_store().add_attribute_user(PROPERTY_QNAME[0]);
            }
            target.set(location);
        }
    }
}
