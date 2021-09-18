/*
 * XML Type:  ResponseItemRemoveType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRemoveType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML ResponseItemRemoveType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ResponseItemRemoveTypeImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseTypeImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRemoveType {
    private static final long serialVersionUID = 1L;

    public ResponseItemRemoveTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("", "id"),
    };


    /**
     * Gets the "id" attribute
     */
    @Override
    public java.lang.String getId() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "id" attribute
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.UUIDType xgetId() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.UUIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.UUIDType)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Sets the "id" attribute
     */
    @Override
    public void setId(java.lang.String id) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[0]);
            }
            target.setStringValue(id);
        }
    }

    /**
     * Sets (as xml) the "id" attribute
     */
    @Override
    public void xsetId(com.io7m.cardant.protocol.inventory.v1.beans.UUIDType id) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.UUIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.UUIDType)get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (com.io7m.cardant.protocol.inventory.v1.beans.UUIDType)get_store().add_attribute_user(PROPERTY_QNAME[0]);
            }
            target.set(id);
        }
    }
}
