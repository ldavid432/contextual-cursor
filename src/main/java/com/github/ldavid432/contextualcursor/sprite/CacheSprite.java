package com.github.ldavid432.contextualcursor.sprite;

import java.awt.image.BufferedImage;
import net.runelite.api.Client;
import net.runelite.client.game.SpriteManager;

public class CacheSprite implements Sprite
{
	final int id;
	BufferedImage image;

	public CacheSprite(final int id)
	{
		this.id = id;
	}

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
}
