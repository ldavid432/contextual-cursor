package com.github.ldavid432.contextualcursor.sprite;

import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.flipImage;
import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.scaleImage;
import java.awt.image.BufferedImage;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Base class that handles caching the image as well as image rotation and scaling. Also houses the common fields
 */
@SuperBuilder
@EqualsAndHashCode
public abstract class BaseSprite implements Sprite
{
	@Nullable
	protected transient BufferedImage image;

	@Builder.Default
	@Setter
	private boolean isInverted = false;

	@Override
	public final void clearImage()
	{
		image = null;
	}

	protected abstract BufferedImage getBaseImage(SpriteContext context);

	protected double getScale(SpriteContext context)
	{
		return context.getCursorScale();
	}

	@Override
	public final BufferedImage getImage(SpriteContext context)
	{
		if (image == null)
		{
			image = getBaseImage(context);

			image = scaleImage(image, getScale(context), context.isSmoothScalingEnabled());

			if (isInverted)
			{
				image = flipImage(image);
			}
		}

		return image;
	}

	@SuppressWarnings("unused")
	public static abstract class BaseSpriteBuilder<C extends BaseSprite, B extends BaseSprite.BaseSpriteBuilder<C, B>>
	{
		// Drop image builder function
		private B image(BufferedImage image)
		{
			return self();
		}
	}
}
