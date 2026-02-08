package com.github.ldavid432.contextualcursor.sprite;

import io.hydrox.contextualcursor.ContextualCursorPlugin;
import java.awt.image.BufferedImage;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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

	@Nullable
	private BufferedImage imageOSRS;

	@Getter
	private final boolean isFullCursor;

	@Getter
	@Setter
	private Boolean isOsrSkin = false;

	public ResourceSprite(@Nonnull String fileName)
	{
		this.fileName = fileName;
		this.isFullCursor = false;
	}

	@Nullable
	@Override
	public BufferedImage getImage(Client client, SpriteManager spriteManager, boolean isOsrSkin)
	{
		return getImage(isOsrSkin);
	}

	@Nullable
	public BufferedImage getImage(boolean isOsrSkin)
	{
		if (isOsrSkin)
		{
			if (imageOSRS == null)
			{
				String osrsPath = String.format("cursors/%s_osrs.png", fileName);
				if (resourceExists(osrsPath))
				{
					imageOSRS = ImageUtil.loadImageResource(ContextualCursorPlugin.class, osrsPath);
				}
			}

			if (imageOSRS != null)
			{
				return imageOSRS;
			}
		}

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

	private static boolean resourceExists(String resourcePath)
	{
		return ContextualCursorPlugin.class.getResourceAsStream(resourcePath) != null;
	}

	public static BufferedImage loadImage(String fileName)
	{
		return ImageUtil.loadImageResource(ContextualCursorPlugin.class, String.format("cursors/%s.png", fileName));
	}
}
