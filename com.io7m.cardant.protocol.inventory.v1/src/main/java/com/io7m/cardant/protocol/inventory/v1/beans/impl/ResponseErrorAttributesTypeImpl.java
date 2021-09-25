/*
 * XML Type:  ResponseErrorAttributesType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML ResponseErrorAttributesType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ResponseErrorAttributesTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesType {
    private static final long serialVersionUID = 1L;

    public ResponseErrorAttributesTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseErrorAttribute"),
    };


    /**
     * Gets a List of "ResponseErrorAttribute" elements
     */
    @Override
    public java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType> getResponseErrorAttributeList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getResponseErrorAttributeArray,
                this::setResponseErrorAttributeArray,
                this::insertNewResponseErrorAttribute,
                this::removeResponseErrorAttribute,
                this::sizeOfResponseErrorAttributeArray
            );
        }
    }

    /**
     * Gets array of all "ResponseErrorAttribute" elements
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType[] getResponseErrorAttributeArray() {
        return getXmlObjectArray(PROPERTY_QNAME[0], new com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType[0]);
    }

    /**
     * Gets ith "ResponseErrorAttribute" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType getResponseErrorAttributeArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "ResponseErrorAttribute" element
     */
    @Override
    public int sizeOfResponseErrorAttributeArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets array of all "ResponseErrorAttribute" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setResponseErrorAttributeArray(com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType[] responseErrorAttributeArray) {
        check_orphaned();
        arraySetterHelper(responseErrorAttributeArray, PROPERTY_QNAME[0]);
    }

    /**
     * Sets ith "ResponseErrorAttribute" element
     */
    @Override
    public void setResponseErrorAttributeArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType responseErrorAttribute) {
        generatedSetterHelperImpl(responseErrorAttribute, PROPERTY_QNAME[0], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "ResponseErrorAttribute" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType insertNewResponseErrorAttribute(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "ResponseErrorAttribute" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType addNewResponseErrorAttribute() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Removes the ith "ResponseErrorAttribute" element
     */
    @Override
    public void removeResponseErrorAttribute(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}
