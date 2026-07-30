package com.github.ldavid432.contextualcursor.overlay;

import com.github.ldavid432.contextualcursor.cursor.CursorProvider;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.MenuEntry;

// TODO: This is really small, is it needed?
@Singleton
public class SpriteProvider
{
	@Inject
	private CursorProvider cursorProvider;

	public Sprite getSprite(MenuEntry menuEntry, MenuEntry lastSubmenuEntry, boolean isSubMenu)
	{
		Sprite sprite = cursorProvider.getSprite(menuEntry);

		// If we don't have a cursor for the submenu entry then use the parent cursor
		if (sprite == null && isSubMenu && lastSubmenuEntry != null)
		{
			sprite = getSprite(lastSubmenuEntry, null, false);
		}
		else
		{
			sprite = Objects.requireNonNullElse(sprite, cursorProvider.getDefaultCursorSprite());
		}

		return sprite;
	}
}
