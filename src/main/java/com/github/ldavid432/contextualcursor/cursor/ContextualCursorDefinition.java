package com.github.ldavid432.contextualcursor.cursor;

import com.github.ldavid432.contextualcursor.sprite.Sprite;
import java.util.List;
import lombok.Value;
import lombok.With;

@Value
public class ContextualCursorDefinition
{
	@With
	List<? extends Cursor> cursors;

	Sprite defaultCursorSprite;

	Sprite backgroundCursorSprite;
}
