/*
 * An XML document type.
 * Localname: ResponseLocationList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ResponseLocationList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseLocationListDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationListDocument {
    private static final long serialVersionUID = 1L;

    public ResponseLocationListDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseLocationList"),
    };


    /**
     * Gets the "ResponseLocationList" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationListType getResponseLocationList() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationListType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationListType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseLocationList" element
     */
    @Override
    public void setResponseLocationList(com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationListType responseLocationList) {
        generatedSetterHelperImpl(responseLocationList, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseLocationList" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationListType addNewResponseLocationList() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationListType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationListType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
