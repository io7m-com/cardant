/*
 * An XML document type.
 * Localname: ResponseItemRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ResponseItemRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseItemRemoveDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRemoveDocument {
    private static final long serialVersionUID = 1L;

    public ResponseItemRemoveDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemRemove"),
    };


    /**
     * Gets the "ResponseItemRemove" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRemoveType getResponseItemRemove() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRemoveType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRemoveType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseItemRemove" element
     */
    @Override
    public void setResponseItemRemove(com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRemoveType responseItemRemove) {
        generatedSetterHelperImpl(responseItemRemove, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseItemRemove" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRemoveType addNewResponseItemRemove() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRemoveType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRemoveType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
