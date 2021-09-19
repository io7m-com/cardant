/*
 * XML Type:  CommandItemRepositType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRepositType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML CommandItemRepositType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class CommandItemRepositTypeImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandTypeImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRepositType {
    private static final long serialVersionUID = 1L;

    public CommandItemRepositTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ItemReposit"),
    };

    private static final QNameSet[] PROPERTY_QSET = {
    QNameSet.forArray( new QName[] { 
        new QName("urn:com.io7m.cardant.inventory:1", "ItemReposit"),
        new QName("urn:com.io7m.cardant.inventory:1", "ItemRepositMove"),
        new QName("urn:com.io7m.cardant.inventory:1", "ItemRepositRemove"),
        new QName("urn:com.io7m.cardant.inventory:1", "ItemRepositAdd"),
    }),
    };

    /**
     * Gets the "ItemReposit" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositType getItemReposit() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositType)get_store().find_element_user(PROPERTY_QSET[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ItemReposit" element
     */
    @Override
    public void setItemReposit(com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositType itemReposit) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositType)get_store().find_element_user(PROPERTY_QSET[0], 0);
            if (target == null) {
                target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositType)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.set(itemReposit);
        }
    }

    /**
     * Appends and returns a new empty "ItemReposit" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositType addNewItemReposit() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
