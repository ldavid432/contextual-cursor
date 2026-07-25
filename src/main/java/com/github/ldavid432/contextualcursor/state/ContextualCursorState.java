package com.github.ldavid432.contextualcursor.state;

import com.github.ldavid432.contextualcursor.sprite.Sprite;
import static io.hydrox.contextualcursor.ContextualCursorWorkerOverlay.BLANK_MOUSE;
import java.awt.Cursor;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import net.runelite.client.ui.overlay.tooltip.Tooltip;

@Value
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ContextualCursorState {
	// actual cursor
	@Nullable
	Cursor cursor;

	boolean isExternalCursor;

	// cursor overlay
	@Nullable
	Sprite cursorSprite;

	@Nullable
	Sprite cursorBackground;

	@Nullable
	Sprite cursorForeground;

	@Nullable
	Tooltip tooltip;

	public static ContextualCursorState genericCursor(Cursor cursor)
	{
		return new ContextualCursorState(cursor, false, null, null, null, null);
	}

	public static ContextualCursorState genericCursorOverlay(Sprite sprite, Tooltip tooltip)
	{
		return new ContextualCursorState(BLANK_MOUSE, false, sprite, null, null, tooltip);
	}

	public static ContextualCursorState externalCursor(Cursor cursor)
	{
		return new ContextualCursorState(cursor, true, null, null, null, null);
	}

	public static ContextualCursorState clearCursor()
	{
		return genericCursor(null);
	}

	public static ContextualCursorState contextualCursor(Sprite foreground, Sprite background, Tooltip tooltip)
	{
		return new ContextualCursorState(BLANK_MOUSE, false, null, background, foreground, tooltip);
	}

	@Override
	public String toString()
	{
		return "AbstractContextualCursorState{" +
			textOrEmpty("cursor=", cursor != null ? cursor.getName() : null) +
			textOrEmpty(", isExternalCursor=", isExternalCursor ? true : null) +
			textOrEmpty(", cursorSprite=", cursorSprite) +
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
