package com.github.ldavid432.contextualcursor.cursor;

import com.github.ldavid432.contextualcursor.sprite.Sprite;
import java.util.List;
import javax.inject.Singleton;
import lombok.Setter;
import net.runelite.api.MenuEntry;

@Singleton
public class CursorProvider
{
	@Setter
	public List<? extends Cursor> cursors;

	public Sprite getSprite(MenuEntry menuEntry)
	{
		for (Cursor cursor : cursors)
		{
			if (cursor.getMatcher().matches(menuEntry))
			{
				return cursor.getSprite(menuEntry);
			}
		}
		return null;
	}
}
