/*
 * XML Type:  CommandItemRepositType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRepositType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRepositType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositType;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.SchemaType;

import javax.xml.namespace.QName;

/**
 * An XML CommandItemRepositType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class CommandItemRepositTypeImpl extends CommandTypeImpl implements
  CommandItemRepositType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ItemReposit"),
  };
  private static final QNameSet[] PROPERTY_QSET = {
    QNameSet.forArray(new QName[]{
      new QName("urn:com.io7m.cardant.inventory:1", "ItemReposit"),
      new QName("urn:com.io7m.cardant.inventory:1", "ItemRepositMove"),
      new QName("urn:com.io7m.cardant.inventory:1", "ItemRepositRemove"),
      new QName("urn:com.io7m.cardant.inventory:1", "ItemRepositAdd"),
    }),
  };

  public CommandItemRepositTypeImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ItemReposit" element
   */
  @Override
  public ItemRepositType getItemReposit()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemRepositType target = null;
      target = (ItemRepositType) this.get_store().find_element_user(
        PROPERTY_QSET[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ItemReposit" element
   */
  @Override
  public void setItemReposit(final ItemRepositType itemReposit)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemRepositType target = null;
      target = (ItemRepositType) this.get_store().find_element_user(
        PROPERTY_QSET[0],
        0);
      if (target == null) {
        target = (ItemRepositType) this.get_store().add_element_user(
          PROPERTY_QNAME[0]);
      }
      target.set(itemReposit);
    }
  }

  /**
   * Appends and returns a new empty "ItemReposit" element
   */
  @Override
  public ItemRepositType addNewItemReposit()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemRepositType target = null;
      target = (ItemRepositType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
