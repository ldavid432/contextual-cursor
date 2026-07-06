package com.github.ldavid432.contextualcursor.sprite;

import java.awt.image.BufferedImage;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@EqualsAndHashCode(of = "id", callSuper = true)
public class CacheSprite extends BaseSprite
{

	private final int id;

	@Override
	protected BufferedImage getBaseImage(SpriteContext context)
	{
		if (context.getClient().getSpriteOverrides().containsKey(id))
		{
			return context.getClient().getSpriteOverrides().get(id).toBufferedImage();
		}
		else
		{
			return context.getSpriteManager().getSprite(id, 0);
		}
	}

}
