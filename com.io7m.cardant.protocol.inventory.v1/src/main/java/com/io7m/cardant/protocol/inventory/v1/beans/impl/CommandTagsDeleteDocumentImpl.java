/*
 * An XML document type.
 * Localname: CommandTagsDelete
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsDeleteDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one CommandTagsDelete(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandTagsDeleteDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsDeleteDocument {
    private static final long serialVersionUID = 1L;

    public CommandTagsDeleteDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "CommandTagsDelete"),
    };


    /**
     * Gets the "CommandTagsDelete" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsDeleteType getCommandTagsDelete() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsDeleteType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsDeleteType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "CommandTagsDelete" element
     */
    @Override
    public void setCommandTagsDelete(com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsDeleteType commandTagsDelete) {
        generatedSetterHelperImpl(commandTagsDelete, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "CommandTagsDelete" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsDeleteType addNewCommandTagsDelete() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsDeleteType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsDeleteType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
