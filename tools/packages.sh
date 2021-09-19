#!/bin/sh

pushd com.io7m.cardant.protocol.inventory.v1
pushd src/main/resources

for DIRECTORY in $(find com/io7m/cardant/protocol/inventory/v1 -type d)
do
  echo ${DIRECTORY} | sed 's:/:.:g; s/^/opens /g; s/$/ to org.apache.xmlbeans;/g;'
done
