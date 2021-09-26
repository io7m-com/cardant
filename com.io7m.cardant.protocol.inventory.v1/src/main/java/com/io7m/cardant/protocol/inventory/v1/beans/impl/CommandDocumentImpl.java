/*
 * An XML document type.
 * Localname: Command
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one Command(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandDocumentImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.MessageDocumentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument {
    private static final long serialVersionUID = 1L;

    public CommandDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "Command"),
    };

    private static final QNameSet[] PROPERTY_QSET = {
    QNameSet.forArray( new QName[] { 
        new QName("urn:com.io7m.cardant.inventory:1", "CommandTagList"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemAttachmentPut"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandLocationGet"),
        new QName("urn:com.io7m.cardant.inventory:1", "Command"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandTagsDelete"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemUpdate"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemGet"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemAttachmentRemove"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandTagsPut"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemMetadataRemove"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemsRemove"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandLocationList"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemMetadataPut"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemLocationsList"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemCreate"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemList"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandLoginUsernamePassword"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandLocationPut"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemReposit"),
    }),
    };

    /**
     * Gets the "Command" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandType getCommand() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandType)get_store().find_element_user(PROPERTY_QSET[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "Command" element
     */
    @Override
    public void setCommand(com.io7m.cardant.protocol.inventory.v1.beans.CommandType command) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandType)get_store().find_element_user(PROPERTY_QSET[0], 0);
            if (target == null) {
                target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandType)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.set(command);
        }
    }

    /**
     * Appends and returns a new empty "Command" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandType addNewCommand() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
