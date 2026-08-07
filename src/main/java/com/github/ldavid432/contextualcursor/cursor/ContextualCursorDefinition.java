package com.github.ldavid432.contextualcursor.cursor;

import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.EMPTY_POINT;
import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.scalePoint;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;
import net.runelite.api.Point;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ContextualCursorDefinition
{
	@With
	private final List<? extends Cursor> cursors;

	@With
	private final OffsetCursor defaultCursor;

	@With
	private final OffsetCursor backgroundCursor;

	@With
	private final Point foregroundCursorCenter;

	private transient Point scaledForegroundCursorCenter = EMPTY_POINT;

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
		scaledForegroundCursorCenter = scalePoint(foregroundCursorCenter, scale);
	}

	public Point getForegroundCursorCenter()
	{
		return scaledForegroundCursorCenter;
	}
}
