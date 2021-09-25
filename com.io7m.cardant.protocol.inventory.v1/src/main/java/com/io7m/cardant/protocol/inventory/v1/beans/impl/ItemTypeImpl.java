/*
 * XML Type:  ItemType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML ItemType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ItemTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ItemType {
    private static final long serialVersionUID = 1L;

    public ItemTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ItemMetadatas"),
        new QName("urn:com.io7m.cardant.inventory:1", "Tags"),
        new QName("urn:com.io7m.cardant.inventory:1", "ItemAttachments"),
        new QName("", "id"),
        new QName("", "name"),
        new QName("", "countTotal"),
        new QName("", "countHere"),
    };


    /**
     * Gets the "ItemMetadatas" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType getItemMetadatas() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ItemMetadatas" element
     */
    @Override
    public void setItemMetadatas(com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType itemMetadatas) {
        generatedSetterHelperImpl(itemMetadatas, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ItemMetadatas" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType addNewItemMetadatas() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Gets the "Tags" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.TagsType getTags() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.TagsType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.TagsType)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "Tags" element
     */
    @Override
    public void setTags(com.io7m.cardant.protocol.inventory.v1.beans.TagsType tags) {
        generatedSetterHelperImpl(tags, PROPERTY_QNAME[1], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "Tags" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.TagsType addNewTags() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.TagsType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.TagsType)get_store().add_element_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /**
     * Gets the "ItemAttachments" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType getItemAttachments() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType)get_store().find_element_user(PROPERTY_QNAME[2], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "ItemAttachments" element
     */
    @Override
    public void setItemAttachments(com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType itemAttachments) {
        generatedSetterHelperImpl(itemAttachments, PROPERTY_QNAME[2], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ItemAttachments" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType addNewItemAttachments() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType)get_store().add_element_user(PROPERTY_QNAME[2]);
            return target;
        }
    }

    /**
     * Gets the "id" attribute
     */
    @Override
    public java.lang.String getId() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[3]);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "id" attribute
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.UUIDType xgetId() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.UUIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.UUIDType)get_store().find_attribute_user(PROPERTY_QNAME[3]);
            return target;
        }
    }

    /**
     * Sets the "id" attribute
     */
    @Override
    public void setId(java.lang.String id) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[3]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[3]);
            }
            target.setStringValue(id);
        }
    }

    /**
     * Sets (as xml) the "id" attribute
     */
    @Override
    public void xsetId(com.io7m.cardant.protocol.inventory.v1.beans.UUIDType id) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.UUIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.UUIDType)get_store().find_attribute_user(PROPERTY_QNAME[3]);
            if (target == null) {
                target = (com.io7m.cardant.protocol.inventory.v1.beans.UUIDType)get_store().add_attribute_user(PROPERTY_QNAME[3]);
            }
            target.set(id);
        }
    }

    /**
     * Gets the "name" attribute
     */
    @Override
    public java.lang.String getName() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[4]);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "name" attribute
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemNameType xgetName() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemNameType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemNameType)get_store().find_attribute_user(PROPERTY_QNAME[4]);
            return target;
        }
    }

    /**
     * Sets the "name" attribute
     */
    @Override
    public void setName(java.lang.String name) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[4]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[4]);
            }
            target.setStringValue(name);
        }
    }

    /**
     * Sets (as xml) the "name" attribute
     */
    @Override
    public void xsetName(com.io7m.cardant.protocol.inventory.v1.beans.ItemNameType name) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemNameType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemNameType)get_store().find_attribute_user(PROPERTY_QNAME[4]);
            if (target == null) {
                target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemNameType)get_store().add_attribute_user(PROPERTY_QNAME[4]);
            }
            target.set(name);
        }
    }

    /**
     * Gets the "countTotal" attribute
     */
    @Override
    public java.math.BigInteger getCountTotal() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[5]);
            return (target == null) ? null : target.getBigIntegerValue();
        }
    }

    /**
     * Gets (as xml) the "countTotal" attribute
     */
    @Override
    public org.apache.xmlbeans.XmlUnsignedLong xgetCountTotal() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlUnsignedLong target = null;
            target = (org.apache.xmlbeans.XmlUnsignedLong)get_store().find_attribute_user(PROPERTY_QNAME[5]);
            return target;
        }
    }

    /**
     * Sets the "countTotal" attribute
     */
    @Override
    public void setCountTotal(java.math.BigInteger countTotal) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[5]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[5]);
            }
            target.setBigIntegerValue(countTotal);
        }
    }

    /**
     * Sets (as xml) the "countTotal" attribute
     */
    @Override
    public void xsetCountTotal(org.apache.xmlbeans.XmlUnsignedLong countTotal) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlUnsignedLong target = null;
            target = (org.apache.xmlbeans.XmlUnsignedLong)get_store().find_attribute_user(PROPERTY_QNAME[5]);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlUnsignedLong)get_store().add_attribute_user(PROPERTY_QNAME[5]);
            }
            target.set(countTotal);
        }
    }

    /**
     * Gets the "countHere" attribute
     */
    @Override
    public java.math.BigInteger getCountHere() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[6]);
            return (target == null) ? null : target.getBigIntegerValue();
        }
    }

    /**
     * Gets (as xml) the "countHere" attribute
     */
    @Override
    public org.apache.xmlbeans.XmlUnsignedLong xgetCountHere() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlUnsignedLong target = null;
            target = (org.apache.xmlbeans.XmlUnsignedLong)get_store().find_attribute_user(PROPERTY_QNAME[6]);
            return target;
        }
    }

    /**
     * Sets the "countHere" attribute
     */
    @Override
    public void setCountHere(java.math.BigInteger countHere) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[6]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[6]);
            }
            target.setBigIntegerValue(countHere);
        }
    }

    /**
     * Sets (as xml) the "countHere" attribute
     */
    @Override
    public void xsetCountHere(org.apache.xmlbeans.XmlUnsignedLong countHere) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlUnsignedLong target = null;
            target = (org.apache.xmlbeans.XmlUnsignedLong)get_store().find_attribute_user(PROPERTY_QNAME[6]);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlUnsignedLong)get_store().add_attribute_user(PROPERTY_QNAME[6]);
            }
            target.set(countHere);
        }
    }
}
