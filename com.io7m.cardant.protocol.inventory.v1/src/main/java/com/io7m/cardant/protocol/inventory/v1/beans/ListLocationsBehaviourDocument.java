/*
 * An XML document type.
 * Localname: ListLocationsBehaviour
 * Namespace: urn:com.io7m.cardant.inventory:1
 * Java type: com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsBehaviourDocument
 *
 * Automatically generated - do not modify.
 */
package com.io7m.cardant.protocol.inventory.v1.beans;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one ListLocationsBehaviour(@urn:com.io7m.cardant.inventory:1) element.
 *
 * This is a complex type.
 */
public interface ListLocationsBehaviourDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsBehaviourDocument> Factory = new DocumentFactory<>(com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F.TypeSystemHolder.typeSystem, "listlocationsbehaviour60eadoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "ListLocationsBehaviour" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsBehaviourType getListLocationsBehaviour();

    /**
     * Sets the "ListLocationsBehaviour" element
     */
    void setListLocationsBehaviour(com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsBehaviourType listLocationsBehaviour);

    /**
     * Appends and returns a new empty "ListLocationsBehaviour" element
     */
    com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsBehaviourType addNewListLocationsBehaviour();
}
