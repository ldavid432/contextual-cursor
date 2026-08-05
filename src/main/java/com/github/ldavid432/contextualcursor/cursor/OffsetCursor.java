package com.github.ldavid432.contextualcursor.cursor;

import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.EMPTY_POINT;
import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.scalePoint;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Point;

@RequiredArgsConstructor
public class OffsetCursor
{
	@Getter
	private final Sprite sprite;
	@NonNull
	private final Point offset;

	@NonNull
	private transient Point scaledOffset = EMPTY_POINT;

	public OffsetCursor(Sprite sprite)
	{
		this.sprite = sprite;
		this.offset = EMPTY_POINT;
	}

	public Point getOffset()
	{
		return scaledOffset;
	}

	public void updateScale(double scale)
	{
		scaledOffset = scalePoint(offset, scale);
	}
}
