/*
 * XML Type:  FilesType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.FilesType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML FilesType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class FilesTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.FilesType {
    private static final long serialVersionUID = 1L;

    public FilesTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "File"),
    };


    /**
     * Gets a List of "File" elements
     */
    @Override
    public java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.FileType> getFileList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getFileArray,
                this::setFileArray,
                this::insertNewFile,
                this::removeFile,
                this::sizeOfFileArray
            );
        }
    }

    /**
     * Gets array of all "File" elements
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.FileType[] getFileArray() {
        return getXmlObjectArray(PROPERTY_QNAME[0], new com.io7m.cardant.protocol.inventory.v1.beans.FileType[0]);
    }

    /**
     * Gets ith "File" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.FileType getFileArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.FileType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.FileType)get_store().find_element_user(PROPERTY_QNAME[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "File" element
     */
    @Override
    public int sizeOfFileArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /**
     * Sets array of all "File" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setFileArray(com.io7m.cardant.protocol.inventory.v1.beans.FileType[] fileArray) {
        check_orphaned();
        arraySetterHelper(fileArray, PROPERTY_QNAME[0]);
    }

    /**
     * Sets ith "File" element
     */
    @Override
    public void setFileArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.FileType file) {
        generatedSetterHelperImpl(file, PROPERTY_QNAME[0], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "File" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.FileType insertNewFile(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.FileType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.FileType)get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "File" element
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
     * Removes the ith "File" element
     */
    @Override
    public void removeFile(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}
