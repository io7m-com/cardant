/*
 * An XML document type.
 * Localname: ResponseLocationPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ResponseLocationPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseLocationPutDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationPutDocument {
    private static final long serialVersionUID = 1L;

    public ResponseLocationPutDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseLocationPut"),
    };


    /**
     * Gets the "ResponseLocationPut" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationPutType getResponseLocationPut() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationPutType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationPutType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseLocationPut" element
     */
    @Override
    public void setResponseLocationPut(com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationPutType responseLocationPut) {
        generatedSetterHelperImpl(responseLocationPut, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseLocationPut" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationPutType addNewResponseLocationPut() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationPutType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationPutType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
