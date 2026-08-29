package com.github.ldavid432.contextualcursor.cursor;

import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import com.github.ldavid432.contextualcursor.sprite.Sprite;

// TODO: Potentially rename this to avoid clashes
public interface Cursor
{
	Sprite getSprite();

	MenuEntryMatcher getMatcher();
}
