/*
 * An XML document type.
 * Localname: ResponseFileRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseFileRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ResponseFileRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseFileRemoveDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseFileRemoveDocument {
    private static final long serialVersionUID = 1L;

    public ResponseFileRemoveDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseFileRemove"),
    };


    /**
     * Gets the "ResponseFileRemove" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseFileRemoveType getResponseFileRemove() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseFileRemoveType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseFileRemoveType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseFileRemove" element
     */
    @Override
    public void setResponseFileRemove(com.io7m.cardant.protocol.inventory.v1.beans.ResponseFileRemoveType responseFileRemove) {
        generatedSetterHelperImpl(responseFileRemove, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseFileRemove" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseFileRemoveType addNewResponseFileRemove() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseFileRemoveType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseFileRemoveType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
