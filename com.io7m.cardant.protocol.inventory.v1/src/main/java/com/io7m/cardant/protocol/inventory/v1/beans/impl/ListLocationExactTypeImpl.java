/*
 * XML Type:  ListLocationExactType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType;
import com.io7m.cardant.protocol.inventory.v1.beans.UUIDType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;

import javax.xml.namespace.QName;

/**
 * An XML ListLocationExactType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ListLocationExactTypeImpl extends ListLocationsBehaviourTypeImpl implements
  ListLocationExactType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("", "location"),
  };

  public ListLocationExactTypeImpl(final SchemaType sType)
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
}
