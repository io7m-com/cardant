/*
 * An XML document type.
 * Localname: ItemMetadatas
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one ItemMetadatas(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ItemMetadatasDocumentImpl extends XmlComplexContentImpl implements
  ItemMetadatasDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ItemMetadatas"),
  };

  public ItemMetadatasDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ItemMetadatas" element
   */
  @Override
  public ItemMetadatasType getItemMetadatas()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemMetadatasType target = null;
      target = (ItemMetadatasType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ItemMetadatas" element
   */
  @Override
  public void setItemMetadatas(final ItemMetadatasType itemMetadatas)
  {
    this.generatedSetterHelperImpl(
      itemMetadatas,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "ItemMetadatas" element
   */
  @Override
  public ItemMetadatasType addNewItemMetadatas()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemMetadatasType target = null;
      target = (ItemMetadatasType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
