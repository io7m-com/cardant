/*
 * An XML document type.
 * Localname: ResponseErrorDetails
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ResponseErrorDetails(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseErrorDetailsDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsDocument {
    private static final long serialVersionUID = 1L;

    public ResponseErrorDetailsDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseErrorDetails"),
    };


    /**
     * Gets the "ResponseErrorDetails" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsType getResponseErrorDetails() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseErrorDetails" element
     */
    @Override
    public void setResponseErrorDetails(com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsType responseErrorDetails) {
        generatedSetterHelperImpl(responseErrorDetails, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseErrorDetails" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsType addNewResponseErrorDetails() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailsType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
