/*
 * An XML document type.
 * Localname: ItemMetadata
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one ItemMetadata(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ItemMetadataDocumentImpl extends XmlComplexContentImpl implements
  ItemMetadataDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ItemMetadata"),
  };

  public ItemMetadataDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ItemMetadata" element
   */
  @Override
  public ItemMetadataType getItemMetadata()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemMetadataType target = null;
      target = (ItemMetadataType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ItemMetadata" element
   */
  @Override
  public void setItemMetadata(final ItemMetadataType itemMetadata)
  {
    this.generatedSetterHelperImpl(
      itemMetadata,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "ItemMetadata" element
   */
  @Override
  public ItemMetadataType addNewItemMetadata()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ItemMetadataType target = null;
      target = (ItemMetadataType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
