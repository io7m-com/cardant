/*
 * An XML document type.
 * Localname: ListLocationsBehaviour
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsBehaviourDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ListLocationsBehaviour(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ListLocationsBehaviourDocument extends XmlObject
{
  DocumentFactory<ListLocationsBehaviourDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "listlocationsbehaviour60eadoctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ListLocationsBehaviour" element
   */
  ListLocationsBehaviourType getListLocationsBehaviour();

  /**
   * Sets the "ListLocationsBehaviour" element
   */
  void setListLocationsBehaviour(ListLocationsBehaviourType listLocationsBehaviour);

  /**
   * Appends and returns a new empty "ListLocationsBehaviour" element
   */
  ListLocationsBehaviourType addNewListLocationsBehaviour();
}
