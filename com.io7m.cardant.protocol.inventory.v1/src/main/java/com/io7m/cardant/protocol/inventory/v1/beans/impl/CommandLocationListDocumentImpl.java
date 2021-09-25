/*
 * An XML document type.
 * Localname: CommandLocationList
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationListDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one CommandLocationList(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandLocationListDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.CommandDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationListDocument {
    private static final long serialVersionUID = 1L;

    public CommandLocationListDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "CommandLocationList"),
    };


    /**
     * Gets the "CommandLocationList" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationListType getCommandLocationList() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationListType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationListType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "CommandLocationList" element
     */
    @Override
    public void setCommandLocationList(com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationListType commandLocationList) {
        generatedSetterHelperImpl(commandLocationList, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "CommandLocationList" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationListType addNewCommandLocationList() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationListType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationListType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
