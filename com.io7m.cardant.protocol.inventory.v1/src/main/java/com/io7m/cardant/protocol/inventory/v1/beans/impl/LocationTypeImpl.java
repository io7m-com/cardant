/*
 * XML Type:  LocationType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.LocationType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.LocationDescriptionType;
import com.io7m.cardant.protocol.inventory.v1.beans.LocationNameType;
import com.io7m.cardant.protocol.inventory.v1.beans.LocationType;
import com.io7m.cardant.protocol.inventory.v1.beans.UUIDType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

import javax.xml.namespace.QName;

/**
 * An XML LocationType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class LocationTypeImpl extends XmlComplexContentImpl implements
  LocationType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("", "id"),
    new QName("", "parent"),
    new QName("", "name"),
    new QName("", "description"),
  };

  public LocationTypeImpl(final SchemaType sType)
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
   * Gets the "parent" attribute
   */
  @Override
  public String getParent()
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
   * Sets the "parent" attribute
   */
  @Override
  public void setParent(final String parent)
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
      target.setStringValue(parent);
    }
  }

  /**
   * Gets (as xml) the "parent" attribute
   */
  @Override
  public UUIDType xgetParent()
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
   * True if has "parent" attribute
   */
  @Override
  public boolean isSetParent()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      return this.get_store().find_attribute_user(PROPERTY_QNAME[1]) != null;
    }
  }

  /**
   * Sets (as xml) the "parent" attribute
   */
  @Override
  public void xsetParent(final UUIDType parent)
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
      target.set(parent);
    }
  }

  /**
   * Unsets the "parent" attribute
   */
  @Override
  public void unsetParent()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      this.get_store().remove_attribute(PROPERTY_QNAME[1]);
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
        PROPERTY_QNAME[2]);
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
        PROPERTY_QNAME[2]);
      if (target == null) {
        target = (SimpleValue) this.get_store().add_attribute_user(
          PROPERTY_QNAME[2]);
      }
      target.setStringValue(name);
    }
  }

  /**
   * Gets (as xml) the "name" attribute
   */
  @Override
  public LocationNameType xgetName()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      LocationNameType target = null;
      target = (LocationNameType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[2]);
      return target;
    }
  }

  /**
   * Sets (as xml) the "name" attribute
   */
  @Override
  public void xsetName(final LocationNameType name)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      LocationNameType target = null;
      target = (LocationNameType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[2]);
      if (target == null) {
        target = (LocationNameType) this.get_store().add_attribute_user(
          PROPERTY_QNAME[2]);
      }
      target.set(name);
    }
  }

  /**
   * Gets the "description" attribute
   */
  @Override
  public String getDescription()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      SimpleValue target = null;
      target = (SimpleValue) this.get_store().find_attribute_user(
        PROPERTY_QNAME[3]);
      return (target == null) ? null : target.getStringValue();
    }
  }

  /**
   * Sets the "description" attribute
   */
  @Override
  public void setDescription(final String description)
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
      target.setStringValue(description);
    }
  }

  /**
   * Gets (as xml) the "description" attribute
   */
  @Override
  public LocationDescriptionType xgetDescription()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      LocationDescriptionType target = null;
      target = (LocationDescriptionType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[3]);
      return target;
    }
  }

  /**
   * Sets (as xml) the "description" attribute
   */
  @Override
  public void xsetDescription(final LocationDescriptionType description)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      LocationDescriptionType target = null;
      target = (LocationDescriptionType) this.get_store().find_attribute_user(
        PROPERTY_QNAME[3]);
      if (target == null) {
        target = (LocationDescriptionType) this.get_store().add_attribute_user(
          PROPERTY_QNAME[3]);
      }
      target.set(description);
    }
  }
}
