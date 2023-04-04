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

import javafx.scene.image.Image;

public final class CAIcons implements CAIconsType
{
  private final Image error;
  private final Image info;
  private final Image inProgress;
  private final Image ok;
  private final Image cardant;
  private final Image downloadOk;
  private final Image downloadInProgress;
  private final Image downloadError;

  public CAIcons()
  {
    this.error =
      loadImage("data-error.png");
    this.info =
      loadImage("data-information.png");
    this.ok =
      loadImage("answer.png");
    this.inProgress =
      loadImage("clock.png");
    this.cardant =
      loadImage("cardant.png");
    this.downloadOk =
      loadImage("download-succeeded.png");
    this.downloadError =
      loadImage("error32.png");
    this.downloadInProgress =
      loadImage("download.png");
  }

  private static Image loadImage(
    final String name)
  {
    return new Image(
      CAIcons.class.getResource("/com/io7m/cardant/gui/internal/" + name)
        .toString()
    );
  }

  @Override
  public Image downloadOk()
  {
    return this.downloadOk;
  }

  @Override
  public Image downloadError()
  {
    return this.downloadError;
  }

  @Override
  public Image downloadInProgress()
  {
    return this.downloadInProgress;
  }

  @Override
  public Image info()
  {
    return this.info;
  }

  @Override
  public Image inProgress()
  {
    return this.inProgress;
  }

  @Override
  public Image ok()
  {
    return this.ok;
  }

  @Override
  public Image cardant()
  {
    return this.cardant;
  }

  @Override
  public Image error()
  {
    return this.error;
  }

  @Override
  public String toString()
  {
    return String.format("[CAIcons 0x%08x]", Integer.valueOf(this.hashCode()));
  }

  @Override
  public String description()
  {
    return "Icon service";
  }
}
