/*
 * XML Type:  CommandItemListType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemListType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML CommandItemListType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class CommandItemListTypeImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandTypeImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandItemListType {
    private static final long serialVersionUID = 1L;

    public CommandItemListTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ListLocationsAll"),
        new QName("urn:com.io7m.cardant.inventory:1", "ListLocationExact"),
        new QName("urn:com.io7m.cardant.inventory:1", "ListLocationWithDescendants"),
    };


    /**
     * Gets the "ListLocationsAll" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllType getListLocationsAll() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * True if has "ListLocationsAll" element
     */
    @Override
    public boolean isSetListLocationsAll() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    /**
     * Sets the "ListLocationsAll" element
     */
    @Override
    public void setListLocationsAll(com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllType listLocationsAll) {
        generatedSetterHelperImpl(listLocationsAll, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ListLocationsAll" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllType addNewListLocationsAll() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Unsets the "ListLocationsAll" element
     */
    @Override
    public void unsetListLocationsAll() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], 0);
        }
    }

    /**
     * Gets the "ListLocationExact" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType getListLocationExact() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * True if has "ListLocationExact" element
     */
    @Override
    public boolean isSetListLocationExact() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    /**
     * Sets the "ListLocationExact" element
     */
    @Override
    public void setListLocationExact(com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType listLocationExact) {
        generatedSetterHelperImpl(listLocationExact, PROPERTY_QNAME[1], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ListLocationExact" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType addNewListLocationExact() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType)get_store().add_element_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /**
     * Unsets the "ListLocationExact" element
     */
    @Override
    public void unsetListLocationExact() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[1], 0);
        }
    }

    /**
     * Gets the "ListLocationWithDescendants" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsType getListLocationWithDescendants() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsType)get_store().find_element_user(PROPERTY_QNAME[2], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * True if has "ListLocationWithDescendants" element
     */
    @Override
    public boolean isSetListLocationWithDescendants() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    /**
     * Sets the "ListLocationWithDescendants" element
     */
    @Override
    public void setListLocationWithDescendants(com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsType listLocationWithDescendants) {
        generatedSetterHelperImpl(listLocationWithDescendants, PROPERTY_QNAME[2], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ListLocationWithDescendants" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsType addNewListLocationWithDescendants() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsType)get_store().add_element_user(PROPERTY_QNAME[2]);
            return target;
        }
    }

    /**
     * Unsets the "ListLocationWithDescendants" element
     */
    @Override
    public void unsetListLocationWithDescendants() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[2], 0);
        }
    }
}
