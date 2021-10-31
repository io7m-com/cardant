/*
 * XML Type:  ItemAttachmentType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML ItemAttachmentType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ItemAttachmentTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType {
    private static final long serialVersionUID = 1L;

    public ItemAttachmentTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "File"),
        new QName("", "relation"),
    };


    /**
     * Gets the "File" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.FileType getFile() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.FileType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.FileType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "File" element
     */
    @Override
    public void setFile(com.io7m.cardant.protocol.inventory.v1.beans.FileType file) {
        generatedSetterHelperImpl(file, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "File" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.FileType addNewFile() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.FileType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.FileType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Gets the "relation" attribute
     */
    @Override
    public java.lang.String getRelation() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "relation" attribute
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.RelationType xgetRelation() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.RelationType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.RelationType)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /**
     * Sets the "relation" attribute
     */
    @Override
    public void setRelation(java.lang.String relation) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[1]);
            }
            target.setStringValue(relation);
        }
    }

    /**
     * Sets (as xml) the "relation" attribute
     */
    @Override
    public void xsetRelation(com.io7m.cardant.protocol.inventory.v1.beans.RelationType relation) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.RelationType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.RelationType)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (com.io7m.cardant.protocol.inventory.v1.beans.RelationType)get_store().add_attribute_user(PROPERTY_QNAME[1]);
            }
            target.set(relation);
        }
    }
}
