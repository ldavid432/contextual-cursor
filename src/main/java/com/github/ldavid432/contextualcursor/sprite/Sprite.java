package com.github.ldavid432.contextualcursor.sprite;

import com.github.ldavid432.contextualcursor.config.CursorTheme;
import io.hydrox.contextualcursor.ContextualCursorPlugin;
import java.awt.image.BufferedImage;
import javax.annotation.Nullable;
import net.runelite.api.Client;
import net.runelite.client.game.SpriteManager;

public interface Sprite
{
	@Nullable
	BufferedImage getImage(Client client, SpriteManager spriteManager, CursorTheme theme, double scale, boolean isSmoothScaling);

	@Nullable
	default BufferedImage getImage(Client client, SpriteManager spriteManager, ContextualCursorPlugin plugin) {
		return getImage(client, spriteManager, plugin.getCursorTheme(), plugin.getCursorScale(), plugin.isSmoothScalingEnabled());
	}

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
