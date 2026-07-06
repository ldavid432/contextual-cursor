package com.github.ldavid432.contextualcursor.sprite;

import java.awt.image.BufferedImage;
import javax.annotation.Nullable;

public interface Sprite
{
	@Nullable
	BufferedImage getImage(SpriteContext context);

	void clearImage();

	static ResourceSprite.ResourceSpriteBuilder<?, ?> resourceSprite()
	{
		return ResourceSprite.builder();
	}

	static CacheSprite.CacheSpriteBuilder<?, ?> cacheSprite()
	{
		return CacheSprite.builder();
	}

	static ItemSprite.ItemSpriteBuilder<?, ?> itemSprite()
	{
		return ItemSprite.builder();
	}
}
