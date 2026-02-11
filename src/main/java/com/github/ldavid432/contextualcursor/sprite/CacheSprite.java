package com.github.ldavid432.contextualcursor.sprite;

import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.scaleImage;
import com.github.ldavid432.contextualcursor.config.CursorTheme;
import java.awt.image.BufferedImage;
import javax.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.client.game.SpriteManager;

@EqualsAndHashCode(of = "id")
@RequiredArgsConstructor
public class CacheSprite implements Sprite
{
	private final int id;

	@Nullable
	BufferedImage image;

	@Override
	public BufferedImage getImage(final Client client, final SpriteManager spriteManager, CursorTheme theme,
								  double scale, boolean isSmoothScaling)
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

			image = scaleImage(image, scale, isSmoothScaling);
		}

		return image;
	}

	@Override
	public void clearImage()
	{
		image = null;
	}

	@Override
	public boolean isFullCursor()
	{
		return false;
	}
}
