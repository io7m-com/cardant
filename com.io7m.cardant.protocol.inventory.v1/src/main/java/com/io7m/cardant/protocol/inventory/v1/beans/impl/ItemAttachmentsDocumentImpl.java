/*
 * An XML document type.
 * Localname: ItemAttachments
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ItemAttachments(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ItemAttachmentsDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsDocument {
    private static final long serialVersionUID = 1L;

    public ItemAttachmentsDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ItemAttachments"),
    };


    /**
     * Gets the "ItemAttachments" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType getItemAttachments() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ItemAttachments" element
     */
    @Override
    public void setItemAttachments(com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType itemAttachments) {
        generatedSetterHelperImpl(itemAttachments, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ItemAttachments" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType addNewItemAttachments() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
