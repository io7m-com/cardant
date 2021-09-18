/*
 * XML Type:  CommandLoginUsernamePasswordType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlToken;

import javax.xml.namespace.QName;

/**
 * An XML CommandLoginUsernamePasswordType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class CommandLoginUsernamePasswordTypeImpl extends CommandTypeImpl implements
  CommandLoginUsernamePasswordType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("", "user"),
    new QName("", "password"),
  };

  public CommandLoginUsernamePasswordTypeImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "user" attribute
   */
  @Override
  public String getUser()
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
   * Sets the "user" attribute
   */
  @Override
  public void setUser(final String user)
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
      target.setStringValue(user);
    }
  }

  /**
   * Gets (as xml) the "user" attribute
   */
  @Override
  public XmlToken xgetUser()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      XmlToken target = null;
      target = (XmlToken) this.get_store().find_attribute_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }

  /**
   * Sets (as xml) the "user" attribute
   */
  @Override
  public void xsetUser(final XmlToken user)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      XmlToken target = null;
      target = (XmlToken) this.get_store().find_attribute_user(
        PROPERTY_QNAME[0]);
      if (target == null) {
        target = (XmlToken) this.get_store().add_attribute_user(
          PROPERTY_QNAME[0]);
      }
      target.set(user);
    }
  }

  /**
   * Gets the "password" attribute
   */
  @Override
  public String getPassword()
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
   * Sets the "password" attribute
   */
  @Override
  public void setPassword(final String password)
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
      target.setStringValue(password);
    }
  }

  /**
   * Gets (as xml) the "password" attribute
   */
  @Override
  public XmlString xgetPassword()
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
   * Sets (as xml) the "password" attribute
   */
  @Override
  public void xsetPassword(final XmlString password)
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
      target.set(password);
    }
  }
}
