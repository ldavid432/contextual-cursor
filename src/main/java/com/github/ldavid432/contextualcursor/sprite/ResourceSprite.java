package com.github.ldavid432.contextualcursor.sprite;

import com.google.common.annotations.VisibleForTesting;
import java.awt.image.BufferedImage;
import javax.annotation.Nonnull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@EqualsAndHashCode(of = "fileName", callSuper = true)
public class ResourceSprite extends BaseSprite
{
	@Getter(onMethod_ = @VisibleForTesting)
	@Nonnull
	private final String fileName;

	@Override
	protected BufferedImage getBaseImage(SpriteContext context)
	{
		return context.getTheme().getImage(fileName);
	}

	@Override
	public String getType()
	{
		return "resource";
	}
}
