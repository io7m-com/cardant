/*
 * An XML document type.
 * Localname: TagID
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.TagIDDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one TagID(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class TagIDDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.IDDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.TagIDDocument {
    private static final long serialVersionUID = 1L;

    public TagIDDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "TagID"),
    };


    /**
     * Gets the "TagID" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.TagIDType getTagID() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.TagIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.TagIDType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "TagID" element
     */
    @Override
    public void setTagID(com.io7m.cardant.protocol.inventory.v1.beans.TagIDType tagID) {
        generatedSetterHelperImpl(tagID, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "TagID" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.TagIDType addNewTagID() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.TagIDType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.TagIDType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
