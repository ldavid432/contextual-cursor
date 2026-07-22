package com.github.ldavid432.contextualcursor.cursor;

import com.github.ldavid432.contextualcursor.sprite.Sprite;
import java.util.List;
import javax.annotation.Nullable;
import lombok.Value;
import lombok.With;

@Value
public class ContextualCursorDefinition
{
	@With
	List<? extends Cursor> cursors;

	Sprite defaultCursorSprite;

	@Nullable
	Sprite backgroundCursorSprite;

	public void clearImages() {
		for (Cursor cursor : getCursors())
		{
			cursor.clearImage();
		}
		defaultCursorSprite.clearImage();
		if (backgroundCursorSprite != null)
		{
			backgroundCursorSprite.clearImage();
		}
	}
}
