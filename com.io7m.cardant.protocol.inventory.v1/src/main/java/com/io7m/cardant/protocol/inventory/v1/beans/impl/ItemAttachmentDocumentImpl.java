/*
 * An XML document type.
 * Localname: ItemAttachment
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ItemAttachment(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ItemAttachmentDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentDocument {
    private static final long serialVersionUID = 1L;

    public ItemAttachmentDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ItemAttachment"),
    };


    /**
     * Gets the "ItemAttachment" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType getItemAttachment() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ItemAttachment" element
     */
    @Override
    public void setItemAttachment(com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType itemAttachment) {
        generatedSetterHelperImpl(itemAttachment, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ItemAttachment" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType addNewItemAttachment() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
