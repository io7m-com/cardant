/*
 * An XML document type.
 * Localname: Tag
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.TagDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.TagDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.TagType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one Tag(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class TagDocumentImpl extends XmlComplexContentImpl implements
  TagDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "Tag"),
  };

  public TagDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "Tag" element
   */
  @Override
  public TagType getTag()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      TagType target = null;
      target = (TagType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "Tag" element
   */
  @Override
  public void setTag(final TagType tag)
  {
    this.generatedSetterHelperImpl(
      tag,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "Tag" element
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
}
