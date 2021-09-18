/*
 * XML Type:  CommandItemMetadataRemoveType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML CommandItemMetadataRemoveType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class CommandItemMetadataRemoveTypeImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandTypeImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveType {
    private static final long serialVersionUID = 1L;

    public CommandItemMetadataRemoveTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ItemMetadataName"),
        new QName("", "item"),
    };


    /**
     * Gets a List of "ItemMetadataName" elements
     */
    @Override
    public java.util.List<java.lang.String> getItemMetadataNameList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListObject<>(
                this::getItemMetadataNameArray,
                this::setItemMetadataNameArray,
                this::insertItemMetadataName,
                this::removeItemMetadataName,
                this::sizeOfItemMetadataNameArray
            );
        }
    }

    /**
     * Gets array of all "ItemMetadataName" elements
     */
    @Override
    public java.lang.String[] getItemMetadataNameArray() {
        return getObjectArray(PROPERTY_QNAME[0], org.apache.xmlbeans.SimpleValue::getStringValue, String[]::new);
    }

    /**
     * Gets ith "ItemMetadataName" element
     */
    @Override
    public java.lang.String getItemMetadataNameArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }

    /**
     * Gets (as xml) a List of "ItemMetadataName" elements
     */
    @Override
    public java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType> xgetItemMetadataNameList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::xgetItemMetadataNameArray,
                this::xsetItemMetadataNameArray,
                this::insertNewItemMetadataName,
                this::removeItemMetadataName,
                this::sizeOfItemMetadataNameArray
            );
        }
    }

    /**
     * Gets (as xml) array of all "ItemMetadataName" elements
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType[] xgetItemMetadataNameArray() {
        return xgetArray(PROPERTY_QNAME[0], com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType[]::new);
    }

    /**
     * Gets (as xml) ith "ItemMetadataName" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType xgetItemMetadataNameArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "ItemMetadataName" element
     */
    @Override
    public int sizeOfItemMetadataNameArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets array of all "ItemMetadataName" element
     */
    @Override
    public void setItemMetadataNameArray(java.lang.String[] itemMetadataNameArray) {
        synchronized (monitor()) {
            check_orphaned();
            arraySetterHelper(itemMetadataNameArray, PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets ith "ItemMetadataName" element
     */
    @Override
    public void setItemMetadataNameArray(int i, java.lang.String itemMetadataName) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(itemMetadataName);
        }
    }

    /**
     * Sets (as xml) array of all "ItemMetadataName" element
     */
    @Override
    public void xsetItemMetadataNameArray(com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType[]itemMetadataNameArray) {
        synchronized (monitor()) {
            check_orphaned();
            arraySetterHelper(itemMetadataNameArray, PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets (as xml) ith "ItemMetadataName" element
     */
    @Override
    public void xsetItemMetadataNameArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType itemMetadataName) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(itemMetadataName);
        }
    }

    /**
     * Inserts the value as the ith "ItemMetadataName" element
     */
    @Override
    public void insertItemMetadataName(int i, java.lang.String itemMetadataName) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target =
                (org.apache.xmlbeans.SimpleValue)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            target.setStringValue(itemMetadataName);
        }
    }

    /**
     * Appends the value as the last "ItemMetadataName" element
     */
    @Override
    public void addItemMetadataName(java.lang.String itemMetadataName) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[0]);
            target.setStringValue(itemMetadataName);
        }
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "ItemMetadataName" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType insertNewItemMetadataName(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "ItemMetadataName" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType addNewItemMetadataName() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Removes the ith "ItemMetadataName" element
     */
    @Override
    public void removeItemMetadataName(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], i);
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
