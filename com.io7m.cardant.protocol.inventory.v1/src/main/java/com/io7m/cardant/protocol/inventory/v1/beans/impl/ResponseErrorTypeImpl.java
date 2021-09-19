/*
 * XML Type:  ResponseErrorType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML ResponseErrorType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ResponseErrorTypeImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseTypeImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorType {
    private static final long serialVersionUID = 1L;

    public ResponseErrorTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseErrorDetail"),
        new QName("", "status"),
        new QName("", "message"),
    };


    /**
     * Gets a List of "ResponseErrorDetail" elements
     */
    @Override
    public java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType> getResponseErrorDetailList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getResponseErrorDetailArray,
                this::setResponseErrorDetailArray,
                this::insertNewResponseErrorDetail,
                this::removeResponseErrorDetail,
                this::sizeOfResponseErrorDetailArray
            );
        }
    }

    /**
     * Gets array of all "ResponseErrorDetail" elements
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType[] getResponseErrorDetailArray() {
        return getXmlObjectArray(PROPERTY_QNAME[0], new com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType[0]);
    }

    /**
     * Gets ith "ResponseErrorDetail" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType getResponseErrorDetailArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "ResponseErrorDetail" element
     */
    @Override
    public int sizeOfResponseErrorDetailArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets array of all "ResponseErrorDetail" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setResponseErrorDetailArray(com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType[] responseErrorDetailArray) {
        check_orphaned();
        arraySetterHelper(responseErrorDetailArray, PROPERTY_QNAME[0]);
    }

    /**
     * Sets ith "ResponseErrorDetail" element
     */
    @Override
    public void setResponseErrorDetailArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType responseErrorDetail) {
        generatedSetterHelperImpl(responseErrorDetail, PROPERTY_QNAME[0], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "ResponseErrorDetail" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType insertNewResponseErrorDetail(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "ResponseErrorDetail" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType addNewResponseErrorDetail() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Removes the ith "ResponseErrorDetail" element
     */
    @Override
    public void removeResponseErrorDetail(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }

    /**
     * Gets the "status" attribute
     */
    @Override
    public java.math.BigInteger getStatus() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            return (target == null) ? null : target.getBigIntegerValue();
        }
    }

    /**
     * Gets (as xml) the "status" attribute
     */
    @Override
    public org.apache.xmlbeans.XmlInteger xgetStatus() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlInteger target = null;
            target = (org.apache.xmlbeans.XmlInteger)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /**
     * Sets the "status" attribute
     */
    @Override
    public void setStatus(java.math.BigInteger status) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[1]);
            }
            target.setBigIntegerValue(status);
        }
    }

    /**
     * Sets (as xml) the "status" attribute
     */
    @Override
    public void xsetStatus(org.apache.xmlbeans.XmlInteger status) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlInteger target = null;
            target = (org.apache.xmlbeans.XmlInteger)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlInteger)get_store().add_attribute_user(PROPERTY_QNAME[1]);
            }
            target.set(status);
        }
    }

    /**
     * Gets the "message" attribute
     */
    @Override
    public java.lang.String getMessage() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[2]);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "message" attribute
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetMessage() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(PROPERTY_QNAME[2]);
            return target;
        }
    }

    /**
     * Sets the "message" attribute
     */
    @Override
    public void setMessage(java.lang.String message) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[2]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[2]);
            }
            target.setStringValue(message);
        }
    }

    /**
     * Sets (as xml) the "message" attribute
     */
    @Override
    public void xsetMessage(org.apache.xmlbeans.XmlString message) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(PROPERTY_QNAME[2]);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(PROPERTY_QNAME[2]);
            }
            target.set(message);
        }
    }
}
