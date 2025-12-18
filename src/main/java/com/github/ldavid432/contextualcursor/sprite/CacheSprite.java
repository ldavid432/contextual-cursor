package com.github.ldavid432.contextualcursor.sprite;

import java.awt.image.BufferedImage;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.client.game.SpriteManager;

@RequiredArgsConstructor
public class CacheSprite implements Sprite
{
	private final int id;
	@Nullable
	BufferedImage image;

	@Override
	public BufferedImage getImage(final Client client, final SpriteManager spriteManager)
	{
		if (image == null)
		{
			if (client.getSpriteOverrides().containsKey(id))
			{
				image = client.getSpriteOverrides().get(id).toBufferedImage();
			}
			else
			{
				image = spriteManager.getSprite(id, 0);
			}
		}

		return image;
	}

	@Override
	public void clearImage()
	{
		image = null;
	}
}
