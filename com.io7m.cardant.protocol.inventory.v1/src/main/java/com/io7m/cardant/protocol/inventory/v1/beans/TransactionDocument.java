/*
 * An XML document type.
 * Localname: Transaction
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.TransactionDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import com.io7m.cardant.protocol.inventory.v1.beans.system.s224658FCFC90A14D91039032BDB551D0.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;


/**
 * A document containing one Transaction(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface TransactionDocument extends MessageDocument
{
  DocumentFactory<TransactionDocument> Factory = new DocumentFactory<>(
    TypeSystemHolder.typeSystem,
    "transaction823bdoctype");
  SchemaType type = Factory.getType();


  /**
   * Gets the "Transaction" element
   */
  TransactionType getTransaction();

  /**
   * Sets the "Transaction" element
   */
  void setTransaction(TransactionType transaction);

  /**
   * Appends and returns a new empty "Transaction" element
   */
  TransactionType addNewTransaction();
}
