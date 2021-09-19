/*
 * XML Type:  ResponseLocationPutType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationPutType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.LocationType;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationPutType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * An XML ResponseLocationPutType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class ResponseLocationPutTypeImpl extends ResponseTypeImpl implements
  ResponseLocationPutType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "Location"),
  };

  public ResponseLocationPutTypeImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "Location" element
   */
  @Override
  public LocationType getLocation()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      LocationType target = null;
      target = (LocationType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "Location" element
   */
  @Override
  public void setLocation(final LocationType location)
  {
    this.generatedSetterHelperImpl(
      location,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "Location" element
   */
  @Override
  public LocationType addNewLocation()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      LocationType target = null;
      target = (LocationType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
