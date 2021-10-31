/*
 * XML Type:  FileType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.FileType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML FileType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class FileTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.FileType {
    private static final long serialVersionUID = 1L;

    public FileTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "FileData"),
        new QName("", "id"),
        new QName("", "description"),
        new QName("", "mediaType"),
        new QName("", "size"),
        new QName("", "hashAlgorithm"),
        new QName("", "hashValue"),
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
     * True if has "FileData" element
     */
    @Override
    public boolean isSetFileData() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[0]) != 0;
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

    /**
     * Unsets the "FileData" element
     */
    @Override
    public void unsetFileData() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[0], 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[1]);
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
            target = (com.io7m.cardant.protocol.inventory.v1.beans.UUIDType)get_store().find_attribute_user(PROPERTY_QNAME[1]);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[1]);
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
            target = (com.io7m.cardant.protocol.inventory.v1.beans.UUIDType)get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (com.io7m.cardant.protocol.inventory.v1.beans.UUIDType)get_store().add_attribute_user(PROPERTY_QNAME[1]);
            }
            target.set(id);
        }
    }

    /**
     * Gets the "description" attribute
     */
    @Override
    public java.lang.String getDescription() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[2]);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "description" attribute
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.FileDescriptionType xgetDescription() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.FileDescriptionType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.FileDescriptionType)get_store().find_attribute_user(PROPERTY_QNAME[2]);
            return target;
        }
    }

    /**
     * Sets the "description" attribute
     */
    @Override
    public void setDescription(java.lang.String description) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[2]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[2]);
            }
            target.setStringValue(description);
        }
    }

    /**
     * Sets (as xml) the "description" attribute
     */
    @Override
    public void xsetDescription(com.io7m.cardant.protocol.inventory.v1.beans.FileDescriptionType description) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.FileDescriptionType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.FileDescriptionType)get_store().find_attribute_user(PROPERTY_QNAME[2]);
            if (target == null) {
                target = (com.io7m.cardant.protocol.inventory.v1.beans.FileDescriptionType)get_store().add_attribute_user(PROPERTY_QNAME[2]);
            }
            target.set(description);
        }
    }

    /**
     * Gets the "mediaType" attribute
     */
    @Override
    public java.lang.String getMediaType() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[3]);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "mediaType" attribute
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.MediaType xgetMediaType() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.MediaType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.MediaType)get_store().find_attribute_user(PROPERTY_QNAME[3]);
            return target;
        }
    }

    /**
     * Sets the "mediaType" attribute
     */
    @Override
    public void setMediaType(java.lang.String mediaType) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[3]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[3]);
            }
            target.setStringValue(mediaType);
        }
    }

    /**
     * Sets (as xml) the "mediaType" attribute
     */
    @Override
    public void xsetMediaType(com.io7m.cardant.protocol.inventory.v1.beans.MediaType mediaType) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.MediaType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.MediaType)get_store().find_attribute_user(PROPERTY_QNAME[3]);
            if (target == null) {
                target = (com.io7m.cardant.protocol.inventory.v1.beans.MediaType)get_store().add_attribute_user(PROPERTY_QNAME[3]);
            }
            target.set(mediaType);
        }
    }

    /**
     * Gets the "size" attribute
     */
    @Override
    public java.math.BigInteger getSize() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[4]);
            return (target == null) ? null : target.getBigIntegerValue();
        }
    }

    /**
     * Gets (as xml) the "size" attribute
     */
    @Override
    public org.apache.xmlbeans.XmlUnsignedLong xgetSize() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlUnsignedLong target = null;
            target = (org.apache.xmlbeans.XmlUnsignedLong)get_store().find_attribute_user(PROPERTY_QNAME[4]);
            return target;
        }
    }

    /**
     * Sets the "size" attribute
     */
    @Override
    public void setSize(java.math.BigInteger size) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[4]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[4]);
            }
            target.setBigIntegerValue(size);
        }
    }

    /**
     * Sets (as xml) the "size" attribute
     */
    @Override
    public void xsetSize(org.apache.xmlbeans.XmlUnsignedLong size) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlUnsignedLong target = null;
            target = (org.apache.xmlbeans.XmlUnsignedLong)get_store().find_attribute_user(PROPERTY_QNAME[4]);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlUnsignedLong)get_store().add_attribute_user(PROPERTY_QNAME[4]);
            }
            target.set(size);
        }
    }

    /**
     * Gets the "hashAlgorithm" attribute
     */
    @Override
    public java.lang.String getHashAlgorithm() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[5]);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "hashAlgorithm" attribute
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.HashAlgorithmType xgetHashAlgorithm() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.HashAlgorithmType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.HashAlgorithmType)get_store().find_attribute_user(PROPERTY_QNAME[5]);
            return target;
        }
    }

    /**
     * Sets the "hashAlgorithm" attribute
     */
    @Override
    public void setHashAlgorithm(java.lang.String hashAlgorithm) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[5]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[5]);
            }
            target.setStringValue(hashAlgorithm);
        }
    }

    /**
     * Sets (as xml) the "hashAlgorithm" attribute
     */
    @Override
    public void xsetHashAlgorithm(com.io7m.cardant.protocol.inventory.v1.beans.HashAlgorithmType hashAlgorithm) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.HashAlgorithmType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.HashAlgorithmType)get_store().find_attribute_user(PROPERTY_QNAME[5]);
            if (target == null) {
                target = (com.io7m.cardant.protocol.inventory.v1.beans.HashAlgorithmType)get_store().add_attribute_user(PROPERTY_QNAME[5]);
            }
            target.set(hashAlgorithm);
        }
    }

    /**
     * Gets the "hashValue" attribute
     */
    @Override
    public java.lang.String getHashValue() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[6]);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "hashValue" attribute
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.HashValueType xgetHashValue() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.HashValueType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.HashValueType)get_store().find_attribute_user(PROPERTY_QNAME[6]);
            return target;
        }
    }

    /**
     * Sets the "hashValue" attribute
     */
    @Override
    public void setHashValue(java.lang.String hashValue) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[6]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[6]);
            }
            target.setStringValue(hashValue);
        }
    }

    /**
     * Sets (as xml) the "hashValue" attribute
     */
    @Override
    public void xsetHashValue(com.io7m.cardant.protocol.inventory.v1.beans.HashValueType hashValue) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.HashValueType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.HashValueType)get_store().find_attribute_user(PROPERTY_QNAME[6]);
            if (target == null) {
                target = (com.io7m.cardant.protocol.inventory.v1.beans.HashValueType)get_store().add_attribute_user(PROPERTY_QNAME[6]);
            }
            target.set(hashValue);
        }
    }
}
