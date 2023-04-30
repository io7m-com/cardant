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

import com.io7m.cardant.client.preferences.api.CAPreferencesServiceType;
import com.io7m.cardant.client.transfer.api.CATransferServiceType;
import com.io7m.cardant.gui.internal.model.CAItemMutable;
import com.io7m.cardant.gui.internal.model.CAMutableModelElementType;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType.CAFileWithData;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.protocol.inventory.CAICommandFilePut;
import com.io7m.cardant.protocol.inventory.CAICommandItemAttachmentAdd;
import com.io7m.cardant.protocol.inventory.CAICommandItemMetadataPut;
import com.io7m.jwheatsheaf.api.JWFileChooserAction;
import com.io7m.jwheatsheaf.api.JWFileChooserConfiguration;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

public final class CAViewControllerItemEditorOverviewTab
  implements Initializable
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAViewControllerItemEditorOverviewTab.class);

  private final CAMainStrings strings;
  private final Stage stage;
  private final CAMainController controller;
  private final CAFileDialogs fileDialogs;
  private final CAPreferencesServiceType preferences;
  private final CAExternalImages externalImages;
  private final CATransferServiceType transfers;
  private final CAMainClientService clientService;
  private final CAStatusServiceType statusService;

  @FXML private AnchorPane itemEditorPlaceholder;
  @FXML private Button itemDescriptionUpdate;
  @FXML private Button itemImageAdd;
  @FXML private Button itemImageRemove;
  @FXML private ImageView itemImage;
  @FXML private TextArea itemDescriptionField;
  @FXML private TextField itemCountField;
  @FXML private TextField itemIDField;
  @FXML private TextField itemNameField;
  @FXML private VBox itemEditorContainer;
  @FXML private Rectangle itemImageRectangle;
  @FXML private ProgressIndicator itemImageProgress;

  private Optional<CAItemMutable> itemCurrent;

  public CAViewControllerItemEditorOverviewTab(
    final RPServiceDirectoryType mainServices,
    final Stage inStage)
  {
    this.stage =
      Objects.requireNonNull(inStage, "stage");

    this.statusService =
      mainServices.requireService(CAStatusServiceType.class);
    this.strings =
      mainServices.requireService(CAMainStrings.class);
    this.controller =
      mainServices.requireService(CAMainController.class);
    this.fileDialogs =
      mainServices.requireService(CAFileDialogs.class);
    this.preferences =
      mainServices.requireService(CAPreferencesServiceType.class);
    this.externalImages =
      mainServices.requireService(CAExternalImages.class);
    this.transfers =
      mainServices.requireService(CATransferServiceType.class);
    this.clientService =
      mainServices.requireService(CAMainClientService.class);

    this.itemCurrent = Optional.empty();
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.controller.itemSelected()
      .addListener((observable, oldValue, newValue) -> {
        this.onItemSelected(newValue);
      });
  }

  @FXML
  private void onImageAddSelected()
    throws IOException
  {
    final var fileChooserConfiguration =
      JWFileChooserConfiguration.builder()
        .setAction(JWFileChooserAction.OPEN_EXISTING_SINGLE)
        .setCssStylesheet(CACSS.mainStylesheet())
        .addFileFilters(this.fileDialogs.filterForImages())
        .setFileSelectionMode(path -> Boolean.valueOf(Files.isRegularFile(path)))
        .setRecentFiles(this.preferences.preferences().recentFiles())
        .build();

    final var choosers =
      this.fileDialogs.choosers();
    final var chooser =
      choosers.create(this.stage, fileChooserConfiguration);
    final var files =
      chooser.showAndWait();

    if (files.isEmpty()) {
      return;
    }

    final var file =
      this.preferences.addRecentFile(files.get(0));

    try {
      final var imageData =
        this.externalImages.open(file);

      final var fileWithData =
        new CAFileWithData(
          CAFileID.random(),
          this.strings.format("item.image"),
          imageData.mediaType(),
          imageData.size(),
          imageData.hashAlgorithm(),
          imageData.hashValue(),
          imageData.data()
        );

      final var client = this.clientService.client();
      client.executeAsync(new CAICommandFilePut(fileWithData))
        .thenCompose(r -> {
          return client.executeAsync(new CAICommandItemAttachmentAdd(
            this.itemCurrent.get().id(),
            fileWithData.id(),
            "image"
          ));
        });

    } catch (final CAImageDataException e) {
      this.statusService.publish(
        new CAStatusEventType.CAStatusEventError(
          e.errorCode(),
          e.getMessage(),
          e.attributes(),
          e.remediatingAction(),
          e.exception()
        )
      );
    }
  }

  @FXML
  private void onImageRemoveSelected()
  {

  }

  @FXML
  private void onItemDescriptionUpdateSelected()
  {
    final var item = this.itemCurrent.get();
    this.clientService.client()
      .executeAsync(new CAICommandItemMetadataPut(
        item.id(),
        Set.of(
          new CAItemMetadata(
            "Description",
            this.itemDescriptionField.getText())
        ))
      );
  }

  @FXML
  private void onItemDescriptionEditChanged()
  {
    final var item = this.itemCurrent.get();
    this.itemDescriptionUpdate.setDisable(
      item.description()
        .getValue()
        .equals(this.itemDescriptionField.getText())
    );
  }

  private void onDataReceived(
    final CAMutableModelElementType data)
  {
    if (data instanceof CAItemMutable item) {
      final var itemIdIncoming =
        Optional.of(item.id());
      final var itemIdCurrent =
        this.itemCurrent.map(CAItemMutable::id);

      if (itemIdIncoming.equals(itemIdCurrent)) {
        this.onItemSelected(Optional.of(item));
      }
    }
  }

  private void onItemSelected(
    final Optional<CAItemMutable> itemOpt)
  {
    this.itemCurrent = itemOpt;
    if (itemOpt.isEmpty()) {
      return;
    }

    final var item = itemOpt.get();
    this.itemIDField.setText(
      item.id().id().toString());
    this.itemNameField.textProperty()
      .bind(item.name());
    this.itemCountField.textProperty()
      .bind(item.countTotal().asString());
    this.itemDescriptionField.textProperty()
      .bind(item.description());

    this.itemImageProgress.setVisible(true);

    final var imageAttachmentOpt =
      item.imageAttachment();
    if (imageAttachmentOpt.isPresent()) {
      final var imageAttachment =
        imageAttachmentOpt.get();

      final var attachmentFile =
        imageAttachment.file();

      final var title =
        this.strings.format(
          "transfer.attachment.image",
          attachmentFile.id().displayId());

      this.clientService.client()
        .fileDataAsyncOrElseThrow(attachmentFile.id())
        .thenCompose(stream -> {
          return this.transfers.transfer(
            stream,
            title,
            attachmentFile.size(),
            attachmentFile.hashAlgorithm(),
            attachmentFile.hashValue()
          );
        }).thenAcceptAsync(path -> {
          Platform.runLater(() -> {
            LOG.debug("loading received image");
            final var image =
              new Image(
                path.toUri().toString(),
                this.itemImageRectangle.getWidth(),
                this.itemImageRectangle.getHeight(),
                false,
                false
              );
            this.itemImage.setImage(image);
            this.itemImageProgress.setVisible(false);
          });
        });
    } else {
      this.itemImage.setImage(null);
      this.itemImageProgress.setVisible(false);
    }

    this.onItemDescriptionEditChanged();
  }
}
