/*
 * An XML document type.
 * Localname: Message
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.MessageDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.MessageDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.MessageType;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

import javax.xml.namespace.QName;

/**
 * A document containing one Message(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class MessageDocumentImpl extends XmlComplexContentImpl implements
  MessageDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "Message"),
  };
  private static final QNameSet[] PROPERTY_QSET = {
    QNameSet.forArray(new QName[]{
      new QName("urn:com.io7m.cardant.inventory:1", "ResponseTagList"),
      new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemRemove"),
      new QName("urn:com.io7m.cardant.inventory:1", "ResponseTagsDelete"),
      new QName("urn:com.io7m.cardant.inventory:1", "Event"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandItemAttachmentPut"),
      new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemMetadataPut"),
      new QName("urn:com.io7m.cardant.inventory:1", "Command"),
      new QName("urn:com.io7m.cardant.inventory:1", "Transaction"),
      new QName("urn:com.io7m.cardant.inventory:1", "Response"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandItemGet"),
      new QName(
        "urn:com.io7m.cardant.inventory:1",
        "CommandItemAttachmentRemove"),
      new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemCreate"),
      new QName(
        "urn:com.io7m.cardant.inventory:1",
        "ResponseItemAttachmentRemove"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandItemMetadataPut"),
      new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemGet"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandItemList"),
      new QName(
        "urn:com.io7m.cardant.inventory:1",
        "CommandLoginUsernamePassword"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandItemRemove"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandTagList"),
      new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemList"),
      new QName(
        "urn:com.io7m.cardant.inventory:1",
        "ResponseItemMetadataRemove"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandLocationGet"),
      new QName("urn:com.io7m.cardant.inventory:1", "EventUpdated"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandTagsDelete"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandItemUpdate"),
      new QName(
        "urn:com.io7m.cardant.inventory:1",
        "ResponseLoginUsernamePassword"),
      new QName(
        "urn:com.io7m.cardant.inventory:1",
        "ResponseItemAttachmentPut"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandTagsPut"),
      new QName("urn:com.io7m.cardant.inventory:1", "ResponseTagsPut"),
      new QName(
        "urn:com.io7m.cardant.inventory:1",
        "CommandItemMetadataRemove"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandLocationList"),
      new QName("urn:com.io7m.cardant.inventory:1", "Message"),
      new QName("urn:com.io7m.cardant.inventory:1", "ResponseItemUpdate"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandItemLocationList"),
      new QName("urn:com.io7m.cardant.inventory:1", "ResponseError"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandItemCreate"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandLocationPut"),
      new QName("urn:com.io7m.cardant.inventory:1", "CommandItemReposit"),
    }),
  };

  public MessageDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "Message" element
   */
  @Override
  public MessageType getMessage()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      MessageType target = null;
      target = (MessageType) this.get_store().find_element_user(
        PROPERTY_QSET[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "Message" element
   */
  @Override
  public void setMessage(final MessageType message)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      MessageType target = null;
      target = (MessageType) this.get_store().find_element_user(
        PROPERTY_QSET[0],
        0);
      if (target == null) {
        target = (MessageType) this.get_store().add_element_user(
          PROPERTY_QNAME[0]);
      }
      target.set(message);
    }
  }

  /**
   * Appends and returns a new empty "Message" element
   */
  @Override
  public MessageType addNewMessage()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      MessageType target = null;
      target = (MessageType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
