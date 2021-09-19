/*
 * An XML document type.
 * Localname: ListLocationExact
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one ListLocationExact(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ListLocationExactDocument extends ListLocationsBehaviourDocument
{
  DocumentFactory<ListLocationExactDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "listlocationexact0fcddoctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "ListLocationExact" element
   */
  ListLocationExactType getListLocationExact();

  /**
   * Sets the "ListLocationExact" element
   */
  void setListLocationExact(ListLocationExactType listLocationExact);

  /**
   * Appends and returns a new empty "ListLocationExact" element
   */
  ListLocationExactType addNewListLocationExact();
}
