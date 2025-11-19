package com.github.ldavid432.contextualcursor.sprite;

import io.hydrox.contextualcursor.ContextualCursorPlugin;
import java.awt.image.BufferedImage;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.util.ImageUtil;

@RequiredArgsConstructor
public class ResourceSprite implements Sprite
{
	@Nonnull
	String path;
	BufferedImage image;

	@Nullable
	@Override
	public BufferedImage getImage(Client client, SpriteManager spriteManager)
	{
		if (image == null)
		{
			image = loadImage(path);
		}
		return image;
	}

	public static BufferedImage loadImage(String path)
	{
		return ImageUtil.loadImageResource(ContextualCursorPlugin.class, String.format("cursors/%s.png", path));
	}
}
