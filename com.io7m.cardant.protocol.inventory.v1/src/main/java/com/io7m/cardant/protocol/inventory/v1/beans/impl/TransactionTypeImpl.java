/*
 * XML Type:  TransactionType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.TransactionType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.CommandType;
import com.io7m.cardant.protocol.inventory.v1.beans.TransactionType;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * An XML TransactionType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class TransactionTypeImpl extends MessageTypeImpl implements
  TransactionType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "Command"),
  };
  private static final QNameSet[] PROPERTY_QSET = {
    QNameSet.forArray(new QName[]{
      new QName("urn:com.io7m.cardant.inventory:1", "CommandTagList"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandItemAttachmentPut"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandLocationGet"),
      new QName("urn:com.io7m.cardant.inventory:1", "Command"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandTagsDelete"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandItemUpdate"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandItemGet"),
      new QName(
        "urn:com.io7m.cardant.inventory:1",
        "CommandItemAttachmentRemove"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandTagsPut"),
      new QName(
        "urn:com.io7m.cardant.inventory:1",
        "CommandItemMetadataRemove"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandLocationList"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandItemMetadataPut"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandItemLocationList"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandItemCreate"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandItemList"),
      new QName(
        "urn:com.io7m.cardant.inventory:1",
        "CommandLoginUsernamePassword"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandItemRemove"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandLocationPut"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandItemReposit"),
    }),
  };

  public TransactionTypeImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets a List of "Command" elements
   */
  @Override
  public List<CommandType> getCommandList()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      return new JavaListXmlObject<>(
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
  public CommandType[] getCommandArray()
  {
    return this.getXmlObjectArray(
      PROPERTY_QSET[0],
      new CommandType[0]);
  }

  /**
   * Sets array of all "Command" element  WARNING: This method is not atomicaly synchronized.
   */
  @Override
  public void setCommandArray(final CommandType[] commandArray)
  {
    this.check_orphaned();
    this.arraySetterHelper(commandArray, PROPERTY_QNAME[0], PROPERTY_QSET[0]);
  }

  /**
   * Gets ith "Command" element
   */
  @Override
  public CommandType getCommandArray(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandType target = null;
      target = (CommandType) this.get_store().find_element_user(
        PROPERTY_QSET[0],
        i);
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
  public int sizeOfCommandArray()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      return this.get_store().count_elements(PROPERTY_QSET[0]);
    }
  }

  /**
   * Sets ith "Command" element
   */
  @Override
  public void setCommandArray(
    final int i,
    final CommandType command)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandType target = null;
      target = (CommandType) this.get_store().find_element_user(
        PROPERTY_QSET[0],
        i);
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
  public CommandType insertNewCommand(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandType target = null;
      target = (CommandType) this.get_store().insert_element_user(
        PROPERTY_QSET[0],
        PROPERTY_QNAME[0],
        i);
      return target;
    }
  }

  /**
   * Appends and returns a new empty value (as xml) as the last "Command" element
   */
  @Override
  public CommandType addNewCommand()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandType target = null;
      target = (CommandType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }

  /**
   * Removes the ith "Command" element
   */
  @Override
  public void removeCommand(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      this.get_store().remove_element(PROPERTY_QSET[0], i);
    }
  }
}
