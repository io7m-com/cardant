/*
 * An XML document type.
 * Localname: FileData
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.FileDataDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one FileData(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class FileDataDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.FileDataDocument {
    private static final long serialVersionUID = 1L;

    public FileDataDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "FileData"),
    };


    /**
     * Gets the "FileData" element
     */
    @Override
    public byte[] getFileData() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target.getByteArrayValue();
        }
    }

    /**
     * Gets (as xml) the "FileData" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.FileDataType xgetFileData() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.FileDataType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.FileDataType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return target;
        }
    }

    /**
     * Sets the "FileData" element
     */
    @Override
    public void setFileData(byte[] fileData) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.setByteArrayValue(fileData);
        }
    }

    /**
     * Sets (as xml) the "FileData" element
     */
    @Override
    public void xsetFileData(com.io7m.cardant.protocol.inventory.v1.beans.FileDataType fileData) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.FileDataType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.FileDataType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (com.io7m.cardant.protocol.inventory.v1.beans.FileDataType)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.set(fileData);
        }
    }
}
