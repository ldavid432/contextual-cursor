package com.github.ldavid432.contextualcursor.serialization;

import com.github.ldavid432.contextualcursor.cursor.Cursor;
import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class JsonCursor implements Cursor
{
	@Getter
	private Sprite sprite;
	@Getter
	private MenuEntryMatcher matcher;
}
