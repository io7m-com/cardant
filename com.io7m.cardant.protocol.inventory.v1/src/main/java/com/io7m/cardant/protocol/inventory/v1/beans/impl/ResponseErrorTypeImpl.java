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
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseErrorAttributes"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseErrorDetails"),
        new QName("", "status"),
        new QName("", "summary"),
    };


    /**
     * Gets the "ResponseErrorAttributes" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesType getResponseErrorAttributes() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseErrorAttributes" element
     */
    @Override
    public void setResponseErrorAttributes(com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesType responseErrorAttributes) {
        generatedSetterHelperImpl(responseErrorAttributes, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseErrorAttributes" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesType addNewResponseErrorAttributes() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Gets the "ResponseErrorDetails" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsType getResponseErrorDetails() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsType)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseErrorDetails" element
     */
    @Override
    public void setResponseErrorDetails(com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsType responseErrorDetails) {
        generatedSetterHelperImpl(responseErrorDetails, PROPERTY_QNAME[1], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseErrorDetails" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsType addNewResponseErrorDetails() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsType)get_store().add_element_user(PROPERTY_QNAME[1]);
            return target;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[2]);
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
            target = (org.apache.xmlbeans.XmlInteger)get_store().find_attribute_user(PROPERTY_QNAME[2]);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[2]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[2]);
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
            target = (org.apache.xmlbeans.XmlInteger)get_store().find_attribute_user(PROPERTY_QNAME[2]);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlInteger)get_store().add_attribute_user(PROPERTY_QNAME[2]);
            }
            target.set(status);
        }
    }

    /**
     * Gets the "summary" attribute
     */
    @Override
    public java.lang.String getSummary() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[3]);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "summary" attribute
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetSummary() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(PROPERTY_QNAME[3]);
            return target;
        }
    }

    /**
     * Sets the "summary" attribute
     */
    @Override
    public void setSummary(java.lang.String summary) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[3]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[3]);
            }
            target.setStringValue(summary);
        }
    }

    /**
     * Sets (as xml) the "summary" attribute
     */
    @Override
    public void xsetSummary(org.apache.xmlbeans.XmlString summary) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(PROPERTY_QNAME[3]);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(PROPERTY_QNAME[3]);
            }
            target.set(summary);
        }
    }
}
