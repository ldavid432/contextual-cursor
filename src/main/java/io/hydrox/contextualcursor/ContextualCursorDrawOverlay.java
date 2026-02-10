/*
 * Copyright (c) 2022 Enriath <ikada@protonmail.ch>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package io.hydrox.contextualcursor;

import com.github.ldavid432.contextualcursor.ContextualCursorConfig;
import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.scaleImage;
import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.scalePoint;
import com.github.ldavid432.contextualcursor.config.CursorTheme;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import static io.hydrox.contextualcursor.ContextualCursor.BLANK_CURSOR;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

public class ContextualCursorDrawOverlay extends Overlay
{
	//The pointer sticks out to the left slightly, so this makes sure it's point to the correct spot
	private static final Point POINTER_OFFSET = new Point(-5, 0);
	private static final Point POINTER_OFFSET2 = new Point(-3, 0);
	//The centre of the circle (biased bottom right since it's an even size), for use with sprites
	private static final Point CENTRAL_POINT = new Point(16, 18);

	private final Client client;
	private final ContextualCursorPlugin plugin;
	private final SpriteManager spriteManager;

	private Point scaledCenterPoint = CENTRAL_POINT;
	private Point scaledOffset = POINTER_OFFSET;
	private Point scaledOffset2 = POINTER_OFFSET2;
	private BufferedImage blankCursor;
	private BufferedImage scaledBlankCursor;
	private Sprite currentSprite;
	private BufferedImage currentScaledSprite;

	@Inject
	ContextualCursorDrawOverlay(Client client, ContextualCursorPlugin plugin, SpriteManager spriteManager, ContextualCursorConfig config)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
		setPriority(1f);
		this.client = client;
		this.plugin = plugin;
		this.spriteManager = spriteManager;
		setBlankCursor(config.getCursorTheme());
	}

	public void setBlankCursor(CursorTheme theme)
	{
		blankCursor = BLANK_CURSOR.getImage(theme);
		scaledBlankCursor = null;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		Sprite sprite = plugin.getSpriteToDraw();

		if (sprite == null)
		{
			return null;
		}

		BufferedImage image = sprite.getImage(client, spriteManager, plugin.getCursorTheme());
		if (image == null)
		{
			return null;
		}

		if (sprite != currentSprite || currentScaledSprite == null)
		{
			currentSprite = sprite;
			currentScaledSprite = scaleImage(image, plugin);
		}

		if (currentScaledSprite == null)
		{
			return null;
		}

		final Point mousePos = client.getMouseCanvasPosition();
		if (sprite.isFullCursor())
		{
			graphics.drawImage(currentScaledSprite, mousePos.getX() + scaledOffset2.getX(), mousePos.getY() + scaledOffset2.getY(), null);
		}
		else
		{
			graphics.drawImage(scaledBlankCursor, mousePos.getX() + scaledOffset.getX(), mousePos.getY() + scaledOffset.getY(), null);
			final int spriteX = scaledOffset.getX() + scaledCenterPoint.getX() - currentScaledSprite.getWidth(null) / 2;
			final int spriteY = scaledOffset.getY() + scaledCenterPoint.getY() - currentScaledSprite.getHeight(null) / 2;
			graphics.drawImage(currentScaledSprite, mousePos.getX() + spriteX, mousePos.getY() + spriteY, null);
		}

		return null;
	}

	public void updateScale()
	{
		scaledCenterPoint = scalePoint(CENTRAL_POINT, plugin.getCursorScale());
		scaledOffset = scalePoint(POINTER_OFFSET, plugin.getCursorScale());
		scaledOffset2 = scalePoint(POINTER_OFFSET2, plugin.getCursorScale());
		rerenderImages();
	}

	public void rerenderImages()
	{
		blankCursor = BLANK_CURSOR.getImage(plugin.getCursorTheme());
		if (blankCursor != null)
		{
			scaledBlankCursor = scaleImage(blankCursor, plugin);
		}
		currentScaledSprite = null;
	}
}
