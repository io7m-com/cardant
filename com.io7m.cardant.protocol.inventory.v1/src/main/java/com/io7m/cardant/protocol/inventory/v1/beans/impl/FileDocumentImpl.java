/*
 * An XML document type.
 * Localname: File
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.FileDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one File(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class FileDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.FileDocument {
    private static final long serialVersionUID = 1L;

    public FileDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "File"),
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
}
