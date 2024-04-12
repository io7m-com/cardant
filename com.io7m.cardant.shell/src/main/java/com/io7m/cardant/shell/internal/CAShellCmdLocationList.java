/*
 * Copyright © 2023 Mark Raynsford <code@io7m.com> https://www.io7m.com
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


package com.io7m.cardant.shell.internal;

import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.protocol.inventory.CAICommandLocationList;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationList;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import hu.webarticum.treeprinter.SimpleTreeNode;
import hu.webarticum.treeprinter.printer.listing.ListingTreePrinter;

import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "location-list"
 */

public final class CAShellCmdLocationList
  extends CAShellCmdAbstractCR<CAICommandLocationList, CAIResponseLocationList>
{
  /**
   * Construct a command.
   *
   * @param inServices The context
   */

  public CAShellCmdLocationList(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "location-list",
        new QConstant("List locations."),
        Optional.empty()
      ),
      CAICommandLocationList.class
    );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of();
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    final var client =
      this.client();

    final var locations =
      client.sendAndWaitOrThrow(
        new CAICommandLocationList(),
        this.commandTimeout()
      ).data();

    /*
     * Collect all the locations into a map. Create nodes for each
     * but don't create any edges between nodes.
     */

    final var nodes = new TreeMap<CALocationID, SimpleTreeNode>();
    for (final var entry : locations.locations().entrySet()) {
      final var id =
        entry.getKey();
      final var location =
        entry.getValue();
      final var node =
        new SimpleTreeNode(
          String.format("[%s] %s", id.displayId(), location.name())
        );
      nodes.put(id, node);
    }

    /*
     * Now all the nodes are present in the map, iterate over the locations
     * again and add parent -> child edges.
     */

    for (final var entry : locations.locations().entrySet()) {
      final var id =
        entry.getKey();
      final var location =
        entry.getValue();
      final var node =
        nodes.get(id);

      location.parent()
        .flatMap(x -> Optional.ofNullable(nodes.get(x)))
        .ifPresent(parent -> parent.addChild(node));
    }

    /*
     * Now print all the root nodes. The tree printer handles printing
     * the child elements.
     */

    final var printer = new ListingTreePrinter();
    for (final var entry : locations.locations().entrySet()) {
      final var id =
        entry.getKey();
      final var location =
        entry.getValue();

      if (location.parent().isEmpty()) {
        final var node = nodes.get(id);
        printer.print(node, context.output());
      }
    }

    return SUCCESS;
  }
}
