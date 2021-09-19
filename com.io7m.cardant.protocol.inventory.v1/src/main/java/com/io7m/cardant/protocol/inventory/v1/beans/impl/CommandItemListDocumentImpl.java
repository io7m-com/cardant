/*
 * An XML document type.
 * Localname: CommandItemList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one CommandItemList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandItemListDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandItemListDocument {
    private static final long serialVersionUID = 1L;

    public CommandItemListDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemList"),
    };


    /**
     * Gets the "CommandItemList" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandItemListType getCommandItemList() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandItemListType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandItemListType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "CommandItemList" element
     */
    @Override
    public void setCommandItemList(com.io7m.cardant.protocol.inventory.v1.beans.CommandItemListType commandItemList) {
        generatedSetterHelperImpl(commandItemList, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "CommandItemList" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandItemListType addNewCommandItemList() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandItemListType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandItemListType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
