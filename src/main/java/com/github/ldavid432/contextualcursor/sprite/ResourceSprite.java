package com.github.ldavid432.contextualcursor.sprite;

import io.hydrox.contextualcursor.ContextualCursorPlugin;
import java.awt.image.BufferedImage;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.util.ImageUtil;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class ResourceSprite implements Sprite
{
	@Nonnull
	String path;
	@Nullable
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

	@Override
	public void clearImage()
	{
		image = null;
	}

	public static BufferedImage loadImage(String path)
	{
		return ImageUtil.loadImageResource(ContextualCursorPlugin.class, String.format("cursors/%s.png", path));
	}
}
