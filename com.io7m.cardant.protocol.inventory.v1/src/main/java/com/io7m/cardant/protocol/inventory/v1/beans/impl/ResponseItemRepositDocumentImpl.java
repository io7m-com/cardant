/*
 * An XML document type.
 * Localname: ResponseItemReposit
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRepositDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ResponseItemReposit(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseItemRepositDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRepositDocument {
    private static final long serialVersionUID = 1L;

    public ResponseItemRepositDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemReposit"),
    };


    /**
     * Gets the "ResponseItemReposit" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRepositType getResponseItemReposit() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRepositType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRepositType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseItemReposit" element
     */
    @Override
    public void setResponseItemReposit(com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRepositType responseItemReposit) {
        generatedSetterHelperImpl(responseItemReposit, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseItemReposit" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRepositType addNewResponseItemReposit() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRepositType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRepositType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
