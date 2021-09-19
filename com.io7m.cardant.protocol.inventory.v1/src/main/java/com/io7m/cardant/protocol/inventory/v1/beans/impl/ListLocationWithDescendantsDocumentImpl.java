/*
 * An XML document type.
 * Localname: ListLocationWithDescendants
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one ListLocationWithDescendants(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ListLocationWithDescendantsDocumentImpl extends ListLocationsBehaviourDocumentImpl implements
  ListLocationWithDescendantsDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName(
      "urn:com.io7m.cardant.inventory:1",
      "ListLocationWithDescendants"),
  };

  public ListLocationWithDescendantsDocumentImpl(final SchemaType sType)
  {
    super(sType);
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
        PROPERTY_QNAME[0],
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
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
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
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
