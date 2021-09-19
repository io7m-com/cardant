/*
 * An XML document type.
 * Localname: ListLocationsAll
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one ListLocationsAll(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ListLocationsAllDocumentImpl extends ListLocationsBehaviourDocumentImpl implements
  ListLocationsAllDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ListLocationsAll"),
  };

  public ListLocationsAllDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ListLocationsAll" element
   */
  @Override
  public ListLocationsAllType getListLocationsAll()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ListLocationsAllType target = null;
      target = (ListLocationsAllType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ListLocationsAll" element
   */
  @Override
  public void setListLocationsAll(final ListLocationsAllType listLocationsAll)
  {
    this.generatedSetterHelperImpl(
      listLocationsAll,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "ListLocationsAll" element
   */
  @Override
  public ListLocationsAllType addNewListLocationsAll()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ListLocationsAllType target = null;
      target = (ListLocationsAllType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
