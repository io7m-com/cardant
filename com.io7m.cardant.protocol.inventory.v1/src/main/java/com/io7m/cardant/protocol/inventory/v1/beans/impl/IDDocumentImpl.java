/*
 * An XML document type.
 * Localname: ID
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.IDDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.IDDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.IDType;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

import javax.xml.namespace.QName;

/**
 * A document containing one ID(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class IDDocumentImpl extends XmlComplexContentImpl implements
  IDDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ID"),
  };
  private static final QNameSet[] PROPERTY_QSET = {
    QNameSet.forArray(new QName[]{
      new QName("urn:com.io7m.cardant.inventory:1", "ItemID"),
      new QName("urn:com.io7m.cardant.inventory:1", "ItemAttachmentID"),
      new QName("urn:com.io7m.cardant.inventory:1", "TagID"),
      new QName("urn:com.io7m.cardant.inventory:1", "LocationID"),
      new QName("urn:com.io7m.cardant.inventory:1", "ID"),
      new QName("urn:com.io7m.cardant.inventory:1", "UserID"),
    }),
  };

  public IDDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ID" element
   */
  @Override
  public IDType getID()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      IDType target = null;
      target = (IDType) this.get_store().find_element_user(
        PROPERTY_QSET[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ID" element
   */
  @Override
  public void setID(final IDType id)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      IDType target = null;
      target = (IDType) this.get_store().find_element_user(
        PROPERTY_QSET[0],
        0);
      if (target == null) {
        target = (IDType) this.get_store().add_element_user(
          PROPERTY_QNAME[0]);
      }
      target.set(id);
    }
  }

  /**
   * Appends and returns a new empty "ID" element
   */
  @Override
  public IDType addNewID()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      IDType target = null;
      target = (IDType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
