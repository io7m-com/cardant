/*
 * An XML document type.
 * Localname: ListLocationsBehaviour
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsBehaviourDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsBehaviourDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsBehaviourType;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

import javax.xml.namespace.QName;

/**
 * A document containing one ListLocationsBehaviour(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class ListLocationsBehaviourDocumentImpl extends XmlComplexContentImpl implements
  ListLocationsBehaviourDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "ListLocationsBehaviour"),
  };
  private static final QNameSet[] PROPERTY_QSET = {
    QNameSet.forArray(new QName[]{
      new QName("urn:com.io7m.cardant.inventory:1", "ListLocationsAll"),
      new QName(
        "urn:com.io7m.cardant.inventory:1",
        "ListLocationWithDescendants"),
      new QName("urn:com.io7m.cardant.inventory:1", "ListLocationsBehaviour"),
      new QName("urn:com.io7m.cardant.inventory:1", "ListLocationExact"),
    }),
  };

  public ListLocationsBehaviourDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "ListLocationsBehaviour" element
   */
  @Override
  public ListLocationsBehaviourType getListLocationsBehaviour()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ListLocationsBehaviourType target = null;
      target = (ListLocationsBehaviourType) this.get_store().find_element_user(
        PROPERTY_QSET[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "ListLocationsBehaviour" element
   */
  @Override
  public void setListLocationsBehaviour(final ListLocationsBehaviourType listLocationsBehaviour)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ListLocationsBehaviourType target = null;
      target = (ListLocationsBehaviourType) this.get_store().find_element_user(
        PROPERTY_QSET[0],
        0);
      if (target == null) {
        target = (ListLocationsBehaviourType) this.get_store().add_element_user(
          PROPERTY_QNAME[0]);
      }
      target.set(listLocationsBehaviour);
    }
  }

  /**
   * Appends and returns a new empty "ListLocationsBehaviour" element
   */
  @Override
  public ListLocationsBehaviourType addNewListLocationsBehaviour()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      ListLocationsBehaviourType target = null;
      target = (ListLocationsBehaviourType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
