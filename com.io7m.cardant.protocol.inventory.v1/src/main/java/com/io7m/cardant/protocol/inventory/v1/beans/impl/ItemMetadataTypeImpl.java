/*
 * XML Type:  ItemMetadataType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataNameType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataValueType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

import javax.xml.namespace.QName;

/**
 * An XML ItemMetadataType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ItemMetadataTypeImpl extends XmlComplexContentImpl implements
  ItemMetadataType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("", "name"),
    new QName("", "value"),
  };

  public ItemMetadataTypeImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "name" attribute
   */
  @Override
  public String getName()
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
   * Sets the "name" attribute
   */
  @Override
  public void setName(final String name)
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
      target.setStringValue(name);
    }
  }

  /**
   * Gets (as xml) the "name" attribute
   */
  @Override
  public ItemMetadataNameType xgetName()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemMetadataNameType target = null;
      target = (ItemMetadataNameType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }

  /**
   * Sets (as xml) the "name" attribute
   */
  @Override
  public void xsetName(final ItemMetadataNameType name)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemMetadataNameType target = null;
      target = (ItemMetadataNameType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[0]);
      if (target == null) {
        target = (ItemMetadataNameType) this.get_store().add_attribute_user(
          PROPERTY_QNAME[0]);
      }
      target.set(name);
    }
  }

  /**
   * Gets the "value" attribute
   */
  @Override
  public String getValue()
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
   * Sets the "value" attribute
   */
  @Override
  public void setValue(final String value)
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
      target.setStringValue(value);
    }
  }

  /**
   * Gets (as xml) the "value" attribute
   */
  @Override
  public ItemMetadataValueType xgetValue()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemMetadataValueType target = null;
      target = (ItemMetadataValueType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[1]);
      return target;
    }
  }

  /**
   * Sets (as xml) the "value" attribute
   */
  @Override
  public void xsetValue(final ItemMetadataValueType value)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemMetadataValueType target = null;
      target = (ItemMetadataValueType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[1]);
      if (target == null) {
        target = (ItemMetadataValueType) this.get_store().add_attribute_user(
          PROPERTY_QNAME[1]);
      }
      target.set(value);
    }
  }
}
