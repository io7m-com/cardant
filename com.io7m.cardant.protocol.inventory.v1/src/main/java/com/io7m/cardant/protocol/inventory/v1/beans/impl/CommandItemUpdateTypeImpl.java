/*
 * XML Type:  CommandItemUpdateType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemUpdateType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemUpdateType;
import com.io7m.cardant.protocol.inventory.v1.beans.UUIDType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlString;

import javax.xml.namespace.QName;

/**
 * An XML CommandItemUpdateType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class CommandItemUpdateTypeImpl extends CommandTypeImpl implements
  CommandItemUpdateType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("", "id"),
    new QName("", "name"),
  };

  public CommandItemUpdateTypeImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "id" attribute
   */
  @Override
  public String getId()
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
   * Sets the "id" attribute
   */
  @Override
  public void setId(final String id)
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
      target.setStringValue(id);
    }
  }

  /**
   * Gets (as xml) the "id" attribute
   */
  @Override
  public UUIDType xgetId()
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
   * Sets (as xml) the "id" attribute
   */
  @Override
  public void xsetId(final UUIDType id)
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
      target.set(id);
    }
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
        PROPERTY_QNAME[1]);
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
        PROPERTY_QNAME[1]);
      if (target == null) {
        target = (SimpleValue) this.get_store().add_attribute_user(
          PROPERTY_QNAME[1]);
      }
      target.setStringValue(name);
    }
  }

  /**
   * Gets (as xml) the "name" attribute
   */
  @Override
  public XmlString xgetName()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      XmlString target = null;
      target = (XmlString) this.get_store().find_attribute_user(
        PROPERTY_QNAME[1]);
      return target;
    }
  }

  /**
   * Sets (as xml) the "name" attribute
   */
  @Override
  public void xsetName(final XmlString name)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      XmlString target = null;
      target = (XmlString) this.get_store().find_attribute_user(
        PROPERTY_QNAME[1]);
      if (target == null) {
        target = (XmlString) this.get_store().add_attribute_user(
          PROPERTY_QNAME[1]);
      }
      target.set(name);
    }
  }
}
