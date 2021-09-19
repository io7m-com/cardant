/*
 * XML Type:  LocationDescriptionType
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.LocationDescriptionType
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlNormalizedString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML LocationDescriptionType(@urn:com.io7m.cardant.inventory:1).
 *
 * This is an atomic type that is a restriction of com.io7m.cardant.protocol.inventory.v1.beans.LocationDescriptionType.
 */
public interface LocationDescriptionType extends XmlNormalizedString
{
  SimpleTypeFactory<LocationDescriptionType> Factory = new SimpleTypeFactory<>(
    TypeSystemHolder.typeSystem,
    "locationdescriptiontypeb508type");
  SchemaType type = Factory.getType();

}
