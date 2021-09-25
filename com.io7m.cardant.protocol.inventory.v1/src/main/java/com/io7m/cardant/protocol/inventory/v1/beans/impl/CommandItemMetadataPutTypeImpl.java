/*
 * XML Type:  CommandItemMetadataPutType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML CommandItemMetadataPutType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class CommandItemMetadataPutTypeImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandTypeImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutType {
    private static final long serialVersionUID = 1L;

    public CommandItemMetadataPutTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ItemMetadatas"),
        new QName("", "item"),
    };


    /**
     * Gets the "ItemMetadatas" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType getItemMetadatas() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ItemMetadatas" element
     */
    @Override
    public void setItemMetadatas(com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType itemMetadatas) {
        generatedSetterHelperImpl(itemMetadatas, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ItemMetadatas" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType addNewItemMetadatas() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Gets the "item" attribute
     */
    @Override
    public java.lang.String getItem() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[1]);
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
            target = (com.io7m.cardant.protocol.inventory.v1.beans.UUIDType)get_store().find_attribute_user(PROPERTY_QNAME[1]);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[1]);
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
            target = (com.io7m.cardant.protocol.inventory.v1.beans.UUIDType)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (com.io7m.cardant.protocol.inventory.v1.beans.UUIDType)get_store().add_attribute_user(PROPERTY_QNAME[1]);
            }
            target.set(item);
        }
    }
}
