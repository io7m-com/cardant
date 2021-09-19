/*
 * An XML document type.
 * Localname: ResponseItemGet
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemGetDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ResponseItemGet(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseItemGetDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemGetDocument {
    private static final long serialVersionUID = 1L;

    public ResponseItemGetDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemGet"),
    };


    /**
     * Gets the "ResponseItemGet" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemGetType getResponseItemGet() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemGetType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemGetType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseItemGet" element
     */
    @Override
    public void setResponseItemGet(com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemGetType responseItemGet) {
        generatedSetterHelperImpl(responseItemGet, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseItemGet" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemGetType addNewResponseItemGet() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemGetType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemGetType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
