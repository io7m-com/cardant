/*
 * An XML document type.
 * Localname: UserID
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.UserIDDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.UserIDDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.UserIDType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one UserID(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class UserIDDocumentImpl extends IDDocumentImpl implements
  UserIDDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "UserID"),
  };

  public UserIDDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "UserID" element
   */
  @Override
  public UserIDType getUserID()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      UserIDType target = null;
      target = (UserIDType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "UserID" element
   */
  @Override
  public void setUserID(final UserIDType userID)
  {
    this.generatedSetterHelperImpl(
      userID,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "UserID" element
   */
  @Override
  public UserIDType addNewUserID()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      UserIDType target = null;
      target = (UserIDType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
