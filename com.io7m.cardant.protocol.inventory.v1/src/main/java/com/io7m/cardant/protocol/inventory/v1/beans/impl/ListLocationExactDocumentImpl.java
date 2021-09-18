/*
 * An XML document type.
 * Localname: ListLocationExact
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one ListLocationExact(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ListLocationExactDocumentImpl extends ListLocationsBehaviourDocumentImpl implements
  ListLocationExactDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ListLocationExact"),
  };

  public ListLocationExactDocumentImpl(final SchemaType sType)
  {
    super(sType);
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
        PROPERTY_QNAME[0],
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
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
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
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
