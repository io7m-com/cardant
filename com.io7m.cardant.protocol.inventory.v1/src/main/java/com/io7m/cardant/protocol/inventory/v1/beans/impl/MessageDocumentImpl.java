/*
 * An XML document type.
 * Localname: Message
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.MessageDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * A document containing one Message(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class MessageDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.io7m.cardant.protocol.inventory.v1.beans.MessageDocument {
    private static final long serialVersionUID = 1L;

    public MessageDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "Message"),
    };

    private static final QNameSet[] PROPERTY_QSET = {
    QNameSet.forArray( new QName[] { 
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseTagList"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseTagsDelete"),
        new QName("urn:com.io7m.cardant.inventory:1", "Event"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseFilePut"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemMetadataPut"),
        new QName("urn:com.io7m.cardant.inventory:1", "Command"),
        new QName("urn:com.io7m.cardant.inventory:1", "Transaction"),
        new QName("urn:com.io7m.cardant.inventory:1", "Response"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemGet"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemAttachmentRemove"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemCreate"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemAttachmentRemove"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemsRemove"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemMetadataPut"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseLocationList"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemGet"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandFileRemove"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemAttachmentAdd"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemList"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandLoginUsernamePassword"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandTagList"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemList"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemsRemove"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemMetadataRemove"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandFilePut"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandLocationGet"),
        new QName("urn:com.io7m.cardant.inventory:1", "EventUpdated"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandTagsDelete"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemAttachmentAdd"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemUpdate"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseLoginUsernamePassword"),
        new QName("urn:com.io7m.cardant.inventory:1", "TransactionResponse"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseFileRemove"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemMetadataRemove"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandTagsPut"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseTagsPut"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandLocationList"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemLocationsList"),
        new QName("urn:com.io7m.cardant.inventory:1", "Message"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemUpdate"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseError"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemLocationsList"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemCreate"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseLocationPut"),
        new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemReposit"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemReposit"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandLocationPut"),
    }),
    };

    /**
     * Gets the "Message" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.MessageType getMessage() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.MessageType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.MessageType)get_store().find_element_user(PROPERTY_QSET[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "Message" element
     */
    @Override
    public void setMessage(com.io7m.cardant.protocol.inventory.v1.beans.MessageType message) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.MessageType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.MessageType)get_store().find_element_user(PROPERTY_QSET[0], 0);
            if (target == null) {
                target = (com.io7m.cardant.protocol.inventory.v1.beans.MessageType)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.set(message);
        }
    }

    /**
     * Appends and returns a new empty "Message" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.MessageType addNewMessage() {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.MessageType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.MessageType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
}
