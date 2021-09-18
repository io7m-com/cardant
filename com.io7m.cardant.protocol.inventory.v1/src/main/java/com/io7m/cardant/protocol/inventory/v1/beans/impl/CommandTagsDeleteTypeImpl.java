/*
 * XML Type:  CommandTagsDeleteType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsDeleteType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML CommandTagsDeleteType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class CommandTagsDeleteTypeImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandTypeImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsDeleteType {
    private static final long serialVersionUID = 1L;

    public CommandTagsDeleteTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "Tags"),
    };


    /**
     * Gets the "Tags" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.TagsType getTags() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.TagsType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.TagsType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "Tags" element
     */
    @Override
    public void setTags(com.io7m.cardant.protocol.inventory.v1.beans.TagsType tags) {
        generatedSetterHelperImpl(tags, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "Tags" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.TagsType addNewTags() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.TagsType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.TagsType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
