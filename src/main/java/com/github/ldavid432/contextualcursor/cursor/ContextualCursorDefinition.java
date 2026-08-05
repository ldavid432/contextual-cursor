package com.github.ldavid432.contextualcursor.cursor;

import java.util.List;
import lombok.Value;
import lombok.With;
import net.runelite.api.Point;

@Value
public class ContextualCursorDefinition
{
	@With
	List<? extends Cursor> cursors;

	@With
	OffsetCursor defaultCursor;

	@With
	OffsetCursor backgroundCursor;

	@With
	Point foregroundCursorCenter;

	public void clearImages() {
		for (Cursor cursor : getCursors())
		{
			cursor.clearImage();
		}
		defaultCursor.getSprite().clearImage();
		backgroundCursor.getSprite().clearImage();
	}

	public void updateScale(double scale)
	{
		defaultCursor.updateScale(scale);
		backgroundCursor.updateScale(scale);
	}
}
