package com.github.ldavid432.contextualcursor.cursor;

import com.github.ldavid432.contextualcursor.sprite.Sprite;
import javax.inject.Singleton;
import lombok.Setter;
import lombok.experimental.Delegate;
import net.runelite.api.MenuEntry;

@Singleton
public class CursorProvider
{
	@Setter
	@Delegate
	private ContextualCursorDefinition definition;

	public Sprite getSprite(MenuEntry menuEntry)
	{
		for (Cursor cursor : getCursors())
		{
			if (cursor.getMatcher().matches(menuEntry))
			{
				return cursor.getSprite(menuEntry);
			}
		}
		return null;
	}

	public void clearImages() {
		for (Cursor cursor : getCursors())
		{
			cursor.clearImage();
		}
		getDefaultCursorSprite().clearImage();
		getBackgroundCursorSprite().clearImage();
	}
}
