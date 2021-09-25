/*
 * An XML document type.
 * Localname: ResponseErrorAttribute
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ResponseErrorAttribute(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseErrorAttributeDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeDocument {
    private static final long serialVersionUID = 1L;

    public ResponseErrorAttributeDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseErrorAttribute"),
    };


    /**
     * Gets the "ResponseErrorAttribute" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType getResponseErrorAttribute() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseErrorAttribute" element
     */
    @Override
    public void setResponseErrorAttribute(com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType responseErrorAttribute) {
        generatedSetterHelperImpl(responseErrorAttribute, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseErrorAttribute" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType addNewResponseErrorAttribute() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
