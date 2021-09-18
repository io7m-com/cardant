/*
 * An XML document type.
 * Localname: TagID
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.TagIDDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.TagIDDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.TagIDType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one TagID(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class TagIDDocumentImpl extends IDDocumentImpl implements
  TagIDDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "TagID"),
  };

  public TagIDDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "TagID" element
   */
  @Override
  public TagIDType getTagID()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      TagIDType target = null;
      target = (TagIDType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "TagID" element
   */
  @Override
  public void setTagID(final TagIDType tagID)
  {
    this.generatedSetterHelperImpl(
      tagID,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "TagID" element
   */
  @Override
  public TagIDType addNewTagID()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      TagIDType target = null;
      target = (TagIDType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
