/*
 * An XML document type.
 * Localname: Tag
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.TagDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one Tag(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class TagDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.TagDocument {
    private static final long serialVersionUID = 1L;

    public TagDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "Tag"),
    };


    /**
     * Gets the "Tag" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.TagType getTag() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.TagType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.TagType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "Tag" element
     */
    @Override
    public void setTag(com.io7m.cardant.protocol.inventory.v1.beans.TagType tag) {
        generatedSetterHelperImpl(tag, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "Tag" element
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
}
