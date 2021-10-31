/*
 * XML Type:  IDsType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.IDsType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML IDsType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class IDsTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.IDsType {
    private static final long serialVersionUID = 1L;

    public IDsTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ID"),
    };

    private static final QNameSet[] PROPERTY_QSET = {
    QNameSet.forArray( new QName[] { 
        new QName("urn:com.io7m.cardant.inventory:1", "ItemID"),
        new QName("urn:com.io7m.cardant.inventory:1", "TagID"),
        new QName("urn:com.io7m.cardant.inventory:1", "LocationID"),
        new QName("urn:com.io7m.cardant.inventory:1", "ID"),
        new QName("urn:com.io7m.cardant.inventory:1", "FileID"),
        new QName("urn:com.io7m.cardant.inventory:1", "UserID"),
    }),
    };

    /**
     * Gets a List of "ID" elements
     */
    @Override
    public java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.IDType> getIDList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getIDArray,
                this::setIDArray,
                this::insertNewID,
                this::removeID,
                this::sizeOfIDArray
            );
        }
    }

    /**
     * Gets array of all "ID" elements
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.IDType[] getIDArray() {
        return getXmlObjectArray(PROPERTY_QSET[0], new com.io7m.cardant.protocol.inventory.v1.beans.IDType[0]);
    }

    /**
     * Gets ith "ID" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.IDType getIDArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.IDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.IDType)get_store().find_element_user(PROPERTY_QSET[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "ID" element
     */
    @Override
    public int sizeOfIDArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QSET[0]);
        }
    }

    /**
     * Sets array of all "ID" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setIDArray(com.io7m.cardant.protocol.inventory.v1.beans.IDType[] idArray) {
        check_orphaned();
        arraySetterHelper(idArray, PROPERTY_QNAME[0], PROPERTY_QSET[0]);
    }

    /**
     * Sets ith "ID" element
     */
    @Override
    public void setIDArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.IDType id) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.IDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.IDType)get_store().find_element_user(PROPERTY_QSET[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(id);
        }
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "ID" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.IDType insertNewID(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.IDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.IDType)get_store().insert_element_user(PROPERTY_QSET[0], PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "ID" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.IDType addNewID() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.IDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.IDType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Removes the ith "ID" element
     */
    @Override
    public void removeID(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QSET[0], i);
        }
    }
}
