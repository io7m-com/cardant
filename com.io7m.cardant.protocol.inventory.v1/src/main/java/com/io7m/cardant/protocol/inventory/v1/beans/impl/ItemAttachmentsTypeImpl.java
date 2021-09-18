/*
 * XML Type:  ItemAttachmentsType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * An XML ItemAttachmentsType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ItemAttachmentsTypeImpl extends XmlComplexContentImpl implements
  ItemAttachmentsType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ItemAttachment"),
  };

  public ItemAttachmentsTypeImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets a List of "ItemAttachment" elements
   */
  @Override
  public List<ItemAttachmentType> getItemAttachmentList()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      return new JavaListXmlObject<>(
        this::getItemAttachmentArray,
        this::setItemAttachmentArray,
        this::insertNewItemAttachment,
        this::removeItemAttachment,
        this::sizeOfItemAttachmentArray
      );
    }
  }

  /**
   * Gets array of all "ItemAttachment" elements
   */
  @Override
  public ItemAttachmentType[] getItemAttachmentArray()
  {
    return this.getXmlObjectArray(
      PROPERTY_QNAME[0],
      new ItemAttachmentType[0]);
  }

  /**
   * Sets array of all "ItemAttachment" element  WARNING: This method is not atomicaly synchronized.
   */
  @Override
  public void setItemAttachmentArray(final ItemAttachmentType[] itemAttachmentArray)
  {
    this.check_orphaned();
    this.arraySetterHelper(itemAttachmentArray, PROPERTY_QNAME[0]);
  }

  /**
   * Gets ith "ItemAttachment" element
   */
  @Override
  public ItemAttachmentType getItemAttachmentArray(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemAttachmentType target = null;
      target = (ItemAttachmentType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        i);
      if (target == null) {
        throw new IndexOutOfBoundsException();
      }
      return target;
    }
  }

  /**
   * Returns number of "ItemAttachment" element
   */
  @Override
  public int sizeOfItemAttachmentArray()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      return this.get_store().count_elements(PROPERTY_QNAME[0]);
    }
  }

  /**
   * Sets ith "ItemAttachment" element
   */
  @Override
  public void setItemAttachmentArray(
    final int i,
    final ItemAttachmentType itemAttachment)
  {
    this.generatedSetterHelperImpl(
      itemAttachment,
      PROPERTY_QNAME[0],
      i,
      XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
  }

  /**
   * Inserts and returns a new empty value (as xml) as the ith "ItemAttachment" element
   */
  @Override
  public ItemAttachmentType insertNewItemAttachment(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemAttachmentType target = null;
      target = (ItemAttachmentType) this.get_store().insert_element_user(
        PROPERTY_QNAME[0],
        i);
      return target;
    }
  }

  /**
   * Appends and returns a new empty value (as xml) as the last "ItemAttachment" element
   */
  @Override
  public ItemAttachmentType addNewItemAttachment()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemAttachmentType target = null;
      target = (ItemAttachmentType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }

  /**
   * Removes the ith "ItemAttachment" element
   */
  @Override
  public void removeItemAttachment(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      this.get_store().remove_element(PROPERTY_QNAME[0], i);
    }
  }
}
