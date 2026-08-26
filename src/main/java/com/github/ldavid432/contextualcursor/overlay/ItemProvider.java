package com.github.ldavid432.contextualcursor.overlay;

import com.github.ldavid432.contextualcursor.ContextualCursorCache;
import com.github.ldavid432.contextualcursor.cursor.Cursor;
import com.github.ldavid432.contextualcursor.cursor.CursorProvider;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.hasOption;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import static com.github.ldavid432.contextualcursor.sprite.Sprite.itemSprite;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AllArgsConstructor;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;

/**
 * Provides sprites for item interactions. Primarily the function of this class is to get triggered before SelectedWidgetProvider.
 * Prioritizes custom item interactions, if none apply returns the use interaction (if enabled).
 */
@Singleton
@AllArgsConstructor(onConstructor_ = @Inject)
public class ItemProvider
{
	private final Client client;
	private final CursorProvider cursorProvider;
	private final ContextualCursorCache cache;

	@Nullable
	public Sprite getItemSprite(@Nullable MenuEntry menuEntry)
	{
		if (menuEntry == null || !hasOption("use").matches(menuEntry) || getItemId(menuEntry) < 0)
		{
			return null;
		}

		for (Cursor cursor : cursorProvider.getItemCursors())
		{
			if (cursor.getMatcher().matches(menuEntry))
			{
				return cursor.getSprite(menuEntry);
			}
		}

		// Show the item being used, not the item being used on
		int itemId = getItemId(menuEntry);

		if (cache.isShowUseItemCursorEnabled())
		{
			// TODO: Caching
			return itemSprite().id(itemId).build();
		}
		else
		{
			return null;
		}
	}

	private Integer getItemId(MenuEntry menuEntry)
	{
		if (menuEntry != null && menuEntry.getItemId() >= 0)
		{
			return menuEntry.getItemId();
		}
		else if (client.isWidgetSelected() && client.getSelectedWidget() != null && client.getSelectedWidget().getItemId() >= 0)
		{
			return client.getSelectedWidget().getItemId();
		}
		else
		{
			return -1;
		}
	}
}
