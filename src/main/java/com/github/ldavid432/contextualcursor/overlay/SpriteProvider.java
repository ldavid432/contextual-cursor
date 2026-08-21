package com.github.ldavid432.contextualcursor.overlay;

import com.github.ldavid432.contextualcursor.ContextualCursorCache;
import com.github.ldavid432.contextualcursor.config.CursorBackgroundMode;
import com.github.ldavid432.contextualcursor.cursor.CursorProvider;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.runelite.api.MenuEntry;

// TODO: This is really small, is it needed?
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SpriteProvider
{
	private final CursorProvider cursorProvider;
	private final ContextualCursorCache cache;

	@Nullable
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
			if (sprite == null && cache.getCursorBackgroundMode() == CursorBackgroundMode.FOR_ANY_ACTION)
			{
				sprite = cursorProvider.getDefaultCursor().getSprite();
			}
		}

		return sprite;
	}
}
