/*
 * XML Type:  ResponseItemListType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemListType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML ResponseItemListType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ResponseItemListTypeImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseTypeImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemListType {
    private static final long serialVersionUID = 1L;

    public ResponseItemListTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "Item"),
    };


    /**
     * Gets a List of "Item" elements
     */
    @Override
    public java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.ItemType> getItemList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getItemArray,
                this::setItemArray,
                this::insertNewItem,
                this::removeItem,
                this::sizeOfItemArray
            );
        }
    }

    /**
     * Gets array of all "Item" elements
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemType[] getItemArray() {
        return getXmlObjectArray(PROPERTY_QNAME[0], new com.io7m.cardant.protocol.inventory.v1.beans.ItemType[0]);
    }

    /**
     * Gets ith "Item" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemType getItemArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemType)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "Item" element
     */
    @Override
    public int sizeOfItemArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets array of all "Item" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setItemArray(com.io7m.cardant.protocol.inventory.v1.beans.ItemType[] itemArray) {
        check_orphaned();
        arraySetterHelper(itemArray, PROPERTY_QNAME[0]);
    }

    /**
     * Sets ith "Item" element
     */
    @Override
    public void setItemArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.ItemType item) {
        generatedSetterHelperImpl(item, PROPERTY_QNAME[0], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "Item" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemType insertNewItem(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemType)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "Item" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemType addNewItem() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Removes the ith "Item" element
     */
    @Override
    public void removeItem(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}
