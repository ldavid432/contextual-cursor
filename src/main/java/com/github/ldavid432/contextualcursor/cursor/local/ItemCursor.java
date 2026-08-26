package com.github.ldavid432.contextualcursor.cursor.local;

import com.github.ldavid432.contextualcursor.cursor.Cursor;
import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.hasAllOf;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.hasAnyOf;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.targetContains;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.targetEndsWith;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import static com.github.ldavid432.contextualcursor.sprite.Sprite.cacheSprite;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.runelite.api.MenuEntry;
import net.runelite.api.gameval.SpriteID;

/**
 * All ItemCursor matchers implictly have the use option, and have an item widget selected.
 */
@AllArgsConstructor
public enum ItemCursor implements Cursor
{
	EXPLORERS_RING_LOW(cacheSprite().id(SpriteID.Magicon.LOW_LEVEL_ALCHEMY).build(),
		hasAllOf(targetContains(" -> explorer's Ring "),
			hasAnyOf(targetEndsWith("1"), targetEndsWith("2"), targetEndsWith("3"))
		)),
	EXPLORERS_RING_HIGH(cacheSprite().id(SpriteID.Magicon.HIGH_LEVEL_ALCHEMY).build(),
		targetEndsWith(" -> explorer's ring 4")),

	PRAYER_ALTAR(cacheSprite().id(SpriteID.Staticons.PRAYER).build(),
		hasAllOf(targetContains("bones -> "),
			hasAnyOf(targetEndsWith(" -> chaos altar"), targetEndsWith(" -> gilded altar")))
	);

	@NonNull
	@Getter
	private final Sprite sprite;
	@Getter
	private final MenuEntryMatcher matcher;


	@Override
	public Sprite getSprite(MenuEntry menuEntry)
	{
		return sprite;
	}

	@Override
	public void clearImage()
	{
		sprite.clearImage();
	}
}
