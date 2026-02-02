package com.github.ldavid432.contextualcursor.sprite;

import io.hydrox.contextualcursor.ContextualCursorPlugin;
import java.awt.image.BufferedImage;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.util.ImageUtil;

@EqualsAndHashCode(of = "fileName")
@RequiredArgsConstructor
public class ResourceSprite implements Sprite
{
	@Nonnull
	private final String fileName;

	@Nullable
	private BufferedImage image;

	@Getter
	private final boolean isFullCursor;

	ResourceSprite(@Nonnull String fileName)
	{
		this.fileName = fileName;
		this.isFullCursor = false;
	}

	@Nullable
	@Override
	public BufferedImage getImage(Client client, SpriteManager spriteManager)
	{
		return getImage();
	}

	@Nullable
	public BufferedImage getImage()
	{
		if (image == null)
		{
			image = loadImage(fileName);
		}
		return image;
	}

	@Override
	public void clearImage()
	{
		image = null;
	}

	public static BufferedImage loadImage(String fileName)
	{
		return ImageUtil.loadImageResource(ContextualCursorPlugin.class, String.format("cursors/%s.png", fileName));
	}
}
