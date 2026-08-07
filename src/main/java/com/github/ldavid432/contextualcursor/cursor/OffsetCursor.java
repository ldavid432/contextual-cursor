package com.github.ldavid432.contextualcursor.cursor;

import com.github.ldavid432.contextualcursor.sprite.Sprite;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode(of = {"sprite", "offset"})
@RequiredArgsConstructor
public class OffsetCursor
{
	@Getter
	private final Sprite sprite;
	@Getter
	@NonNull
	private final ScaledPoint offset;

	public void updateScale(double scale)
	{
		offset.updateScale(scale);
	}
}

