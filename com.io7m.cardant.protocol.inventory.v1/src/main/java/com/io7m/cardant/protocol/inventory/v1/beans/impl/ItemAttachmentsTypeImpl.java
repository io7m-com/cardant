/*
 * XML Type:  ItemAttachmentsType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML ItemAttachmentsType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ItemAttachmentsTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType {
    private static final long serialVersionUID = 1L;

    public ItemAttachmentsTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ItemAttachment"),
    };


    /**
     * Gets a List of "ItemAttachment" elements
     */
    @Override
    public java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType> getItemAttachmentList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getItemAttachmentArray,
                this::setItemAttachmentArray,
                this::insertNewItemAttachment,
                this::removeItemAttachment,
                this::sizeOfItemAttachmentArray
            );
        }
    }

    /**
     * Gets array of all "ItemAttachment" elements
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType[] getItemAttachmentArray() {
        return getXmlObjectArray(PROPERTY_QNAME[0], new com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType[0]);
    }

    /**
     * Gets ith "ItemAttachment" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType getItemAttachmentArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "ItemAttachment" element
     */
    @Override
    public int sizeOfItemAttachmentArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets array of all "ItemAttachment" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setItemAttachmentArray(com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType[] itemAttachmentArray) {
        check_orphaned();
        arraySetterHelper(itemAttachmentArray, PROPERTY_QNAME[0]);
    }

    /**
     * Sets ith "ItemAttachment" element
     */
    @Override
    public void setItemAttachmentArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType itemAttachment) {
        generatedSetterHelperImpl(itemAttachment, PROPERTY_QNAME[0], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "ItemAttachment" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType insertNewItemAttachment(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "ItemAttachment" element
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

    /**
     * Removes the ith "ItemAttachment" element
     */
    @Override
    public void removeItemAttachment(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}
