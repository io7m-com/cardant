/*
 * An XML document type.
 * Localname: ResponseTagsDelete
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsDeleteDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ResponseTagsDelete(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseTagsDeleteDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsDeleteDocument {
    private static final long serialVersionUID = 1L;

    public ResponseTagsDeleteDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseTagsDelete"),
    };


    /**
     * Gets the "ResponseTagsDelete" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsDeleteType getResponseTagsDelete() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsDeleteType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsDeleteType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseTagsDelete" element
     */
    @Override
    public void setResponseTagsDelete(com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsDeleteType responseTagsDelete) {
        generatedSetterHelperImpl(responseTagsDelete, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseTagsDelete" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsDeleteType addNewResponseTagsDelete() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsDeleteType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsDeleteType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
