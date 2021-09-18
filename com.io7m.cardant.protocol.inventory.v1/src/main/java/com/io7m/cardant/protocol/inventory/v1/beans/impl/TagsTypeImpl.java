/*
 * XML Type:  TagsType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.TagsType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML TagsType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class TagsTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.TagsType {
    private static final long serialVersionUID = 1L;

    public TagsTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "Tag"),
    };


    /**
     * Gets a List of "Tag" elements
     */
    @Override
    public java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.TagType> getTagList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getTagArray,
                this::setTagArray,
                this::insertNewTag,
                this::removeTag,
                this::sizeOfTagArray
            );
        }
    }

    /**
     * Gets array of all "Tag" elements
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.TagType[] getTagArray() {
        return getXmlObjectArray(PROPERTY_QNAME[0], new com.io7m.cardant.protocol.inventory.v1.beans.TagType[0]);
    }

    /**
     * Gets ith "Tag" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.TagType getTagArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.TagType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.TagType)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "Tag" element
     */
    @Override
    public int sizeOfTagArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets array of all "Tag" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setTagArray(com.io7m.cardant.protocol.inventory.v1.beans.TagType[] tagArray) {
        check_orphaned();
        arraySetterHelper(tagArray, PROPERTY_QNAME[0]);
    }

    /**
     * Sets ith "Tag" element
     */
    @Override
    public void setTagArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.TagType tag) {
        generatedSetterHelperImpl(tag, PROPERTY_QNAME[0], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "Tag" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.TagType insertNewTag(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.TagType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.TagType)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "Tag" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.TagType addNewTag() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.TagType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.TagType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Removes the ith "Tag" element
     */
    @Override
    public void removeTag(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}
