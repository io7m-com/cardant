/*
 * An XML document type.
 * Localname: ItemAttachmentID
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentIDDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one ItemAttachmentID(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ItemAttachmentIDDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.IDDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentIDDocument {
    private static final long serialVersionUID = 1L;

    public ItemAttachmentIDDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ItemAttachmentID"),
    };


    /**
     * Gets the "ItemAttachmentID" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentIDType getItemAttachmentID() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentIDType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ItemAttachmentID" element
     */
    @Override
    public void setItemAttachmentID(com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentIDType itemAttachmentID) {
        generatedSetterHelperImpl(itemAttachmentID, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ItemAttachmentID" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentIDType addNewItemAttachmentID() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentIDType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
