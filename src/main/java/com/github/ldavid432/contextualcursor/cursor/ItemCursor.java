package com.github.ldavid432.contextualcursor.cursor;

import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.hasAllOf;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.hasOption;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isWidgetTarget;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import net.runelite.api.MenuEntry;

public class ItemCursor implements Cursor
{
	@Override
	public Sprite getSprite(MenuEntry menuEntry)
	{
		return null;
	}

	@Override
	public MenuEntryMatcher getMatcher()
	{
		return hasAllOf(isWidgetTarget(), hasOption("use"));
	}
}
