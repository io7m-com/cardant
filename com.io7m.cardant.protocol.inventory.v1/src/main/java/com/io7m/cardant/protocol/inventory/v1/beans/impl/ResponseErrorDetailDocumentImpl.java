/*
 * An XML document type.
 * Localname: ResponseErrorDetail
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ResponseErrorDetail(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseErrorDetailDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailDocument {
    private static final long serialVersionUID = 1L;

    public ResponseErrorDetailDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseErrorDetail"),
    };


    /**
     * Gets the "ResponseErrorDetail" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType getResponseErrorDetail() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseErrorDetail" element
     */
    @Override
    public void setResponseErrorDetail(com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType responseErrorDetail) {
        generatedSetterHelperImpl(responseErrorDetail, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseErrorDetail" element
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
}
