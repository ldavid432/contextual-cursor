package com.github.ldavid432.contextualcursor;

import com.github.ldavid432.contextualcursor.cursor.OffsetCursor;
import static io.hydrox.contextualcursor.ContextualCursorWorkerOverlay.BLANK_MOUSE;
import java.awt.Cursor;
import javax.annotation.Nullable;
import lombok.Value;
import net.runelite.client.ui.overlay.tooltip.Tooltip;

@Value(staticConstructor = "state")
public class ContextualCursorState {
	// actual cursor
	@Nullable
	Cursor cursor;

	@Nullable
	OffsetCursor cursorBackground;

	// The offset here is technically the center
	@Nullable
	OffsetCursor cursorForeground;

	@Nullable
	Tooltip tooltip;

	public static ContextualCursorState genericCursor(Cursor cursor)
	{
		return state(cursor, null, null, null);
	}

	public static ContextualCursorState genericCursorOverlay(OffsetCursor cursor, Tooltip tooltip)
	{
		return state(BLANK_MOUSE, cursor, null, tooltip);
	}

	public static ContextualCursorState externalCursor(Cursor cursor)
	{
		return state(cursor, null, null, null);
	}

	public static ContextualCursorState clearCursor()
	{
		return genericCursor(null);
	}

	public static ContextualCursorState contextualCursor(OffsetCursor foreground, OffsetCursor background, Tooltip tooltip)
	{
		return state(BLANK_MOUSE, background, foreground, tooltip);
	}

	// Contextual cursor with background disabled and cursor overlay disabled
	public static ContextualCursorState contextualCursorHybrid(OffsetCursor foreground, Cursor cursor)
	{
		return state(cursor, null, foreground, null);
	}

	@Override
	public String toString()
	{
		return "ContextualCursorState{" +
			textOrEmpty("cursor=", cursor != null ? cursor.getName() : null) +
			textOrEmpty(", cursorBackground=", cursorBackground) +
			textOrEmpty(", cursorForeground=", cursorForeground) +
			textOrEmpty(", tooltip=", tooltip) +
			'}';
	}

	private String textOrEmpty(String prefix, Object value)
	{
		return value != null ? prefix + value : "";
	}
}
