/*
 * XML Type:  ResponseLocationListType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationListType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML ResponseLocationListType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ResponseLocationListTypeImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseTypeImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationListType {
    private static final long serialVersionUID = 1L;

    public ResponseLocationListTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "Location"),
    };


    /**
     * Gets a List of "Location" elements
     */
    @Override
    public java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.LocationType> getLocationList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getLocationArray,
                this::setLocationArray,
                this::insertNewLocation,
                this::removeLocation,
                this::sizeOfLocationArray
            );
        }
    }

    /**
     * Gets array of all "Location" elements
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.LocationType[] getLocationArray() {
        return getXmlObjectArray(PROPERTY_QNAME[0], new com.io7m.cardant.protocol.inventory.v1.beans.LocationType[0]);
    }

    /**
     * Gets ith "Location" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.LocationType getLocationArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.LocationType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.LocationType)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "Location" element
     */
    @Override
    public int sizeOfLocationArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets array of all "Location" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setLocationArray(com.io7m.cardant.protocol.inventory.v1.beans.LocationType[] locationArray) {
        check_orphaned();
        arraySetterHelper(locationArray, PROPERTY_QNAME[0]);
    }

    /**
     * Sets ith "Location" element
     */
    @Override
    public void setLocationArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.LocationType location) {
        generatedSetterHelperImpl(location, PROPERTY_QNAME[0], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "Location" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.LocationType insertNewLocation(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.LocationType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.LocationType)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "Location" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.LocationType addNewLocation() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.LocationType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.LocationType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Removes the ith "Location" element
     */
    @Override
    public void removeLocation(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}
