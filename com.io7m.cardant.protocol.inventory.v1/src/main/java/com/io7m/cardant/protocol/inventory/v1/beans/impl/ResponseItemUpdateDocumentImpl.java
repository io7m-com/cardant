/*
 * An XML document type.
 * Localname: ResponseItemUpdate
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemUpdateDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ResponseItemUpdate(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseItemUpdateDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemUpdateDocument {
    private static final long serialVersionUID = 1L;

    public ResponseItemUpdateDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemUpdate"),
    };


    /**
     * Gets the "ResponseItemUpdate" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemUpdateType getResponseItemUpdate() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemUpdateType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemUpdateType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseItemUpdate" element
     */
    @Override
    public void setResponseItemUpdate(com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemUpdateType responseItemUpdate) {
        generatedSetterHelperImpl(responseItemUpdate, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseItemUpdate" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemUpdateType addNewResponseItemUpdate() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemUpdateType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemUpdateType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
