package com.github.ldavid432.contextualcursor.cursor;

import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import lombok.Getter;
import net.runelite.api.MenuEntry;

public class JsonCursor implements Cursor
{
	private Sprite sprite;
	@Getter
	private MenuEntryMatcher matcher;

	@Override
	public Sprite getSprite(MenuEntry menuEntry)
	{
		// For now, we will just support static sprites with JSON.
		// Dynamic ones like the spellbook or item cursors will stay in Java.
		return sprite;
	}

	@Override
	public void clearImage()
	{
		sprite.clearImage();
	}
}
