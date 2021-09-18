/*
 * XML Type:  TagsType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.TagsType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.TagType;
import com.io7m.cardant.protocol.inventory.v1.beans.TagsType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * An XML TagsType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class TagsTypeImpl extends XmlComplexContentImpl implements
  TagsType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "Tag"),
  };

  public TagsTypeImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets a List of "Tag" elements
   */
  @Override
  public List<TagType> getTagList()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      return new JavaListXmlObject<>(
        this::getTagArray,
        this::setTagArray,
        this::insertNewTag,
        this::removeTag,
        this::sizeOfTagArray
      );
    }
  }

  /**
   * Gets array of all "Tag" elements
   */
  @Override
  public TagType[] getTagArray()
  {
    return this.getXmlObjectArray(
      PROPERTY_QNAME[0],
      new TagType[0]);
  }

  /**
   * Sets array of all "Tag" element  WARNING: This method is not atomicaly synchronized.
   */
  @Override
  public void setTagArray(final TagType[] tagArray)
  {
    this.check_orphaned();
    this.arraySetterHelper(tagArray, PROPERTY_QNAME[0]);
  }

  /**
   * Gets ith "Tag" element
   */
  @Override
  public TagType getTagArray(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      TagType target = null;
      target = (TagType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        i);
      if (target == null) {
        throw new IndexOutOfBoundsException();
      }
      return target;
    }
  }

  /**
   * Returns number of "Tag" element
   */
  @Override
  public int sizeOfTagArray()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      return this.get_store().count_elements(PROPERTY_QNAME[0]);
    }
  }

  /**
   * Sets ith "Tag" element
   */
  @Override
  public void setTagArray(
    final int i,
    final TagType tag)
  {
    this.generatedSetterHelperImpl(
      tag,
      PROPERTY_QNAME[0],
      i,
      XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
  }

  /**
   * Inserts and returns a new empty value (as xml) as the ith "Tag" element
   */
  @Override
  public TagType insertNewTag(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      TagType target = null;
      target = (TagType) this.get_store().insert_element_user(
        PROPERTY_QNAME[0],
        i);
      return target;
    }
  }

  /**
   * Appends and returns a new empty value (as xml) as the last "Tag" element
   */
  @Override
  public TagType addNewTag()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      TagType target = null;
      target = (TagType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }

  /**
   * Removes the ith "Tag" element
   */
  @Override
  public void removeTag(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      this.get_store().remove_element(PROPERTY_QNAME[0], i);
    }
  }
}
