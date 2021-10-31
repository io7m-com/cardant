/*
 * XML Type:  TransactionType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.TransactionType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML TransactionType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface TransactionType extends com.io7m.cardant.protocol.inventory.v1.beans.MessageType {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.TransactionType> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sE431AFF67C9477B4270ED45520E13157.TypeSystemHolder.typeSystem, "transactiontypebe91type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "Command" elements
     */
    java.util.List<com.io7m.cardant.protocol.inventory.v1.beans.CommandType> getCommandList();

    /**
     * Gets array of all "Command" elements
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandType[] getCommandArray();

    /**
     * Gets ith "Command" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandType getCommandArray(int i);

    /**
     * Returns number of "Command" element
     */
    int sizeOfCommandArray();

    /**
     * Sets array of all "Command" element
     */
    void setCommandArray(com.io7m.cardant.protocol.inventory.v1.beans.CommandType[] commandArray);

    /**
     * Sets ith "Command" element
     */
    void setCommandArray(int i, com.io7m.cardant.protocol.inventory.v1.beans.CommandType command);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "Command" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandType insertNewCommand(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "Command" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.CommandType addNewCommand();

    /**
     * Removes the ith "Command" element
     */
    void removeCommand(int i);
}
