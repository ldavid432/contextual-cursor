package com.github.ldavid432.contextualcursor.sprite;

import com.github.ldavid432.contextualcursor.config.CursorTheme;
import java.awt.image.BufferedImage;
import javax.annotation.Nullable;
import net.runelite.api.Client;
import net.runelite.client.game.SpriteManager;

public interface Sprite
{
	@Nullable
	BufferedImage getImage(Client client, SpriteManager spriteManager, CursorTheme theme);

	void clearImage();

	boolean isFullCursor();

	static Sprite of(int id)
	{
		return new CacheSprite(id);
	}

	static Sprite of(String path)
	{
		return new ResourceSprite(path);
	}

	static Sprite of(String path, boolean isFullCursor)
	{
		return new ResourceSprite(path, isFullCursor);
	}
}
