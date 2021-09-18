/*
 * XML Type:  ItemRepositMoveType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositMoveType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositMoveType;
import com.io7m.cardant.protocol.inventory.v1.beans.UUIDType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlUnsignedLong;

import javax.xml.namespace.QName;
import java.math.BigInteger;

/**
 * An XML ItemRepositMoveType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ItemRepositMoveTypeImpl extends ItemRepositTypeImpl implements
  ItemRepositMoveType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("", "item"),
    new QName("", "fromLocation"),
    new QName("", "toLocation"),
    new QName("", "count"),
  };

  public ItemRepositMoveTypeImpl(final SchemaType sType)
  {
    super(sType);
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
        PROPERTY_QNAME[0]);
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
        PROPERTY_QNAME[0]);
      if (target == null) {
        target = (SimpleValue) this.get_store().add_attribute_user(
          PROPERTY_QNAME[0]);
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
        PROPERTY_QNAME[0]);
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
        PROPERTY_QNAME[0]);
      if (target == null) {
        target = (UUIDType) this.get_store().add_attribute_user(
          PROPERTY_QNAME[0]);
      }
      target.set(item);
    }
  }

  /**
   * Gets the "fromLocation" attribute
   */
  @Override
  public String getFromLocation()
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
   * Sets the "fromLocation" attribute
   */
  @Override
  public void setFromLocation(final String fromLocation)
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
      target.setStringValue(fromLocation);
    }
  }

  /**
   * Gets (as xml) the "fromLocation" attribute
   */
  @Override
  public UUIDType xgetFromLocation()
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
   * Sets (as xml) the "fromLocation" attribute
   */
  @Override
  public void xsetFromLocation(final UUIDType fromLocation)
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
      target.set(fromLocation);
    }
  }

  /**
   * Gets the "toLocation" attribute
   */
  @Override
  public String getToLocation()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_attribute_user(
        PROPERTY_QNAME[2]);
      return (target == null) ? null : target.getStringValue();
    }
  }

  /**
   * Sets the "toLocation" attribute
   */
  @Override
  public void setToLocation(final String toLocation)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_attribute_user(
        PROPERTY_QNAME[2]);
      if (target == null) {
        target = (SimpleValue) this.get_store().add_attribute_user(
          PROPERTY_QNAME[2]);
      }
      target.setStringValue(toLocation);
    }
  }

  /**
   * Gets (as xml) the "toLocation" attribute
   */
  @Override
  public UUIDType xgetToLocation()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      UUIDType target = null;
      target = (UUIDType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[2]);
      return target;
    }
  }

  /**
   * Sets (as xml) the "toLocation" attribute
   */
  @Override
  public void xsetToLocation(final UUIDType toLocation)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      UUIDType target = null;
      target = (UUIDType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[2]);
      if (target == null) {
        target = (UUIDType) this.get_store().add_attribute_user(
          PROPERTY_QNAME[2]);
      }
      target.set(toLocation);
    }
  }

  /**
   * Gets the "count" attribute
   */
  @Override
  public BigInteger getCount()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_attribute_user(
        PROPERTY_QNAME[3]);
      return (target == null) ? null : target.getBigIntegerValue();
    }
  }

  /**
   * Sets the "count" attribute
   */
  @Override
  public void setCount(final BigInteger count)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_attribute_user(
        PROPERTY_QNAME[3]);
      if (target == null) {
        target = (SimpleValue) this.get_store().add_attribute_user(
          PROPERTY_QNAME[3]);
      }
      target.setBigIntegerValue(count);
    }
  }

  /**
   * Gets (as xml) the "count" attribute
   */
  @Override
  public XmlUnsignedLong xgetCount()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      XmlUnsignedLong target = null;
      target = (XmlUnsignedLong) this.get_store().find_attribute_user(
        PROPERTY_QNAME[3]);
      return target;
    }
  }

  /**
   * Sets (as xml) the "count" attribute
   */
  @Override
  public void xsetCount(final XmlUnsignedLong count)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      XmlUnsignedLong target = null;
      target = (XmlUnsignedLong) this.get_store().find_attribute_user(
        PROPERTY_QNAME[3]);
      if (target == null) {
        target = (XmlUnsignedLong) this.get_store().add_attribute_user(
          PROPERTY_QNAME[3]);
      }
      target.set(count);
    }
  }
}
