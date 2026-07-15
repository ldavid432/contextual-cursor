package com.github.ldavid432.contextualcursor.cursor;

import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.hasAllOf;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.hasOption;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isWidgetTarget;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.nonSerializable;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import static com.github.ldavid432.contextualcursor.sprite.Sprite.itemSprite;
import io.hydrox.contextualcursor.ContextualCursorPlugin;
import lombok.AllArgsConstructor;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;

@AllArgsConstructor
public class ItemCursor implements Cursor
{
	private Client client;
	private ContextualCursorPlugin plugin;

	@Override
	public Sprite getSprite(MenuEntry menuEntry)
	{
		if (client.getSelectedWidget() != null && client.getSelectedWidget().getItemId() > 0)
		{
			return itemSprite().id(client.getSelectedWidget().getItemId()).build();
		}

		return null;
	}

	@Override
	public MenuEntryMatcher getMatcher()
	{
		return hasAllOf(
			nonSerializable(e -> plugin.isShowUseItemCursorEnabled()),
			nonSerializable(e -> client.isWidgetSelected() && client.getSelectedWidget() != null && client.getSelectedWidget().getItemId() > 0),
			isWidgetTarget(),
			hasOption("use")
		);
	}

	@Override
	public void clearImage()
	{
		// Item sprites currently are not cached
	}
}
