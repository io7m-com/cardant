/*
 * Copyright © 2021 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

/**
 * Inventory system (Inventory protocol API)
 */

module com.io7m.cardant.protocol.inventory.v1
{
  requires static org.osgi.annotation.bundle;
  requires static org.osgi.annotation.versioning;

  requires transitive com.io7m.cardant.protocol.inventory.api;

  requires org.apache.xmlbeans;
  requires com.io7m.anethum.common;
  requires com.io7m.junreachable.core;
  requires java.xml;

  exports com.io7m.cardant.protocol.inventory.v1;

  opens com.io7m.cardant.protocol.inventory.v1
    to org.apache.xmlbeans;
  opens com.io7m.cardant.protocol.inventory.v1.beans
    to org.apache.xmlbeans;
  opens com.io7m.cardant.protocol.inventory.v1.beans.src
    to org.apache.xmlbeans;
  opens com.io7m.cardant.protocol.inventory.v1.beans.element.urn_3Acom_2Eio7m_2Ecardant_2Einventory_3A1
    to org.apache.xmlbeans;
  opens com.io7m.cardant.protocol.inventory.v1.beans.namespace.urn_3Acom_2Eio7m_2Ecardant_2Einventory_3A1
    to org.apache.xmlbeans;
  opens com.io7m.cardant.protocol.inventory.v1.beans.javaname.com.io7m.cardant.protocol.inventory.v1.beans
    to org.apache.xmlbeans;
  opens com.io7m.cardant.protocol.inventory.v1.beans.modelgroup.urn_3Acom_2Eio7m_2Ecardant_2Einventory_3A1
    to org.apache.xmlbeans;
  opens com.io7m.cardant.protocol.inventory.v1.beans.type.urn_3Acom_2Eio7m_2Ecardant_2Einventory_3A1
    to org.apache.xmlbeans;
  opens com.io7m.cardant.protocol.inventory.v1.beans.system.sFD186D0BF9A55EE36362F4FDE124660F
    to org.apache.xmlbeans;
  opens com.io7m.cardant.protocol.inventory.v1.beans.system.s27AB10090C918EEACB2D4A033D15C959
    to org.apache.xmlbeans;
  opens com.io7m.cardant.protocol.inventory.v1.beans.system.sBC9867C097A883CC3E864FCA81C088B0
    to org.apache.xmlbeans;
  opens com.io7m.cardant.protocol.inventory.v1.beans.system.sB0BC2A7BC37AA09D3BE386EF1E3B543B
    to org.apache.xmlbeans;
  opens com.io7m.cardant.protocol.inventory.v1.beans.system.s0C13A2C3A638DC6D00771826D2657063
    to org.apache.xmlbeans;
  opens com.io7m.cardant.protocol.inventory.v1.beans.system.s9C26CFF39558CB8BECF75E0F80C66444
    to org.apache.xmlbeans;
  opens com.io7m.cardant.protocol.inventory.v1.beans.system.s2F5B3CB3EEF95D40ACF30F098DD12ED2
    to org.apache.xmlbeans;
  opens com.io7m.cardant.protocol.inventory.v1.beans.system.s103E887B75EEC3A0894B458BCBC362DF
    to org.apache.xmlbeans;
  opens com.io7m.cardant.protocol.inventory.v1.beans.system.s81891A43ECABFF6DB78F1B680023A79F
    to org.apache.xmlbeans;
  opens com.io7m.cardant.protocol.inventory.v1.beans.system.s4057B33BB56BCBD4DA597AE05493F93F
    to org.apache.xmlbeans;
  opens com.io7m.cardant.protocol.inventory.v1.beans.system.s0A605F74A6337FDB2B7C345B9EC823CC
    to org.apache.xmlbeans;
  opens com.io7m.cardant.protocol.inventory.v1.beans.system.sB1C06B73AB6C2532BEBB863A36AD7B97
    to org.apache.xmlbeans;
}