/*
 * XML Type:  CommandItemListType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.CommandItemListType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemListType;
import com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType;
import com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsType;
import com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * An XML CommandItemListType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class CommandItemListTypeImpl extends CommandTypeImpl implements
  CommandItemListType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ListLocationsAll"),
    new QName("urn:com.io7m.cardant.inventory:1", "ListLocationExact"),
    new QName(
      "urn:com.io7m.cardant.inventory:1",
      "ListLocationWithDescendants"),
  };

  public CommandItemListTypeImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ListLocationsAll" element
   */
  @Override
  public ListLocationsAllType getListLocationsAll()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ListLocationsAllType target = null;
      target = (ListLocationsAllType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ListLocationsAll" element
   */
  @Override
  public void setListLocationsAll(final ListLocationsAllType listLocationsAll)
  {
    this.generatedSetterHelperImpl(
      listLocationsAll,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * True if has "ListLocationsAll" element
   */
  @Override
  public boolean isSetListLocationsAll()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
    }
  }

  /**
   * Appends and returns a new empty "ListLocationsAll" element
   */
  @Override
  public ListLocationsAllType addNewListLocationsAll()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ListLocationsAllType target = null;
      target = (ListLocationsAllType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }

  /**
   * Unsets the "ListLocationsAll" element
   */
  @Override
  public void unsetListLocationsAll()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      this.get_store().remove_element(PROPERTY_QNAME[0], 0);
    }
  }

  /**
   * Gets the "ListLocationExact" element
   */
  @Override
  public ListLocationExactType getListLocationExact()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ListLocationExactType target = null;
      target = (ListLocationExactType) this.get_store().find_element_user(
        PROPERTY_QNAME[1],
        0);
      return target;
    }
  }

  /**
   * Sets the "ListLocationExact" element
   */
  @Override
  public void setListLocationExact(final ListLocationExactType listLocationExact)
  {
    this.generatedSetterHelperImpl(
      listLocationExact,
      PROPERTY_QNAME[1],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * True if has "ListLocationExact" element
   */
  @Override
  public boolean isSetListLocationExact()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
    }
  }

  /**
   * Appends and returns a new empty "ListLocationExact" element
   */
  @Override
  public ListLocationExactType addNewListLocationExact()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ListLocationExactType target = null;
      target = (ListLocationExactType) this.get_store().add_element_user(
        PROPERTY_QNAME[1]);
      return target;
    }
  }

  /**
   * Unsets the "ListLocationExact" element
   */
  @Override
  public void unsetListLocationExact()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      this.get_store().remove_element(PROPERTY_QNAME[1], 0);
    }
  }

  /**
   * Gets the "ListLocationWithDescendants" element
   */
  @Override
  public ListLocationWithDescendantsType getListLocationWithDescendants()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ListLocationWithDescendantsType target = null;
      target = (ListLocationWithDescendantsType) this.get_store().find_element_user(
        PROPERTY_QNAME[2],
        0);
      return target;
    }
  }

  /**
   * Sets the "ListLocationWithDescendants" element
   */
  @Override
  public void setListLocationWithDescendants(final ListLocationWithDescendantsType listLocationWithDescendants)
  {
    this.generatedSetterHelperImpl(
      listLocationWithDescendants,
      PROPERTY_QNAME[2],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * True if has "ListLocationWithDescendants" element
   */
  @Override
  public boolean isSetListLocationWithDescendants()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      return this.get_store().count_elements(PROPERTY_QNAME[2]) != 0;
    }
  }

  /**
   * Appends and returns a new empty "ListLocationWithDescendants" element
   */
  @Override
  public ListLocationWithDescendantsType addNewListLocationWithDescendants()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ListLocationWithDescendantsType target = null;
      target = (ListLocationWithDescendantsType) this.get_store().add_element_user(
        PROPERTY_QNAME[2]);
      return target;
    }
  }

  /**
   * Unsets the "ListLocationWithDescendants" element
   */
  @Override
  public void unsetListLocationWithDescendants()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      this.get_store().remove_element(PROPERTY_QNAME[2], 0);
    }
  }
}
