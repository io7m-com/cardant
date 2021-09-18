/*
 * XML Type:  ItemRepositMoveType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositMoveType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML ItemRepositMoveType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ItemRepositMoveTypeImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ItemRepositTypeImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositMoveType {
    private static final long serialVersionUID = 1L;

    public ItemRepositMoveTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("", "item"),
        new QName("", "fromLocation"),
        new QName("", "toLocation"),
        new QName("", "count"),
    };


    /**
     * Gets the "item" attribute
     */
    @Override
    public java.lang.String getItem() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "item" attribute
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.UUIDType xgetItem() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.UUIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.UUIDType)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Sets the "item" attribute
     */
    @Override
    public void setItem(java.lang.String item) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[0]);
            }
            target.setStringValue(item);
        }
    }

    /**
     * Sets (as xml) the "item" attribute
     */
    @Override
    public void xsetItem(com.io7m.cardant.protocol.inventory.v1.beans.UUIDType item) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.UUIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.UUIDType)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (com.io7m.cardant.protocol.inventory.v1.beans.UUIDType)get_store().add_attribute_user(PROPERTY_QNAME[0]);
            }
            target.set(item);
        }
    }

    /**
     * Gets the "fromLocation" attribute
     */
    @Override
    public java.lang.String getFromLocation() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "fromLocation" attribute
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.UUIDType xgetFromLocation() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.UUIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.UUIDType)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /**
     * Sets the "fromLocation" attribute
     */
    @Override
    public void setFromLocation(java.lang.String fromLocation) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[1]);
            }
            target.setStringValue(fromLocation);
        }
    }

    /**
     * Sets (as xml) the "fromLocation" attribute
     */
    @Override
    public void xsetFromLocation(com.io7m.cardant.protocol.inventory.v1.beans.UUIDType fromLocation) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.UUIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.UUIDType)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (com.io7m.cardant.protocol.inventory.v1.beans.UUIDType)get_store().add_attribute_user(PROPERTY_QNAME[1]);
            }
            target.set(fromLocation);
        }
    }

    /**
     * Gets the "toLocation" attribute
     */
    @Override
    public java.lang.String getToLocation() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[2]);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "toLocation" attribute
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.UUIDType xgetToLocation() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.UUIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.UUIDType)get_store().find_attribute_user(PROPERTY_QNAME[2]);
            return target;
        }
    }

    /**
     * Sets the "toLocation" attribute
     */
    @Override
    public void setToLocation(java.lang.String toLocation) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[2]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[2]);
            }
            target.setStringValue(toLocation);
        }
    }

    /**
     * Sets (as xml) the "toLocation" attribute
     */
    @Override
    public void xsetToLocation(com.io7m.cardant.protocol.inventory.v1.beans.UUIDType toLocation) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.UUIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.UUIDType)get_store().find_attribute_user(PROPERTY_QNAME[2]);
            if (target == null) {
                target = (com.io7m.cardant.protocol.inventory.v1.beans.UUIDType)get_store().add_attribute_user(PROPERTY_QNAME[2]);
            }
            target.set(toLocation);
        }
    }

    /**
     * Gets the "count" attribute
     */
    @Override
    public java.math.BigInteger getCount() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[3]);
            return (target == null) ? null : target.getBigIntegerValue();
        }
    }

    /**
     * Gets (as xml) the "count" attribute
     */
    @Override
    public org.apache.xmlbeans.XmlUnsignedLong xgetCount() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlUnsignedLong target = null;
            target = (org.apache.xmlbeans.XmlUnsignedLong)get_store().find_attribute_user(PROPERTY_QNAME[3]);
            return target;
        }
    }

    /**
     * Sets the "count" attribute
     */
    @Override
    public void setCount(java.math.BigInteger count) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[3]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[3]);
            }
            target.setBigIntegerValue(count);
        }
    }

    /**
     * Sets (as xml) the "count" attribute
     */
    @Override
    public void xsetCount(org.apache.xmlbeans.XmlUnsignedLong count) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlUnsignedLong target = null;
            target = (org.apache.xmlbeans.XmlUnsignedLong)get_store().find_attribute_user(PROPERTY_QNAME[3]);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlUnsignedLong)get_store().add_attribute_user(PROPERTY_QNAME[3]);
            }
            target.set(count);
        }
    }
}
