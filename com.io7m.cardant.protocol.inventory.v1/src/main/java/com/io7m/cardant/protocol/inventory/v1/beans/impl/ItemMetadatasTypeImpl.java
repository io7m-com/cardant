/*
 * XML Type:  ItemMetadatasType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML ItemMetadatasType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ItemMetadatasTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType {
    private static final long serialVersionUID = 1L;

    public ItemMetadatasTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "ItemMetadata"),
    };


    /**
     * Gets a List of "ItemMetadata" elements
     */
    @Override
    public java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType> getItemMetadataList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getItemMetadataArray,
                this::setItemMetadataArray,
                this::insertNewItemMetadata,
                this::removeItemMetadata,
                this::sizeOfItemMetadataArray
            );
        }
    }

    /**
     * Gets array of all "ItemMetadata" elements
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType[] getItemMetadataArray() {
        return getXmlObjectArray(PROPERTY_QNAME[0], new com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType[0]);
    }

    /**
     * Gets ith "ItemMetadata" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType getItemMetadataArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "ItemMetadata" element
     */
    @Override
    public int sizeOfItemMetadataArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets array of all "ItemMetadata" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setItemMetadataArray(com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType[] itemMetadataArray) {
        check_orphaned();
        arraySetterHelper(itemMetadataArray, PROPERTY_QNAME[0]);
    }

    /**
     * Sets ith "ItemMetadata" element
     */
    @Override
    public void setItemMetadataArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType itemMetadata) {
        generatedSetterHelperImpl(itemMetadata, PROPERTY_QNAME[0], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "ItemMetadata" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType insertNewItemMetadata(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "ItemMetadata" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType addNewItemMetadata() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Removes the ith "ItemMetadata" element
     */
    @Override
    public void removeItemMetadata(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}
