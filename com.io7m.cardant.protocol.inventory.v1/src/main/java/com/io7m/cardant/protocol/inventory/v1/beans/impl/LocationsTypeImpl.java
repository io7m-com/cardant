/*
 * XML Type:  LocationsType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.LocationsType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.LocationType;
import com.io7m.cardant.protocol.inventory.v1.beans.LocationsType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * An XML LocationsType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class LocationsTypeImpl extends XmlComplexContentImpl implements
  LocationsType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "Location"),
  };

  public LocationsTypeImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets a List of "Location" elements
   */
  @Override
  public List<LocationType> getLocationList()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      return new JavaListXmlObject<>(
        this::getLocationArray,
        this::setLocationArray,
        this::insertNewLocation,
        this::removeLocation,
        this::sizeOfLocationArray
      );
    }
  }

  /**
   * Gets array of all "Location" elements
   */
  @Override
  public LocationType[] getLocationArray()
  {
    return this.getXmlObjectArray(
      PROPERTY_QNAME[0],
      new LocationType[0]);
  }

  /**
   * Sets array of all "Location" element  WARNING: This method is not atomicaly synchronized.
   */
  @Override
  public void setLocationArray(final LocationType[] locationArray)
  {
    this.check_orphaned();
    this.arraySetterHelper(locationArray, PROPERTY_QNAME[0]);
  }

  /**
   * Gets ith "Location" element
   */
  @Override
  public LocationType getLocationArray(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      LocationType target = null;
      target = (LocationType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        i);
      if (target == null) {
        throw new IndexOutOfBoundsException();
      }
      return target;
    }
  }

  /**
   * Returns number of "Location" element
   */
  @Override
  public int sizeOfLocationArray()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      return this.get_store().count_elements(PROPERTY_QNAME[0]);
    }
  }

  /**
   * Sets ith "Location" element
   */
  @Override
  public void setLocationArray(
    final int i,
    final LocationType location)
  {
    this.generatedSetterHelperImpl(
      location,
      PROPERTY_QNAME[0],
      i,
      XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
  }

  /**
   * Inserts and returns a new empty value (as xml) as the ith "Location" element
   */
  @Override
  public LocationType insertNewLocation(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      LocationType target = null;
      target = (LocationType) this.get_store().insert_element_user(
        PROPERTY_QNAME[0],
        i);
      return target;
    }
  }

  /**
   * Appends and returns a new empty value (as xml) as the last "Location" element
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

  /**
   * Removes the ith "Location" element
   */
  @Override
  public void removeLocation(final int i)
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      this.get_store().remove_element(PROPERTY_QNAME[0], i);
    }
  }
}
