/*
 * An XML document type.
 * Localname: ResponseTagsPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ResponseTagsPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseTagsPutDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsPutDocument {
    private static final long serialVersionUID = 1L;

    public ResponseTagsPutDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseTagsPut"),
    };


    /**
     * Gets the "ResponseTagsPut" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsPutType getResponseTagsPut() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsPutType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsPutType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseTagsPut" element
     */
    @Override
    public void setResponseTagsPut(com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsPutType responseTagsPut) {
        generatedSetterHelperImpl(responseTagsPut, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseTagsPut" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsPutType addNewResponseTagsPut() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsPutType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsPutType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
