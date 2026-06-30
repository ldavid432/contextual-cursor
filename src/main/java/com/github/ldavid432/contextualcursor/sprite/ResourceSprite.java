package com.github.ldavid432.contextualcursor.sprite;

import java.awt.image.BufferedImage;
import javax.annotation.Nonnull;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@EqualsAndHashCode(of = "fileName", callSuper = true)
public class ResourceSprite extends BaseSprite
{
	@Nonnull
	private final String fileName;

	@Override
	protected BufferedImage getBaseImage(SpriteContext context)
	{
		return context.getTheme().getImage(fileName);
	}

}
