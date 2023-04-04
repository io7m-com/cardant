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

package com.io7m.cardant.gui.internal;

import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public final class CAViewControllers
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAViewControllers.class);

  private CAViewControllers()
  {

  }

  public static Object createController(
    final Class<?> clazz,
    final Stage stage,
    final RPServiceDirectoryType mainServices)
  {
    LOG.debug("createController: {}", clazz);

    if (Objects.equals(clazz, CAViewControllerMain.class)) {
      return new CAViewControllerMain(mainServices, stage);
    }
    if (Objects.equals(clazz, CAViewControllerItemsTab.class)) {
      return new CAViewControllerItemsTab(mainServices, stage);
    }
    if (Objects.equals(clazz, CAViewControllerLocationsTab.class)) {
      return new CAViewControllerLocationsTab(mainServices, stage);
    }
    if (Objects.equals(clazz, CAViewControllerTransfersTab.class)) {
      return new CAViewControllerTransfersTab(mainServices, stage);
    }
    if (Objects.equals(clazz, CAViewControllerDebuggingTab.class)) {
      return new CAViewControllerDebuggingTab(mainServices, stage);
    }
    if (Objects.equals(clazz, CAViewControllerConnect.class)) {
      return new CAViewControllerConnect(mainServices, stage);
    }
    if (Objects.equals(clazz, CAViewControllerCreateItem.class)) {
      return new CAViewControllerCreateItem(mainServices, stage);
    }
    if (Objects.equals(clazz, CAViewControllerLocationCreate.class)) {
      return new CAViewControllerLocationCreate(mainServices, stage);
    }
    if (Objects.equals(clazz, CAViewControllerLocationSetParent.class)) {
      return new CAViewControllerLocationSetParent(mainServices, stage);
    }
    if (Objects.equals(clazz, CAViewControllerItemEditor.class)) {
      return new CAViewControllerItemEditor(mainServices, stage);
    }
    if (Objects.equals(clazz, CAViewControllerItemEditorAttachmentsTab.class)) {
      return new CAViewControllerItemEditorAttachmentsTab(mainServices, stage);
    }
    if (Objects.equals(clazz, CAViewControllerItemEditorMetadataTab.class)) {
      return new CAViewControllerItemEditorMetadataTab(mainServices, stage);
    }
    if (Objects.equals(clazz, CAViewControllerItemEditorOverviewTab.class)) {
      return new CAViewControllerItemEditorOverviewTab(mainServices, stage);
    }
    if (Objects.equals(clazz, CAViewControllerItemMetadataEditor.class)) {
      return new CAViewControllerItemMetadataEditor(mainServices, stage);
    }
    if (Objects.equals(clazz, CAViewControllerError.class)) {
      return new CAViewControllerError(mainServices, stage);
    }
    if (Objects.equals(clazz, CAViewControllerItemEditorLocationsTab.class)) {
      return new CAViewControllerItemEditorLocationsTab(mainServices, stage);
    }

    throw new IllegalStateException(
      String.format("Unrecognized class: %s", clazz)
    );
  }
}
