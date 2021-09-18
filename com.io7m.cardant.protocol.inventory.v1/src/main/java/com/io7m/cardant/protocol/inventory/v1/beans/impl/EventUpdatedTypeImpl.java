/*
 * XML Type:  EventUpdatedType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.EventUpdatedType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.EventUpdatedType;
import com.io7m.cardant.protocol.inventory.v1.beans.RemovedType;
import com.io7m.cardant.protocol.inventory.v1.beans.UpdatedType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * An XML EventUpdatedType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is a complex type.
 */
public class EventUpdatedTypeImpl extends EventTypeImpl implements
  EventUpdatedType
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "Updated"),
    new QName("urn:com.io7m.cardant.inventory:1", "Removed"),
  };

  public EventUpdatedTypeImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "Updated" element
   */
  @Override
  public UpdatedType getUpdated()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      UpdatedType target = null;
      target = (UpdatedType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "Updated" element
   */
  @Override
  public void setUpdated(final UpdatedType updated)
  {
    this.generatedSetterHelperImpl(
      updated,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "Updated" element
   */
  @Override
  public UpdatedType addNewUpdated()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      UpdatedType target = null;
      target = (UpdatedType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }

  /**
   * Gets the "Removed" element
   */
  @Override
  public RemovedType getRemoved()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      RemovedType target = null;
      target = (RemovedType) this.get_store().find_element_user(
        PROPERTY_QNAME[1],
        0);
      return target;
    }
  }

  /**
   * Sets the "Removed" element
   */
  @Override
  public void setRemoved(final RemovedType removed)
  {
    this.generatedSetterHelperImpl(
      removed,
      PROPERTY_QNAME[1],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "Removed" element
   */
  @Override
  public RemovedType addNewRemoved()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      RemovedType target = null;
      target = (RemovedType) this.get_store().add_element_user(
        PROPERTY_QNAME[1]);
      return target;
    }
  }
}
