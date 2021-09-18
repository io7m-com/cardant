/*
 * An XML document type.
 * Localname: LocationID
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.LocationIDDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.LocationIDDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.LocationIDType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one LocationID(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class LocationIDDocumentImpl extends IDDocumentImpl implements
  LocationIDDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "LocationID"),
  };

  public LocationIDDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "LocationID" element
   */
  @Override
  public LocationIDType getLocationID()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      LocationIDType target = null;
      target = (LocationIDType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "LocationID" element
   */
  @Override
  public void setLocationID(final LocationIDType locationID)
  {
    this.generatedSetterHelperImpl(
      locationID,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "LocationID" element
   */
  @Override
  public LocationIDType addNewLocationID()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      LocationIDType target = null;
      target = (LocationIDType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
