/*
 * XML Type:  IDType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.IDType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.IDType;
import com.io7m.cardant.protocol.inventory.v1.beans.UUIDType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

import javax.xml.namespace.QName;

/**
 * An XML IDType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class IDTypeImpl extends XmlComplexContentImpl implements
  IDType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("", "value"),
  };

  public IDTypeImpl(final SchemaType sType)
  {
    super(sType);
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
        PROPERTY_QNAME[0]);
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
        PROPERTY_QNAME[0]);
      if (target == null) {
        target = (SimpleValue) this.get_store().add_attribute_user(
          PROPERTY_QNAME[0]);
      }
      target.setStringValue(value);
    }
  }

  /**
   * Gets (as xml) the "value" attribute
   */
  @Override
  public UUIDType xgetValue()
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
   * Sets (as xml) the "value" attribute
   */
  @Override
  public void xsetValue(final UUIDType value)
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
      target.set(value);
    }
  }
}
