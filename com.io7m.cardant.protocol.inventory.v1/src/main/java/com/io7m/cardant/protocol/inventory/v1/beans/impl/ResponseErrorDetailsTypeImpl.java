/*
 * XML Type:  ResponseErrorDetailsType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML ResponseErrorDetailsType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ResponseErrorDetailsTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsType {
    private static final long serialVersionUID = 1L;

    public ResponseErrorDetailsTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseErrorDetail"),
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
}
