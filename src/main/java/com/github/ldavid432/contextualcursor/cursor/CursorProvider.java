package com.github.ldavid432.contextualcursor.cursor;

import com.github.ldavid432.contextualcursor.sprite.Sprite;
import javax.inject.Singleton;
import lombok.experimental.Delegate;
import net.runelite.api.MenuEntry;

@Singleton
public class CursorProvider
{
	@Delegate
	private ContextualCursorDefinition definition;

	public void setDefinition(ContextualCursorDefinition definition)
	{
		clearImages();
		this.definition = definition;
	}

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
}
