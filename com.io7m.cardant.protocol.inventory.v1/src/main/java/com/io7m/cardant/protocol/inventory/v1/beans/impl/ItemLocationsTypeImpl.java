/*
 * XML Type:  ItemLocationsType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationsType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationsType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * An XML ItemLocationsType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ItemLocationsTypeImpl extends XmlComplexContentImpl implements
  ItemLocationsType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ItemLocation"),
  };

  public ItemLocationsTypeImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets a List of "ItemLocation" elements
   */
  @Override
  public List<ItemLocationType> getItemLocationList()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      return new JavaListXmlObject<>(
        this::getItemLocationArray,
        this::setItemLocationArray,
        this::insertNewItemLocation,
        this::removeItemLocation,
        this::sizeOfItemLocationArray
      );
    }
  }

  /**
   * Gets array of all "ItemLocation" elements
   */
  @Override
  public ItemLocationType[] getItemLocationArray()
  {
    return this.getXmlObjectArray(
      PROPERTY_QNAME[0],
      new ItemLocationType[0]);
  }

  /**
   * Sets array of all "ItemLocation" element  WARNING: This method is not atomicaly synchronized.
   */
  @Override
  public void setItemLocationArray(final ItemLocationType[] itemLocationArray)
  {
    this.check_orphaned();
    this.arraySetterHelper(itemLocationArray, PROPERTY_QNAME[0]);
  }

  /**
   * Gets ith "ItemLocation" element
   */
  @Override
  public ItemLocationType getItemLocationArray(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemLocationType target = null;
      target = (ItemLocationType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        i);
      if (target == null) {
        throw new IndexOutOfBoundsException();
      }
      return target;
    }
  }

  /**
   * Returns number of "ItemLocation" element
   */
  @Override
  public int sizeOfItemLocationArray()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      return this.get_store().count_elements(PROPERTY_QNAME[0]);
    }
  }

  /**
   * Sets ith "ItemLocation" element
   */
  @Override
  public void setItemLocationArray(
    final int i,
    final ItemLocationType itemLocation)
  {
    this.generatedSetterHelperImpl(
      itemLocation,
      PROPERTY_QNAME[0],
      i,
      XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
  }

  /**
   * Inserts and returns a new empty value (as xml) as the ith "ItemLocation" element
   */
  @Override
  public ItemLocationType insertNewItemLocation(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemLocationType target = null;
      target = (ItemLocationType) this.get_store().insert_element_user(
        PROPERTY_QNAME[0],
        i);
      return target;
    }
  }

  /**
   * Appends and returns a new empty value (as xml) as the last "ItemLocation" element
   */
  @Override
  public ItemLocationType addNewItemLocation()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemLocationType target = null;
      target = (ItemLocationType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }

  /**
   * Removes the ith "ItemLocation" element
   */
  @Override
  public void removeItemLocation(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      this.get_store().remove_element(PROPERTY_QNAME[0], i);
    }
  }
}
