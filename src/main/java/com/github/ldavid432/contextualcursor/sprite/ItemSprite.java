package com.github.ldavid432.contextualcursor.sprite;

import java.awt.image.BufferedImage;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@EqualsAndHashCode(of = {"id", "quantity"}, callSuper = true)
public class ItemSprite extends BaseSprite
{

	private final int id;

	@Builder.Default
	private final int quantity = 1;

	@Override
	protected double getScale(SpriteContext context)
	{
		return context.getItemScale();
	}

	@Override
	protected BufferedImage getBaseImage(SpriteContext context)
	{
		return context.getItemManager().getImage(id, quantity, false);
	}

}
