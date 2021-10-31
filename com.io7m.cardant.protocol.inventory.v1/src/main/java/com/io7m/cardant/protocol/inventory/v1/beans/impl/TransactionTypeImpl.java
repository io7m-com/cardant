/*
 * XML Type:  TransactionType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.TransactionType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

/**
 * An XML TransactionType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class TransactionTypeImpl extends com.io7m.cardant.protocol.inventory.v1.beans.impl.MessageTypeImpl implements com.io7m.cardant.protocol.inventory.v1.beans.TransactionType {
    private static final long serialVersionUID = 1L;

    public TransactionTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("urn:com.io7m.cardant.inventory:1", "Command"),
    };

    private static final QNameSet[] PROPERTY_QSET = {
    QNameSet.forArray( new QName[] { 
        new QName("urn:com.io7m.cardant.inventory:1", "CommandTagList"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandFilePut"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandLocationGet"),
        new QName("urn:com.io7m.cardant.inventory:1", "Command"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandTagsDelete"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemAttachmentAdd"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemUpdate"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemGet"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemAttachmentRemove"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemMetadataRemove"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandTagsPut"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemsRemove"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandLocationList"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemMetadataPut"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemLocationsList"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandFileRemove"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemCreate"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemList"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandLoginUsernamePassword"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandItemReposit"),
        new QName("urn:com.io7m.cardant.inventory:1", "CommandLocationPut"),
    }),
    };

    /**
     * Gets a List of "Command" elements
     */
    @Override
    public java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.CommandType> getCommandList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getCommandArray,
                this::setCommandArray,
                this::insertNewCommand,
                this::removeCommand,
                this::sizeOfCommandArray
            );
        }
    }

    /**
     * Gets array of all "Command" elements
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandType[] getCommandArray() {
        return getXmlObjectArray(PROPERTY_QSET[0], new com.io7m.cardant.protocol.inventory.v1.beans.CommandType[0]);
    }

    /**
     * Gets ith "Command" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandType getCommandArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandType)get_store().find_element_user(PROPERTY_QSET[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "Command" element
     */
    @Override
    public int sizeOfCommandArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QSET[0]);
        }
    }

    /**
     * Sets array of all "Command" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setCommandArray(com.io7m.cardant.protocol.inventory.v1.beans.CommandType[] commandArray) {
        check_orphaned();
        arraySetterHelper(commandArray, PROPERTY_QNAME[0], PROPERTY_QSET[0]);
    }

    /**
     * Sets ith "Command" element
     */
    @Override
    public void setCommandArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.CommandType command) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandType)get_store().find_element_user(PROPERTY_QSET[0], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(command);
        }
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "Command" element
     */
    @Override
    public com.io7m.cardant.protocol.inventory.v1.beans.CommandType insertNewCommand(int i) {
        synchronized (monitor()) {
            check_orphaned();
            com.io7m.cardant.protocol.inventory.v1.beans.CommandType target = null;
            target = (com.io7m.cardant.protocol.inventory.v1.beans.CommandType)get_store().insert_element_user(PROPERTY_QSET[0], PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "Command" element
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

    /**
     * Removes the ith "Command" element
     */
    @Override
    public void removeCommand(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QSET[0], i);
        }
    }
}
