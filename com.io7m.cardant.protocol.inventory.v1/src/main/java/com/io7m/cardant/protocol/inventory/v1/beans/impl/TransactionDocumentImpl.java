/*
 * An XML document type.
 * Localname: Transaction
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.TransactionDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans.impl;

import com.io7m.cardant.protocol.inventory.v1.beans.TransactionDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.TransactionType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

import javax.xml.namespace.QName;

/**
 * A document containing one Transaction(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public class TransactionDocumentImpl extends MessageDocumentImpl implements
  TransactionDocument
{
  private static final long serialVersionUID = 1L;
  private static final QName[] PROPERTY_QNAME = {
    new QName("urn:com.io7m.cardant.inventory:1", "Transaction"),
  };

  public TransactionDocumentImpl(final SchemaType sType)
  {
    super(sType);
  }

  /**
   * Gets the "Transaction" element
   */
  @Override
  public TransactionType getTransaction()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      TransactionType target = null;
      target = (TransactionType) this.get_store().find_element_user(
        PROPERTY_QNAME[0],
        0);
      return target;
    }
  }

  /**
   * Sets the "Transaction" element
   */
  @Override
  public void setTransaction(final TransactionType transaction)
  {
    this.generatedSetterHelperImpl(
      transaction,
      PROPERTY_QNAME[0],
      0,
      XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
  }

  /**
   * Appends and returns a new empty "Transaction" element
   */
  @Override
  public TransactionType addNewTransaction()
  {
    synchronized (this.monitor()) {
      this.check_orphaned();
      TransactionType target = null;
      target = (TransactionType) this.get_store().add_element_user(
        PROPERTY_QNAME[0]);
      return target;
    }
  }
}
