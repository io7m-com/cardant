#!/bin/sh

rm -rfv com.io7m.cardant.protocol.inventory.v1/src/main/resources/com/io7m/cardant/protocol/inventory/v1/beans
rm -rfv com.io7m.cardant.protocol.inventory.v1/src/main/java/com/io7m/cardant/protocol/inventory/v1/beans

exec "$HOME/var/tmp/xmlbeans-5.0.1/bin/scomp" \
-verbose \
-repackage "org.apache.xmlbeans.metadata:com.io7m.cardant.protocol.inventory.v1.beans" \
-src com.io7m.cardant.protocol.inventory.v1/src/main/java \
-d com.io7m.cardant.protocol.inventory.v1/src/main/resources \
-srconly \
com.io7m.cardant.protocol.inventory.v1/src/main/resources/com/io7m/cardant/protocol/inventory/v1/inventory-1.xsdconfig \
com.io7m.cardant.protocol.inventory.v1/src/main/resources/com/io7m/cardant/protocol/inventory/v1/inventory-1.xsd
