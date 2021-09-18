/*
 * An XML document type.
 * Localname: ResponseTagList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ResponseTagList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseTagListDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagListDocument {
    private static final long serialVersionUID = 1L;

    public ResponseTagListDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseTagList"),
    };


    /**
     * Gets the "ResponseTagList" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagListType getResponseTagList() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagListType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagListType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseTagList" element
     */
    @Override
    public void setResponseTagList(com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagListType responseTagList) {
        generatedSetterHelperImpl(responseTagList, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseTagList" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagListType addNewResponseTagList() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagListType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagListType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
