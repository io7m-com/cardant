/*
 * An XML document type.
 * Localname: ResponseItemAttachmentPut
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentPutDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ResponseItemAttachmentPut(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseItemAttachmentPutDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentPutDocument {
    private static final long serialVersionUID = 1L;

    public ResponseItemAttachmentPutDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemAttachmentPut"),
    };


    /**
     * Gets the "ResponseItemAttachmentPut" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentPutType getResponseItemAttachmentPut() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentPutType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentPutType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseItemAttachmentPut" element
     */
    @Override
    public void setResponseItemAttachmentPut(com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentPutType responseItemAttachmentPut) {
        generatedSetterHelperImpl(responseItemAttachmentPut, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseItemAttachmentPut" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentPutType addNewResponseItemAttachmentPut() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentPutType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentPutType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
