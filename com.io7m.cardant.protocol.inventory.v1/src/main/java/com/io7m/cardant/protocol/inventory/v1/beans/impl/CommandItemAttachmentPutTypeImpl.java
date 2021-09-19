/*
 * XML Type:  CommandItemAttachmentPutType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentPutType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentPutType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType;
import com.io7m.cardant.protocol.inventory.v1.beans.UUIDType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * An XML CommandItemAttachmentPutType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class CommandItemAttachmentPutTypeImpl extends CommandTypeImpl implements
  CommandItemAttachmentPutType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ItemAttachment"),
    new QName("", "item"),
  };

  public CommandItemAttachmentPutTypeImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ItemAttachment" element
   */
  @Override
  public ItemAttachmentType getItemAttachment()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemAttachmentType target = null;
      target = (ItemAttachmentType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ItemAttachment" element
   */
  @Override
  public void setItemAttachment(final ItemAttachmentType itemAttachment)
  {
    this.generatedSetterHelperImpl(
      itemAttachment,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "ItemAttachment" element
   */
  @Override
  public ItemAttachmentType addNewItemAttachment()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemAttachmentType target = null;
      target = (ItemAttachmentType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }

  /**
   * Gets the "item" attribute
   */
  @Override
  public String getItem()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_attribute_user(
        PROPERTY_QNAME[1]);
      return (target == null) ? null : target.getStringValue();
    }
  }

  /**
   * Sets the "item" attribute
   */
  @Override
  public void setItem(final String item)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_attribute_user(
        PROPERTY_QNAME[1]);
      if (target == null) {
        target = (SimpleValue) this.get_store().add_attribute_user(
          PROPERTY_QNAME[1]);
      }
      target.setStringValue(item);
    }
  }

  /**
   * Gets (as xml) the "item" attribute
   */
  @Override
  public UUIDType xgetItem()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      UUIDType target = null;
      target = (UUIDType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[1]);
      return target;
    }
  }

  /**
   * Sets (as xml) the "item" attribute
   */
  @Override
  public void xsetItem(final UUIDType item)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      UUIDType target = null;
      target = (UUIDType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[1]);
      if (target == null) {
        target = (UUIDType) this.get_store().add_attribute_user(
          PROPERTY_QNAME[1]);
      }
      target.set(item);
    }
  }
}
