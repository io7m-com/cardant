/*
 * An XML document type.
 * Localname: ResponseError
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ResponseError(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseErrorDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDocument {
    private static final long serialVersionUID = 1L;

    public ResponseErrorDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseError"),
    };


    /**
     * Gets the "ResponseError" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorType getResponseError() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseError" element
     */
    @Override
    public void setResponseError(com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorType responseError) {
        generatedSetterHelperImpl(responseError, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseError" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorType addNewResponseError() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
