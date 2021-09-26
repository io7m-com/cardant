/*
 * An XML document type.
 * Localname: ResponseItemsRemove
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemsRemoveDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ResponseItemsRemove(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseItemsRemoveDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemsRemoveDocument {
    private static final long serialVersionUID = 1L;

    public ResponseItemsRemoveDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemsRemove"),
    };


    /**
     * Gets the "ResponseItemsRemove" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemsRemoveType getResponseItemsRemove() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemsRemoveType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemsRemoveType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseItemsRemove" element
     */
    @Override
    public void setResponseItemsRemove(com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemsRemoveType responseItemsRemove) {
        generatedSetterHelperImpl(responseItemsRemove, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseItemsRemove" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemsRemoveType addNewResponseItemsRemove() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemsRemoveType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemsRemoveType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
