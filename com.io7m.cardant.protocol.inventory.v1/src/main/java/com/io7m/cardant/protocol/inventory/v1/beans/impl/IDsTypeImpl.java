/*
 * XML Type:  IDsType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.IDsType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.IDType;
import com.io7m.cardant.protocol.inventory.v1.beans.IDsType;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * An XML IDsType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class IDsTypeImpl extends XmlComplexContentImpl implements
  IDsType
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

  public IDsTypeImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets a List of "ID" elements
   */
  @Override
  public List<IDType> getIDList()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      return new JavaListXmlObject<>(
        this::getIDArray,
        this::setIDArray,
        this::insertNewID,
        this::removeID,
        this::sizeOfIDArray
      );
    }
  }

  /**
   * Gets array of all "ID" elements
   */
  @Override
  public IDType[] getIDArray()
  {
    return this.getXmlObjectArray(
      PROPERTY_QSET[0],
      new IDType[0]);
  }

  /**
   * Sets array of all "ID" element  WARNING: This method is not atomicaly synchronized.
   */
  @Override
  public void setIDArray(final IDType[] idArray)
  {
    this.check_orphaned();
    this.arraySetterHelper(idArray, PROPERTY_QNAME[0], PROPERTY_QSET[0]);
  }

  /**
   * Gets ith "ID" element
   */
  @Override
  public IDType getIDArray(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      IDType target = null;
      target = (IDType) this.get_store().find_element_user(
        PROPERTY_QSET[0],
        i);
      if (target == null) {
        throw new IndexOutOfBoundsException();
      }
      return target;
    }
  }

  /**
   * Returns number of "ID" element
   */
  @Override
  public int sizeOfIDArray()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      return this.get_store().count_elements(PROPERTY_QSET[0]);
    }
  }

  /**
   * Sets ith "ID" element
   */
  @Override
  public void setIDArray(
    final int i,
    final IDType id)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      IDType target = null;
      target = (IDType) this.get_store().find_element_user(
        PROPERTY_QSET[0],
        i);
      if (target == null) {
        throw new IndexOutOfBoundsException();
      }
      target.set(id);
    }
  }

  /**
   * Inserts and returns a new empty value (as xml) as the ith "ID" element
   */
  @Override
  public IDType insertNewID(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      IDType target = null;
      target = (IDType) this.get_store().insert_element_user(
        PROPERTY_QSET[0],
        PROPERTY_QNAME[0],
        i);
      return target;
    }
  }

  /**
   * Appends and returns a new empty value (as xml) as the last "ID" element
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

  /**
   * Removes the ith "ID" element
   */
  @Override
  public void removeID(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      this.get_store().remove_element(PROPERTY_QSET[0], i);
    }
  }
}
