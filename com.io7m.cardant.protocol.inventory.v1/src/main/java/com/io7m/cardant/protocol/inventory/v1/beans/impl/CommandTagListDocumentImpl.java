/*
 * An XML document type.
 * Localname: CommandTagList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandTagListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one CommandTagList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandTagListDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandTagListDocument {
    private static final long serialVersionUID = 1L;

    public CommandTagListDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "CommandTagList"),
    };


    /**
     * Gets the "CommandTagList" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandTagListType getCommandTagList() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandTagListType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandTagListType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "CommandTagList" element
     */
    @Override
    public void setCommandTagList(com.io7m.cardant.protocol.inventory.v1.beans.CommandTagListType commandTagList) {
        generatedSetterHelperImpl(commandTagList, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "CommandTagList" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandTagListType addNewCommandTagList() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandTagListType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandTagListType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
