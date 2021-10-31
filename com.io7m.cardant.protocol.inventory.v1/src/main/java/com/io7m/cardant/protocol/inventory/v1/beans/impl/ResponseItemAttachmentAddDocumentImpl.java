/*
 * An XML document type.
 * Localname: ResponseItemAttachmentAdd
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentAddDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ResponseItemAttachmentAdd(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ResponseItemAttachmentAddDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.ResponseDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentAddDocument {
    private static final long serialVersionUID = 1L;

    public ResponseItemAttachmentAddDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemAttachmentAdd"),
    };


    /**
     * Gets the "ResponseItemAttachmentAdd" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentAddType getResponseItemAttachmentAdd() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentAddType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentAddType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ResponseItemAttachmentAdd" element
     */
    @Override
    public void setResponseItemAttachmentAdd(com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentAddType responseItemAttachmentAdd) {
        generatedSetterHelperImpl(responseItemAttachmentAdd, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ResponseItemAttachmentAdd" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentAddType addNewResponseItemAttachmentAdd() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentAddType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentAddType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
