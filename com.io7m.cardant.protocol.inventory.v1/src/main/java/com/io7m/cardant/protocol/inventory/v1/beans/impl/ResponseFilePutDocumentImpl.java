/*
 * An XML document type.
 * Localname: ResponseFilePut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseFilePutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ResponseFilePut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseFilePutDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseFilePutDocument {
    private static final long serialVersionUID = 1L;

    public ResponseFilePutDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseFilePut"),
    };


    /**
     * Gets the "ResponseFilePut" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseFilePutType getResponseFilePut() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseFilePutType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseFilePutType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseFilePut" element
     */
    @Override
    public void setResponseFilePut(com.io7m.cardant.protocol.inventory.v1.beans.ResponseFilePutType responseFilePut) {
        generatedSetterHelperImpl(responseFilePut, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseFilePut" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseFilePutType addNewResponseFilePut() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseFilePutType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseFilePutType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
