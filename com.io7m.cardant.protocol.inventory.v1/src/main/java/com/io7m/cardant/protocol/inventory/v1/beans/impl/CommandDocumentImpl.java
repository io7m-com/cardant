/*
 * An XML document type.
 * Localname: Command
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandType;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.SchemaType;

import javax.xml.namespace.QName;

/**
 * A document containing one Command(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class CommandDocumentImpl extends MessageDocumentImpl implements
  CommandDocument
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

  public CommandDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "Command" element
   */
  @Override
  public CommandType getCommand()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandType target = null;
      target = (CommandType) this.get_store().find_element_user(
        PROPERTY_QSET[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "Command" element
   */
  @Override
  public void setCommand(final CommandType command)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      CommandType target = null;
      target = (CommandType) this.get_store().find_element_user(
        PROPERTY_QSET[0],
        0);
      if (target == null) {
        target = (CommandType) this.get_store().add_element_user(
          PROPERTY_QNAME[0]);
      }
      target.set(command);
    }
  }

  /**
   * Appends and returns a new empty "Command" element
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
}
