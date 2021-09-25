/*
 * XML Type:  ItemMetadataType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML ItemMetadataType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ItemMetadataTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType {
    private static final long serialVersionUID = 1L;

    public ItemMetadataTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("", "name"),
        new QName("", "value"),
    };


    /**
     * Gets the "name" attribute
     */
    @Override
    public java.lang.String getName() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "name" attribute
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType xgetName() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Sets the "name" attribute
     */
    @Override
    public void setName(java.lang.String name) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[0]);
            }
            target.setStringValue(name);
        }
    }

    /**
     * Sets (as xml) the "name" attribute
     */
    @Override
    public void xsetName(com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType name) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType)get_store().add_attribute_user(PROPERTY_QNAME[0]);
            }
            target.set(name);
        }
    }

    /**
     * Gets the "value" attribute
     */
    @Override
    public java.lang.String getValue() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "value" attribute
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataValueType xgetValue() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataValueType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataValueType)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /**
     * Sets the "value" attribute
     */
    @Override
    public void setValue(java.lang.String value) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[1]);
            }
            target.setStringValue(value);
        }
    }

    /**
     * Sets (as xml) the "value" attribute
     */
    @Override
    public void xsetValue(com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataValueType value) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataValueType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataValueType)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataValueType)get_store().add_attribute_user(PROPERTY_QNAME[1]);
            }
            target.set(value);
        }
    }
}
