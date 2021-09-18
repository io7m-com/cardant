/*
 * XML Type:  TransactionType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.TransactionType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

import java.util.List;


/**
 * An XML TransactionType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public interface TransactionType extends MessageType
{
  DocumentFactory<TransactionType> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "transactiontypebe91type");
  SchemaType type = Factory.getType();


  /**
   * Gets a List of "Command" elements
   */
  List<CommandType> getCommandList();

  /**
   * Gets array of all "Command" elements
   */
  CommandType[] getCommandArray();

  /**
   * Sets array of all "Command" element
   */
  void setCommandArray(CommandType[] commandArray);

  /**
   * Gets ith "Command" element
   */
  CommandType getCommandArray(int i);

  /**
   * Returns number of "Command" element
   */
  int sizeOfCommandArray();

  /**
   * Sets ith "Command" element
   */
  void setCommandArray(
    int i,
    CommandType command);

  /**
   * Inserts and returns a new empty value (as xml) as the ith "Command" element
   */
  CommandType insertNewCommand(int i);

  /**
   * Appends and returns a new empty value (as xml) as the last "Command" element
   */
  CommandType addNewCommand();

  /**
   * Removes the ith "Command" element
   */
  void removeCommand(int i);
}
