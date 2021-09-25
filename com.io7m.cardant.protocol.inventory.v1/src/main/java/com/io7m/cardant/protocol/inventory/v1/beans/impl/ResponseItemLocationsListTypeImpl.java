/*
 * XML Type:  ResponseItemLocationsListType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemLocationsListType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML ResponseItemLocationsListType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ResponseItemLocationsListTypeImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseTypeImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemLocationsListType {
    private static final long serialVersionUID = 1L;

    public ResponseItemLocationsListTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ItemLocation"),
    };


    /**
     * Gets a List of "ItemLocation" elements
     */
    @Override
    public java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType> getItemLocationList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getItemLocationArray,
                this::setItemLocationArray,
                this::insertNewItemLocation,
                this::removeItemLocation,
                this::sizeOfItemLocationArray
            );
        }
    }

    /**
     * Gets array of all "ItemLocation" elements
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType[] getItemLocationArray() {
        return getXmlObjectArray(PROPERTY_QNAME[0], new com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType[0]);
    }

    /**
     * Gets ith "ItemLocation" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType getItemLocationArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "ItemLocation" element
     */
    @Override
    public int sizeOfItemLocationArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets array of all "ItemLocation" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setItemLocationArray(com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType[] itemLocationArray) {
        check_orphaned();
        arraySetterHelper(itemLocationArray, PROPERTY_QNAME[0]);
    }

    /**
     * Sets ith "ItemLocation" element
     */
    @Override
    public void setItemLocationArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType itemLocation) {
        generatedSetterHelperImpl(itemLocation, PROPERTY_QNAME[0], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "ItemLocation" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType insertNewItemLocation(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "ItemLocation" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType addNewItemLocation() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Removes the ith "ItemLocation" element
     */
    @Override
    public void removeItemLocation(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}
