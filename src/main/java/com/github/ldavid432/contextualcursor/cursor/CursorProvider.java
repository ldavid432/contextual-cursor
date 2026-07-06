package com.github.ldavid432.contextualcursor.cursor;

import com.github.ldavid432.contextualcursor.sprite.Sprite;
import java.util.List;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.MenuEntry;

@Singleton
public class CursorProvider
{
	@Setter
	private List<? extends Cursor> cursors;

	@Setter
	@Getter
	private Sprite defaultCursorSprite;

	@Setter
	@Getter
	private Sprite backgroundCursorSprite;

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

	public void clearImages() {
		for (Cursor cursor : cursors)
		{
			cursor.clearImage();
		}
		defaultCursorSprite.clearImage();
		backgroundCursorSprite.clearImage();
	}
}
