package com.github.ldavid432.contextualcursor.cache;

import com.github.ldavid432.contextualcursor.ContextualCursorCache;
import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.flipImage;
import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.loadImage;
import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.scaleImage;
import com.github.ldavid432.contextualcursor.sprite.CacheSprite;
import com.github.ldavid432.contextualcursor.sprite.ItemSprite;
import com.github.ldavid432.contextualcursor.sprite.ResourceSprite;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.awt.image.BufferedImage;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;

@AllArgsConstructor
public class ImageCache extends CacheLoader<Sprite, BufferedImage> implements LoadingCache<Sprite, BufferedImage>
{
	private final ContextualCursorCache configCache;
	private final Client client;
	private final ItemManager itemManager;
	private final SpriteManager spriteManager;

	@Delegate
	private final LoadingCache<Sprite, BufferedImage> spriteCache = CacheBuilder
		.newBuilder()
		.maximumSize(64)
		.build(this);

	@Nullable
	private BufferedImage getBaseImage(Sprite sprite)
	{
		if (sprite instanceof CacheSprite)
		{
			int id = ((CacheSprite) sprite).getId();
			if (client.getSpriteOverrides().containsKey(id))
			{
				return client.getSpriteOverrides().get(id).toBufferedImage();
			}
			else
			{
				return spriteManager.getSprite(id, 0);
			}
		}
		else if (sprite instanceof ResourceSprite)
		{
			return loadImage(((ResourceSprite) sprite).getFileName());
		}
		else if (sprite instanceof ItemSprite)
		{
			ItemSprite itemSprite = (ItemSprite) sprite;
			return itemManager.getImage(itemSprite.getId(), itemSprite.getQuantity(), false);
		}
		else
		{
			return null;
		}
	}

	private double getScale(Sprite sprite)
	{
		if (sprite instanceof ItemSprite)
		{
			return configCache.getItemScale();
		}
		return configCache.getCursorScale();
	}

	private boolean isSmoothScalingEnabled(Sprite sprite)
	{
		if (sprite instanceof ItemSprite)
		{
			return configCache.isItemSmoothScalingEnabled();
		}
		return configCache.isCursorSmoothScalingEnabled();
	}

	@Nullable
	@Override
	public BufferedImage load(@Nonnull Sprite sprite)
	{
		BufferedImage image = getBaseImage(sprite);

		image = scaleImage(image, getScale(sprite), isSmoothScalingEnabled(sprite));

		if (sprite.isInverted())
		{
			image = flipImage(image);
		}
		return image;
	}
}
