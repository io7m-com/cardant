/*
 * An XML document type.
 * Localname: ResponseErrorAttributes
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ResponseErrorAttributes(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseErrorAttributesDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesDocument {
    private static final long serialVersionUID = 1L;

    public ResponseErrorAttributesDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseErrorAttributes"),
    };


    /**
     * Gets the "ResponseErrorAttributes" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesType getResponseErrorAttributes() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseErrorAttributes" element
     */
    @Override
    public void setResponseErrorAttributes(com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesType responseErrorAttributes) {
        generatedSetterHelperImpl(responseErrorAttributes, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseErrorAttributes" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesType addNewResponseErrorAttributes() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributesType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
