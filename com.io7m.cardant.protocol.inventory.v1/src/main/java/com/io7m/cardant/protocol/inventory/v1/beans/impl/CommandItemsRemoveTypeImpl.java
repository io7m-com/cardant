/*
 * XML Type:  CommandItemsRemoveType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemsRemoveType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML CommandItemsRemoveType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class CommandItemsRemoveTypeImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandTypeImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandItemsRemoveType {
    private static final long serialVersionUID = 1L;

    public CommandItemsRemoveTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ItemID"),
    };


    /**
     * Gets a List of "ItemID" elements
     */
    @Override
    public java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType> getItemIDList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getItemIDArray,
                this::setItemIDArray,
                this::insertNewItemID,
                this::removeItemID,
                this::sizeOfItemIDArray
            );
        }
    }

    /**
     * Gets array of all "ItemID" elements
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType[] getItemIDArray() {
        return getXmlObjectArray(PROPERTY_QNAME[0], new com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType[0]);
    }

    /**
     * Gets ith "ItemID" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType getItemIDArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "ItemID" element
     */
    @Override
    public int sizeOfItemIDArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets array of all "ItemID" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setItemIDArray(com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType[] itemIDArray) {
        check_orphaned();
        arraySetterHelper(itemIDArray, PROPERTY_QNAME[0]);
    }

    /**
     * Sets ith "ItemID" element
     */
    @Override
    public void setItemIDArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType itemID) {
        generatedSetterHelperImpl(itemID, PROPERTY_QNAME[0], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "ItemID" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType insertNewItemID(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "ItemID" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType addNewItemID() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Removes the ith "ItemID" element
     */
    @Override
    public void removeItemID(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}
