/*
 * XML Type:  ItemsType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemsType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ItemType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemsType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * An XML ItemsType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ItemsTypeImpl extends XmlComplexContentImpl implements
  ItemsType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "Item"),
  };

  public ItemsTypeImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets a List of "Item" elements
   */
  @Override
  public List<ItemType> getItemList()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      return new JavaListXmlObject<>(
        this::getItemArray,
        this::setItemArray,
        this::insertNewItem,
        this::removeItem,
        this::sizeOfItemArray
      );
    }
  }

  /**
   * Gets array of all "Item" elements
   */
  @Override
  public ItemType[] getItemArray()
  {
    return this.getXmlObjectArray(
      PROPERTY_QNAME[0],
      new ItemType[0]);
  }

  /**
   * Sets array of all "Item" element  WARNING: This method is not atomicaly synchronized.
   */
  @Override
  public void setItemArray(final ItemType[] itemArray)
  {
    this.check_orphaned();
    this.arraySetterHelper(itemArray, PROPERTY_QNAME[0]);
  }

  /**
   * Gets ith "Item" element
   */
  @Override
  public ItemType getItemArray(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemType target = null;
      target = (ItemType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        i);
      if (target == null) {
        throw new IndexOutOfBoundsException();
      }
      return target;
    }
  }

  /**
   * Returns number of "Item" element
   */
  @Override
  public int sizeOfItemArray()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      return this.get_store().count_elements(PROPERTY_QNAME[0]);
    }
  }

  /**
   * Sets ith "Item" element
   */
  @Override
  public void setItemArray(
    final int i,
    final ItemType item)
  {
    this.generatedSetterHelperImpl(
      item,
      PROPERTY_QNAME[0],
      i,
      XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
  }

  /**
   * Inserts and returns a new empty value (as xml) as the ith "Item" element
   */
  @Override
  public ItemType insertNewItem(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemType target = null;
      target = (ItemType) this.get_store().insert_element_user(
        PROPERTY_QNAME[0],
        i);
      return target;
    }
  }

  /**
   * Appends and returns a new empty value (as xml) as the last "Item" element
   */
  @Override
  public ItemType addNewItem()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemType target = null;
      target = (ItemType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }

  /**
   * Removes the ith "Item" element
   */
  @Override
  public void removeItem(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      this.get_store().remove_element(PROPERTY_QNAME[0], i);
    }
  }
}
