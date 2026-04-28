package com.github.ldavid432.contextualcursor.sprite;

import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.flipImage;
import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.scaleImage;
import com.github.ldavid432.contextualcursor.config.CursorTheme;
import java.awt.image.BufferedImage;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.runelite.api.Client;
import net.runelite.client.game.SpriteManager;

/**
 * Base class that handles caching the image as well as image rotation and scaling. Also houses the common fields
 */
@SuperBuilder
public abstract class BaseSprite implements Sprite
{
	@Nullable
	protected BufferedImage image;

	@Builder.Default
	@Setter
	private boolean isInverted = false;

	@Builder.Default
	@Getter
	private final CursorType type = CursorType.CONTEXTUAL;

	@Override
	public final void clearImage()
	{
		image = null;
	}

	protected abstract BufferedImage getBaseImage(final Client client, final SpriteManager spriteManager, CursorTheme theme);

	@Override
	public final BufferedImage getImage(final Client client, final SpriteManager spriteManager, CursorTheme theme,
								  double scale, boolean isSmoothScaling)
	{
		if (image == null)
		{
			image = getBaseImage(client, spriteManager, theme);

			image = scaleImage(image, scale, isSmoothScaling);

			if (isInverted)
			{
				image = flipImage(image);
			}
		}

		return image;
	}

	@SuppressWarnings("unused")
	public static abstract class BaseSpriteBuilder<C extends BaseSprite, B extends BaseSprite.BaseSpriteBuilder<C, B>> {
		// Drop image builder function
		private B image(BufferedImage image){
			return self();
		}
	}
}
