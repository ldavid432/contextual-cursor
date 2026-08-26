package com.github.ldavid432.contextualcursor.cursor;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;

@Getter
@AllArgsConstructor
public class ContextualCursorDefinition
{
	@With
	private final List<? extends Cursor> cursors;

	@With
	private final List<? extends Cursor> itemCursors;

	@With
	private final OffsetCursor defaultCursor;

	@With
	private final OffsetCursor backgroundCursor;

	@With
	private final ScaledPoint foregroundCursorCenter;

	public void clearImages()
	{
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
		foregroundCursorCenter.updateScale(scale);
	}
}
