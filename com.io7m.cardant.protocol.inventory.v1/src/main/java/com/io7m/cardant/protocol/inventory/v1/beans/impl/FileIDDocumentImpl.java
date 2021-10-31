/*
 * An XML document type.
 * Localname: FileID
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.FileIDDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one FileID(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class FileIDDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.IDDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.FileIDDocument {
    private static final long serialVersionUID = 1L;

    public FileIDDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "FileID"),
    };


    /**
     * Gets the "FileID" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.FileIDType getFileID() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.FileIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.FileIDType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "FileID" element
     */
    @Override
    public void setFileID(com.io7m.cardant.protocol.inventory.v1.beans.FileIDType fileID) {
        generatedSetterHelperImpl(fileID, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "FileID" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.FileIDType addNewFileID() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.FileIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.FileIDType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
