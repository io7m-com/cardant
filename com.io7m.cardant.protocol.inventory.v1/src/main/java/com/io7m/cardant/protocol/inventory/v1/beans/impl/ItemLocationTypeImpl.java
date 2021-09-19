/*
 * XML Type:  ItemLocationType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType;
import com.io7m.cardant.protocol.inventory.v1.beans.UUIDType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlUnsignedLong;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

import javax.xml.namespace.QName;
import java.math.BigInteger;

/**
 * An XML ItemLocationType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ItemLocationTypeImpl extends XmlComplexContentImpl implements
  ItemLocationType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("", "location"),
    new QName("", "item"),
    new QName("", "count"),
  };

  public ItemLocationTypeImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "location" attribute
   */
  @Override
  public String getLocation()
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
   * Sets the "location" attribute
   */
  @Override
  public void setLocation(final String location)
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
      target.setStringValue(location);
    }
  }

  /**
   * Gets (as xml) the "location" attribute
   */
  @Override
  public UUIDType xgetLocation()
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
   * Sets (as xml) the "location" attribute
   */
  @Override
  public void xsetLocation(final UUIDType location)
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
      target.set(location);
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
        PROPERTY_QNAME[2]);
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
        PROPERTY_QNAME[2]);
      if (target == null) {
        target = (SimpleValue) this.get_store().add_attribute_user(
          PROPERTY_QNAME[2]);
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
        PROPERTY_QNAME[2]);
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
        PROPERTY_QNAME[2]);
      if (target == null) {
        target = (XmlUnsignedLong) this.get_store().add_attribute_user(
          PROPERTY_QNAME[2]);
      }
      target.set(count);
    }
  }
}
