package com.github.ldavid432.contextualcursor.sprite;

import com.github.ldavid432.contextualcursor.config.CursorTheme;
import java.awt.image.BufferedImage;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import net.runelite.api.Client;
import net.runelite.client.game.SpriteManager;

@SuperBuilder
@EqualsAndHashCode(of = "id", callSuper = false)
public class CacheSprite extends BaseSprite
{

	private final int id;

	@Override
	protected BufferedImage getBaseImage(Client client, SpriteManager spriteManager, CursorTheme theme)
	{
		if (client.getSpriteOverrides().containsKey(id))
		{
			return client.getSpriteOverrides().get(id).toBufferedImage();
		}
		else
		{
			return spriteManager.getSprite(id, 0);
		}
	}

	@Override
	public CursorType getType()
	{
		return CursorType.CONTEXTUAL;
	}
}
