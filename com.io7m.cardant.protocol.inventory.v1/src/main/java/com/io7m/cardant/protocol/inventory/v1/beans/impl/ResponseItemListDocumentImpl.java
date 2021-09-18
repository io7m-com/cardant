/*
 * An XML document type.
 * Localname: ResponseItemList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ResponseItemList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseItemListDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemListDocument {
    private static final long serialVersionUID = 1L;

    public ResponseItemListDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemList"),
    };


    /**
     * Gets the "ResponseItemList" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemListType getResponseItemList() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemListType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemListType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseItemList" element
     */
    @Override
    public void setResponseItemList(com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemListType responseItemList) {
        generatedSetterHelperImpl(responseItemList, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseItemList" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemListType addNewResponseItemList() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemListType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemListType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
